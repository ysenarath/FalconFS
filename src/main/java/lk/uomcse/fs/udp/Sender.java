package lk.uomcse.fs.udp;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender extends Thread {
    private static final int MAX_RETRIES = 3;

    private boolean running;

    private DatagramSocket socket;

    private BlockingQueue<DatagramPacket> packets;

    /**
     * Creates the part of client that handles sends
     *
     * @param socket Datagram socket
     */
    public Sender(DatagramSocket socket) {
        this.packets = new LinkedBlockingQueue<>();
        this.running = false;
        this.socket = socket;
    }

    /**
     * Thread function
     */
    @Override
    public void run() {
        this.running = true;
        int retries;
        while (running) {
            DatagramPacket packet;
            try {
                packet = this.packets.take();
            } catch (InterruptedException e) {
                continue;
            }
            retries = 0;
            while (retries < MAX_RETRIES) {
                try {
                    this.socket.send(packet);
                    break;
                } catch (IOException e) {
                    retries++;
                }
            }
        }
    }

    public void send(DatagramPacket packet) {
        this.packets.add(packet);
    }

    public void setRunning(boolean running) {
        this.running = running;
        this.interrupt();
    }
}
