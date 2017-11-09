package lk.uomcse.fs.model;

import lk.uomcse.fs.model.com.RestReceiver;
import lk.uomcse.fs.model.com.RestSender;
import lk.uomcse.fs.model.com.UDPReceiver;
import lk.uomcse.fs.model.com.UDPSender;
import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.messages.IMessage;
import lk.uomcse.fs.utils.DatagramSocketUtils;
import lk.uomcse.fs.utils.exceptions.InitializationException;
import org.apache.catalina.LifecycleException;
import org.apache.log4j.Logger;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.*;

public class RequestHandler extends Thread {
    private final static Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    private DatagramSocket socket;

    private boolean running;

    private UDPSender udpSender;

    private UDPReceiver udpReceiver;

    private RestSender restSender;

    private RestReceiver restReceiver;

    private ConcurrentMap<String, BlockingQueue<IMessage>> handle;

    private Protocol protocol;

    /**
     * Constructor {{{{@link lk.uomcse.fs.model.RequestHandler}}}}
     * Used for webservice
     *
     * @param self    self node
     * @param udpPort udp port for bootstrapping
     * @throws InitializationException thrown when there is an exception in initialization of this node
     */
    public RequestHandler(Node self, int udpPort) throws InitializationException {
        protocol = Protocol.REST;
        restReceiver = new RestReceiver(self);
        restSender = new RestSender(self);
        initialize(udpPort);
    }


    /**
     * Constructor {{{{@link lk.uomcse.fs.model.RequestHandler}}}}
     * Used for pure UDP connection
     *
     * @param udpPort UDP port
     * @throws InitializationException thrown when there is an exception in initialization of this node
     */
    public RequestHandler(int udpPort) throws InitializationException {
        protocol = Protocol.UDP;
        initialize(udpPort);
    }

    /**
     * Initializes request handler
     *
     * @param udpPort UDP port
     * @throws InitializationException thrown when exception is thrown in initializing request handler
     */
    private void initialize(int udpPort) throws InitializationException {
        handle = new ConcurrentHashMap<>();
        if (protocol == Protocol.REST) {
            try {
                restReceiver.startWebServices(handle);
            } catch (Exception e) {
                try {
                    if (restReceiver != null)
                        restReceiver.stopWebService();
                } catch (LifecycleException ignore) {
                    LOGGER.debug("Ignoring lifecycle error when trying to stop the web server");
                    // Ignore
                }
                throw new InitializationException("Unable to start web services. Tomcat may need different port.");
            }
        }
        try {
            socket = DatagramSocketUtils.getSocket(udpPort);
        } catch (SocketException e) {
            if (socket != null)
                socket.close();
            if (restReceiver != null)
                try {
                    restReceiver.stopWebService();
                } catch (LifecycleException ignore) {
                    LOGGER.debug("Ignoring lifecycle error when trying to stop the web server");
                    // Ignore
                }
            throw new InitializationException("Unable to create the socket. Try changing the port for this application.");
        }
        udpReceiver = new UDPReceiver(socket);
        udpSender = new UDPSender(socket);
        udpReceiver.start();
        udpSender.start();
    }

    /**
     * Thread function
     */
    @Override
    public void run() {
        running = true;
        LOGGER.trace("Starting request handler");
        while (running) {
            try {
                IMessage message = udpReceiver.receive();
                LOGGER.debug(String.format("Message with ID = %s received", message.getID()));
                String id = message.getID();
                handle.putIfAbsent(id, new LinkedBlockingQueue<>());
                BlockingQueue<IMessage> messages = handle.get(id);
                messages.add(message);
            } catch (InterruptedException e) {
                LOGGER.debug("Message receive interrupted. Retrying...");
            }
        }
        LOGGER.trace("Stopping request handler.");
        stopServices();
    }

    /**
     * Requests given node
     *
     * @param ip      ip of the requested node
     * @param port    port of the requested node
     * @param request request
     */
    public void sendMessage(String ip, int port, IMessage request, boolean isBootstrap) {
        if (Protocol.UDP.equals(protocol) || isBootstrap) {
            udpSender.send(ip, port, request);
        } else if (Protocol.REST.equals(protocol)) {
            restSender.send(ip, port, request);
        } else {
            LOGGER.error(String.format("Try connection type %s or %s", Protocol.UDP, Protocol.REST));
        }
    }

    /**
     * Gets reply for reply ID if exists or waits until there is a reply
     *
     * @param id reply id (see protocol specs)
     * @return reply as String
     */
    public IMessage receiveMessage(String id) {
        handle.putIfAbsent(id, new LinkedBlockingQueue<>());
        BlockingQueue<IMessage> messages = handle.get(id);
        IMessage message = null;
        while (running) {
            try {
                LOGGER.debug(String.format("Waiting for message with ID = %s", id));
                message = messages.take();
                LOGGER.debug(String.format("Message with ID obtained = %s", id));
                break;
            } catch (InterruptedException e) {
                LOGGER.debug("Interrupted from getting a reply. Retrying...");
            }
        }
        return message;
    }

    /**
     * Gets reply for reply ID if exists or waits until there is a reply
     *
     * @param id reply id (see protocol specs)
     * @return reply as IMessage
     */
    public IMessage receiveMessage(String id, int timeout) throws TimeoutException {
        handle.putIfAbsent(id, new LinkedBlockingQueue<>());
        BlockingQueue<IMessage> messages = handle.get(id);
        IMessage message = null;
        while (running) {
            try {
                LOGGER.debug(String.format("Waiting for message with ID = %s", id));
                message = messages.poll(timeout, TimeUnit.SECONDS);
                if (message == null) {
                    String error = String.format("Request timeout for ID = %s", id);
                    LOGGER.debug(error);
                    throw new TimeoutException(error);
                }
                LOGGER.debug(String.format("Message with ID obtained = %s", id));
                break;
            } catch (InterruptedException e) {
                LOGGER.debug("Interrupted from getting a reply. Retrying...");
            }
        }
        return message;
    }

    /**
     * Stops all services used by request handler
     */
    private void stopServices() {
        if (Protocol.REST.equals(protocol)) {
            try {
                restReceiver.stopWebService();
            } catch (LifecycleException e) {
                LOGGER.debug("Failed to stop web services. Ignoring and stopping other services.");
            }
            restSender.stopWebSender();
            restSender.setRunning(false);
        }
        this.udpSender.setRunning(false);
        this.udpReceiver.setRunning(false);
        this.socket.close();
    }

    /**
     * Set running status
     *
     * @param running state
     */
    public void setRunning(boolean running) {
        this.running = running;
        this.interrupt();
    }
}
