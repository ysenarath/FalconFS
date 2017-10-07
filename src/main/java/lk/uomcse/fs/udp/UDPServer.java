package lk.uomcse.fs.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public abstract class UDPServer implements Runnable {
    private final int port;

    private DatagramSocket socket;

    private byte[] buf = new byte[256];

    public UDPServer(int port) {
        this.port = port;
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            // e.printStackTrace();
            // TODO: Create custom exception
            throw new RuntimeException("Unable construct Datagram Socket.");
        }
    }

    public void run() {
        boolean running = true;
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                // e.printStackTrace();
                // TODO: Handle this
            }
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);
            String received = new String(packet.getData(), 0, packet.getLength());
            // TODO: Update this to our requirements
            if (received.equals("end")) {
                running = false;
                continue;
            }
            String msg = this.handleRequest(received);
            byte[] buf = msg.getBytes();
            DatagramPacket reply = new DatagramPacket(buf, buf.length, address, port);
            try {
                socket.send(reply);
            } catch (IOException e) {
                // e.printStackTrace();
                // TODO: Handle this
            }
        }
        socket.close();
    }

    /**
     * Package Private: handles reqests
     *
     * @param received
     * @return
     */
    protected abstract String handleRequest(String received);
}
