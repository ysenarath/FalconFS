package lk.uomcse.fs.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

public class UDPReceiver extends Thread {
    private boolean running = true;

    private DatagramSocket socket;

    private BlockingQueue<DatagramPacket> packets;

    public UDPReceiver(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException e) {
            // e.printStackTrace();
            // TODO: Create custom exception
            throw new RuntimeException("Unable construct Datagram Socket.");
        }
    }

    /**
     * Thread function
     */
    public void run() {
        byte[] buf = new byte[256];
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                // e.printStackTrace();
                // TODO: Handle this
            }
            packets.add(packet);
        }
        socket.close();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public BlockingQueue<DatagramPacket> getPackets() {
        return packets;
    }
}
