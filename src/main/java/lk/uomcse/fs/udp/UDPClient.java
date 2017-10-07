package lk.uomcse.fs.udp;

import java.io.IOException;
import java.net.*;

public abstract class UDPClient {
    private final String host;

    private final int port;

    private DatagramSocket socket;

    private InetAddress address;

    /**
     * Creates the client for a server at host:port
     *
     * @param host name/ip of the server
     * @param port port of the server
     */
    public UDPClient(String host, int port) {
        this.port = port;
        this.host = host;
    }

    /**
     * Opens a socket to host
     *
     * @return whether opening connection is success
     */
    public boolean open() {
        try {
            this.socket = new DatagramSocket();
        } catch (SocketException e) {
            // e.printStackTrace();
            // TODO: Create custom exception
            throw new RuntimeException("Unable construct Datagram Socket.");
        }
        try {
            this.address = InetAddress.getByName(this.host);
        } catch (UnknownHostException e) {
            // e.printStackTrace();
            // TODO: Create custom exception
            throw new RuntimeException("Unable construct InetAddress.");
        }
        return true;
    }

    /**
     * Requests server for the msg content
     * <p>
     * TODO: Handle timeouts
     *
     * @param msg a message to relay to the server
     * @return reply from the server
     */
    public String request(String msg) {
        byte[] buf = msg.getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, this.port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            // e.printStackTrace();
            // TODO: Handle correctly
            throw new RuntimeException("Problem in sending the packets.");
        }
        packet = new DatagramPacket(buf, buf.length);
        try {
            // Blocks until a message arrives
            // Stores the message inside the byte array of the DatagramPacket passed to it
            socket.receive(packet);
        } catch (IOException e) {
            // e.printStackTrace();
            // TODO: Handle correctly
            throw new RuntimeException("Problem in receiving the packets.");
        }
        return new String(packet.getData(), 0, packet.getLength());
    }

    /*
      TODO: Add other methods as needed
      ex: methods to send only
     */

    /**
     * Closes the socket
     */
    public void close() {
        socket.close();
    }
}
