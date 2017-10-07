package lk.uomcse.fs.udp;

import java.io.IOException;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Sender extends Thread {
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
        while (running) {
            try {
                DatagramPacket packet = this.packets.take();
                this.socket.send(packet);
            } catch (InterruptedException e) {
                // --retry
            } catch (IOException e) {
                // TODO: Handle correctly
                throw new RuntimeException("Problem in sending the packets.");
            }
        }
        socket.close();
    }

    public void send(DatagramPacket packet) {
        this.packets.add(packet);
    }

    public void setRunning(boolean running) {
        this.running = running;
    }
}
