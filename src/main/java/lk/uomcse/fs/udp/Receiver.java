package lk.uomcse.fs.udp;

import com.google.common.collect.Queues;
import lk.uomcse.fs.entity.Packet;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Receiver extends Thread {
    private boolean running;

    private DatagramSocket socket;

    private final BlockingQueue<Packet> packets;

    /**
     * Creates the part of client that handles receives
     *
     * @param socket Datagram socket
     */
    public Receiver(DatagramSocket socket) {
        this.packets = Queues.newLinkedBlockingDeque();
        this.running = false;
        this.socket = socket;
    }

    /**
     * Thread function
     */
    public void run() {
        running = true;
        while (running) {
            byte[] buf = new byte[65536];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
                Packet p = new Packet(packet);
                packets.offer(p);
            } catch (IOException ignored) {
                // -- Retry
            }
        }
    }

    /**
     * Takes packets received from the queue
     *
     * @return received message
     * @throws InterruptedException Whether receive was interrupted
     */
    public Packet receive() throws InterruptedException {
        return packets.take();
    }

    /**
     * Sets run status and interrupt current activities
     *
     * @param running value
     */
    public void setRunning(boolean running) {
        this.running = running;
        this.interrupt();
    }
}
