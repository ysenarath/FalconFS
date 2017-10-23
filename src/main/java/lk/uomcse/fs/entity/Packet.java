package lk.uomcse.fs.entity;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author Dulanjaya
 * @since 10/23/2017
 */
public class Packet {
    private DatagramPacket packet;
    private long receivedTime;
    private Node receiverNode;

    public Packet(DatagramPacket packet) {
        this.packet = packet;
        this.receivedTime = System.currentTimeMillis();

        String ip = ((InetSocketAddress)packet.getSocketAddress()).getHostName();
        int port = ((InetSocketAddress)packet.getSocketAddress()).getPort();
        this.receiverNode = new Node(ip, port);
    }


    public DatagramPacket getPacket() {
        return packet;
    }

    public long getReceivedTime() {
        return receivedTime;
    }

    public String getMessage() {
        return new String(packet.getData(), 0, packet.getLength());
    }

    public Node getReceiverNode() {
        return this.receiverNode;
    }
}
