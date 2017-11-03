package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Packet;
import lk.uomcse.fs.messages.IMessage;
import lk.uomcse.fs.udp.Receiver;
import lk.uomcse.fs.udp.Sender;
import lk.uomcse.fs.utils.DatagramSocketUtils;
import org.apache.log4j.Logger;

import java.net.*;
import java.util.concurrent.*;

public class RequestHandler extends Thread {
    private final static Logger LOGGER = Logger.getLogger(RequestHandler.class.getName());

    private DatagramSocket socket;

    private boolean running;

    private final Receiver receiver;

    private final Sender sender;

    private final ConcurrentMap<String, BlockingQueue<Packet>> handle;

    /**
     * Constructor {{{{@link lk.uomcse.fs.messages.RegisterResponse}}}}
     *
     * @param port port of this node
     */
    public RequestHandler(int port) {
        socket = DatagramSocketUtils.getSocket(port);
        this.receiver = new Receiver(socket);
        this.sender = new Sender(socket);
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
                Packet packet = receiver.receive();
                String receivedStr = packet.getMessage();
                LOGGER.debug(String.format("Received packet: %s", receivedStr));
                String[] data = receivedStr.split(" ");
                String id;
                // Message should be at least contain 2 space separated strings
                if (data.length >= 2) {
                    id = data[1];
                    handle.putIfAbsent(id, new LinkedBlockingQueue<>());
                    BlockingQueue<Packet> packets = handle.get(id);
                    packets.add(packet);
                } // else { ignore }
            } catch (InterruptedException e) {
                LOGGER.debug("Packet receive interrupted. Retrying...");
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
        byte[] buf = request.toString().getBytes();
        InetAddress address;
        try {
            address = InetAddress.getByName(ip);
        } catch (UnknownHostException e) {
            // TODO: Create custom exception + handle correctly
            throw new RuntimeException("The IP address of a host could not be determined.");
        }
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, port);
        sender.send(packet);
    }

    /**
     * Gets reply for reply ID if exists or waits until there is a reply
     *
     * @param id reply id (see protocol specs)
     * @return reply as String
     */
    public String receiveMessage(String id) {
        Packet packet = receivePacket(id);
        return packet.getMessage();
    }

    /**
     * Gets reply for reply ID if exists or waits until there is a reply
     *
     * @param id reply id (see protocol specs)
     * @return reply as String
     */
    public String receiveMessage(String id, int timeout) throws TimeoutException {
        handle.putIfAbsent(id, new LinkedBlockingQueue<>());
        BlockingQueue<Packet> packets = handle.get(id);
        Packet packet;
        try {
            LOGGER.debug(String.format("Waiting for message with ID: %s", id));
            packet = packets.poll(timeout, TimeUnit.SECONDS);
            if (packet == null) {
                throw new TimeoutException("Packed with given id not received.");
            }
            LOGGER.debug(String.format("Message with ID obtained: %s", id));
        } catch (InterruptedException e) {
            // TODO: change following exception
            throw new RuntimeException("Interrupted from getting a reply.");
        }
        return packet.getMessage();
    }

    /**
     * Gets reply for reply ID if exists or waits until there is a reply
     *
     * @param id reply id (see protocol specs)
     * @return reply as packet
     */
    public Packet receivePacket(String id) {
        handle.putIfAbsent(id, new LinkedBlockingQueue<>());
        BlockingQueue<Packet> packets = handle.get(id);
        Packet packet;
        try {
            LOGGER.debug(String.format("Waiting for message with ID: %s", id));
            packet = packets.take();
            LOGGER.debug(String.format("Message with ID obtained: %s", id));
        } catch (InterruptedException e) {
            // TODO: change following exception
            throw new RuntimeException("Interrupted from getting a reply.");
        }
        return packet;
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
