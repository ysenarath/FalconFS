package lk.uomcse.fs.entity;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;


public class UDPMessage extends Message {
    private DatagramPacket packet;

    public UDPMessage(DatagramPacket packet) {
        super();
        this.packet = packet;
        String ip = ((InetSocketAddress)packet.getSocketAddress()).getHostName();
        int port = ((InetSocketAddress)packet.getSocketAddress()).getPort();
        receiverNode = new Node(ip, port);
    }

    public DatagramPacket getPacket() {
        return packet;
    }

    @Override
    public String getMessage() {
        return new String(packet.getData(), 0, packet.getLength());
    }
}
