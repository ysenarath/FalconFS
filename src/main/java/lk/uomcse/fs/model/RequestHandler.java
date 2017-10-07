package lk.uomcse.fs.model;

import lk.uomcse.fs.messages.IRequest;
import lk.uomcse.fs.udp.Receiver;
import lk.uomcse.fs.udp.Sender;
import lk.uomcse.fs.utils.DatagramSocketUtils;

import java.net.*;
import java.util.concurrent.*;

public class RequestHandler extends Thread {
    private boolean running;

    private final Receiver receiver;

    private final Sender sender;

    private final ConcurrentMap<String, BlockingQueue<DatagramPacket>> handle;

    /**
     * Constructor {{{{@link lk.uomcse.fs.messages.RegisterResponse}}}}
     *
     * @param port port of this node
     */
    public RequestHandler(int port) {
        DatagramSocket socket = DatagramSocketUtils.getSocket(port);
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
        receiver.start();
        sender.start();
        while (running) {
            try {
                DatagramPacket packet = receiver.receive();
                String[] data = new String(packet.getData(), 0, packet.getLength()).split(" ");
                String id;
                // message should be at least contain 2 space separated strings
                if (data.length >= 2) {
                    id = data[1];
                    handle.putIfAbsent(id, new LinkedBlockingQueue<>());
                    BlockingQueue<DatagramPacket> packets = handle.get(id);
                    packets.add(packet);
                } // else ignore the message
            } catch (InterruptedException e) {
                // --retry
            }
        }
        this.sender.setRunning(false);
        this.receiver.setRunning(false);
    }

    /**
     * Requests given node
     *
     * @param ip      ip of the requested node
     * @param port    port of the requested node
     * @param request request
     */
    public void sendRequest(String ip, int port, IRequest request) {
        byte[] buf = request.toString().getBytes();
        InetAddress address = null;
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
    public String receiveResponse(String id) {
        handle.putIfAbsent(id, new LinkedBlockingQueue<>());
        BlockingQueue<DatagramPacket> packets = handle.get(id);
        DatagramPacket packet;
        try {
            // TODO: add a timeout
            packet = packets.take();
        } catch (InterruptedException e) {
            // TODO: change following exception
            throw new RuntimeException("Interrupted from getting a reply.");
        }
        return new String(packet.getData(), 0, packet.getLength());
    }
}
