package lk.uomcse.fs.udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Receiver extends Thread {
    private boolean running;

    private DatagramSocket socket;

    private BlockingQueue<DatagramPacket> packets;

    /**
     * Creates the part of client that handles receives
     *
     * @param socket Datagram socket
     */
    public Receiver(DatagramSocket socket) {
        this.packets = new LinkedBlockingQueue<>();
        this.running = false;
        this.socket = socket;
    }

    /**
     * Thread function
     */
    public void run() {
        running = true;
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

    public DatagramPacket receive() throws InterruptedException {
        return packets.take();
    }
}
