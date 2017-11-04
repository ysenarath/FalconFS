package lk.uomcse.fs.model;

import lk.uomcse.fs.com.*;
import lk.uomcse.fs.entity.Message;
import lk.uomcse.fs.entity.UDPMessage;
import lk.uomcse.fs.messages.IMessage;
import lk.uomcse.fs.utils.DatagramSocketUtils;
import org.apache.log4j.Logger;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.*;

public class RequestHandler extends Thread {

    public static final String CONNECTION_UDP = "udp";

    public static final String CONNECTION_REST = "rest";

    private final static Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    private DatagramSocket socket;

    private boolean running;

    private final Receiver receiver;

    private final Sender sender;

    private final ConcurrentMap<String, BlockingQueue<Message>> handle;

    /**
     * Constructor {{{{@link lk.uomcse.fs.messages.RegisterResponse}}}}
     *
     * @param port port of this node
     */
    public RequestHandler(int port, String connectionType) throws InstantiationException {
        if (CONNECTION_UDP.equals(connectionType)) {
            socket = DatagramSocketUtils.getSocket(port);
            this.receiver = new UDPReceiver(socket);
            this.sender = new UDPSender(socket);
        } else if (CONNECTION_REST.equals(connectionType)) {
            this.receiver = new RestReceiver();
            this.sender = new RestSender();
        } else {
//            TODO handle errors
            throw new InstantiationException(String.format("Provide connection type either %s or %s",
                    CONNECTION_REST, CONNECTION_UDP));
        }
        handle = new ConcurrentHashMap<>();
    }

    /**
     * Thread function
     */
    @Override
    public void run() {
        running = true;
        LOGGER.trace("Initializing request handler.");
        receiver.start();
        sender.start();
        while (running) {
            try {
                Message message = receiver.receive();
                String receivedStr = message.getMessage();
                LOGGER.debug(String.format("Received message: %s", receivedStr));
                String[] data = receivedStr.split(" ");
                String id;
                // Message should be at least contain 2 space separated strings
                if (data.length >= 2) {
                    id = data[1];
                    handle.putIfAbsent(id, new LinkedBlockingQueue<>());
                    BlockingQueue<Message> messages = handle.get(id);
                    messages.add(message);
                } // else { ignore }
            } catch (InterruptedException e) {
                LOGGER.debug("UDPMessage receive interrupted. Retrying...");
            }
        }
        LOGGER.trace("Finalizing request handler.");
        this.sender.setRunning(false);
        this.receiver.setRunning(false);
        this.socket.close();
    }

    /**
     * Requests given node
     *
     * @param ip      ip of the requested node
     * @param port    port of the requested node
     * @param request request
     */
    public void sendMessage(String ip, int port, IMessage request) {
        sender.send(ip, port, request);
    }

    /**
     * Gets reply for reply ID if exists or waits until there is a reply
     *
     * @param id reply id (see protocol specs)
     * @return reply as String
     */
    public String receiveMessage(String id) {
        Message message = receivePacket(id);
        return message.getMessage();
    }

    /**
     * Gets reply for reply ID if exists or waits until there is a reply
     *
     * @param id reply id (see protocol specs)
     * @return reply as String
     */
    public String receiveMessage(String id, int timeout) throws TimeoutException {
        handle.putIfAbsent(id, new LinkedBlockingQueue<>());
        BlockingQueue<Message> messages = handle.get(id);
        Message message;
        try {
            LOGGER.debug(String.format("Waiting for message with ID: %s", id));
            message = messages.poll(timeout, TimeUnit.SECONDS);
            if (message == null) {
                throw new TimeoutException("Message with given id not received.");
            }
            LOGGER.debug(String.format("Message with ID obtained: %s", id));
        } catch (InterruptedException e) {
            // TODO: change following exception
            throw new RuntimeException("Interrupted from getting a reply.");
        }
        return message.getMessage();
    }

    /**
     * Gets reply for reply ID if exists or waits until there is a reply
     *
     * @param id reply id (see protocol specs)
     * @return reply as packet
     */
    public Message receivePacket(String id) {
        handle.putIfAbsent(id, new LinkedBlockingQueue<>());
        BlockingQueue<Message> messages = handle.get(id);
        Message message;
        try {
            LOGGER.debug(String.format("Waiting for message with ID: %s", id));
            message = messages.take();
            LOGGER.debug(String.format("Message with ID obtained: %s", id));
        } catch (InterruptedException e) {
            // TODO: change following exception
            throw new RuntimeException("Interrupted from getting a reply.");
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
}
