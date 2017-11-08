package lk.uomcse.fs.model;

import lk.uomcse.fs.com.RestReceiver;
import lk.uomcse.fs.com.RestSender;
import lk.uomcse.fs.com.UDPReceiver;
import lk.uomcse.fs.com.UDPSender;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.IMessage;
import lk.uomcse.fs.utils.DatagramSocketUtils;
import org.apache.catalina.LifecycleException;
import org.apache.log4j.Logger;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.concurrent.*;

public class RequestHandler extends Thread {

    public enum SenderType {UDP, REST}

    private final static Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    private DatagramSocket socket;

    private boolean running;

    private UDPSender udpSender;

    private UDPReceiver udpReceiver;

    private RestSender restSender;

    private RestReceiver restReceiver;

    private ConcurrentMap<String, BlockingQueue<IMessage>> handle;

    private SenderType senderType;

    /**
     * Constructor {{{{@link lk.uomcse.fs.messages.RegisterResponse}}}}
     * Used for webservice
     *
     * @param self self node
     * @param port port for UPD connection
     */
    public RequestHandler(Node self, int port) throws InstantiationException, LifecycleException {
        this.senderType = SenderType.REST;
        handle = new ConcurrentHashMap<>();
        this.restReceiver = new RestReceiver(self);
        this.restSender = new RestSender(self);
        initHandler(port);
        restReceiver.startWebServices(handle);
    }

    /**
     * Constructor {{{{@link lk.uomcse.fs.messages.RegisterResponse}}}}
     * Used for pure UDP connection
     *
     * @param self self Node
     */
    public RequestHandler(Node self) throws InstantiationException, LifecycleException {
        this.senderType = SenderType.UDP;
        handle = new ConcurrentHashMap<>();
        initHandler(self.getPort());
    }

    private void initHandler(int port) throws InstantiationException {
        try {
            socket = DatagramSocketUtils.getSocket(port);
        } catch (SocketException e) {
            throw new InstantiationException("Unable to create the socket. Try changing the IP:Port.");
        }
        this.udpReceiver = new UDPReceiver(socket);
        this.udpSender = new UDPSender(socket);
    }

    /**
     * Thread function
     */
    @Override
    public void run() {
        running = true;
        LOGGER.trace("Initializing request handler.");
        udpReceiver.start();
        udpSender.start();
        while (running) {
            try {
                IMessage message = udpReceiver.receive();
                LOGGER.debug(String.format("Received message: %s", message.getID()));
                String id = message.getID();
                handle.putIfAbsent(id, new LinkedBlockingQueue<>());
                BlockingQueue<IMessage> messages = handle.get(id);
                messages.add(message);
            } catch (InterruptedException e) {
                LOGGER.debug("Message receive interrupted. Retrying...");
            }
        }
        LOGGER.trace("Finalizing request handler.");
        try {
            stopServices(senderType);
        } catch (LifecycleException e) {//TODO handle error
            e.printStackTrace();
        }
    }

    /**
     * Requests given node
     *
     * @param ip      ip of the requested node
     * @param port    port of the requested node
     * @param request request
     */
    public void sendMessage(String ip, int port, IMessage request, boolean isBoostrap) {
        if (SenderType.UDP.equals(senderType) || isBoostrap) {
            udpSender.send(ip, port, request);
        } else if (SenderType.REST.equals(senderType)) {
            restSender.send(ip, port, request);
        } else {
            LOGGER.error(String.format("Try connection type %s or %s", SenderType.UDP, SenderType.REST));
        }
    }

    /**
     * Requests given node
     *
     * @param ip      ip of the requested node
     * @param port    port of the requested node
     * @param request request
     */
    public void sendOnlyUDP(String ip, int port, IMessage request) {
        udpSender.send(ip, port, request);
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
                LOGGER.debug(String.format("Waiting for message with ID: %s", id));
                message = messages.take();
                LOGGER.debug(String.format("Message with ID obtained: %s", id));
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
                LOGGER.debug(String.format("Waiting for message with ID: %s", id));
                message = messages.poll(timeout, TimeUnit.SECONDS);
                if (message == null) {
                    throw new TimeoutException("Message with given id not received.");
                }
                LOGGER.debug(String.format("Message with ID obtained: %s", id));
                break;
            } catch (InterruptedException e) {
                LOGGER.debug("Interrupted from getting a reply. Retrying...");
            }
        }
        return message;
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

    public void stopServices(SenderType senderType) throws LifecycleException {
        if (SenderType.REST.equals(senderType)) {
            restReceiver.stopWebService();
            restSender.stopWebSender();
        }

        this.restSender.setRunning(false);
        this.udpReceiver.setRunning(false);
        this.socket.close();

    }
}
