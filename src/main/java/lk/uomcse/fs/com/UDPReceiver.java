package lk.uomcse.fs.com;

import com.google.common.collect.Queues;
import lk.uomcse.fs.entity.UDPMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

public class UDPReceiver extends Receiver {
    private DatagramSocket socket;

    private final BlockingQueue<UDPMessage> UDPMessages;

    /**
     * Creates the part of client that handles receives
     *
     * @param socket Datagram socket
     */
    public UDPReceiver(DatagramSocket socket) {
        super();
        this.UDPMessages = Queues.newLinkedBlockingDeque();
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
                UDPMessage p = new UDPMessage(packet);
                UDPMessages.offer(p);
            } catch (IOException ignored) {
                // -- Retry
            }
        }
    }

    /**
     * Takes UDPMessages received from the queue
     *
     * @return received message
     * @throws InterruptedException Whether receive was interrupted
     */
    @Override
    public UDPMessage receive() throws InterruptedException {
        return UDPMessages.take();
    }
}
