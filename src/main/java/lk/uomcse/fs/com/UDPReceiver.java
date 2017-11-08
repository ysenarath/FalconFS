package lk.uomcse.fs.com;

import com.google.common.collect.Queues;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.*;
import lk.uomcse.fs.utils.exceptions.InvalidFormatException;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;

public class UDPReceiver extends Receiver {
    private DatagramSocket socket;

    final BlockingQueue<IMessage> messages;


    /**
     * Creates the part of client that handles receives
     *
     * @param socket Datagram socket
     */
    public UDPReceiver(DatagramSocket socket) {
        super();
        this.socket = socket;
        this.messages = Queues.newLinkedBlockingDeque();
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
                long receivedTime = System.currentTimeMillis();
                InetSocketAddress socketAddress = (InetSocketAddress) packet.getSocketAddress();
                String ip = socketAddress.getAddress().getHostAddress();
                int port = socketAddress.getPort();
                Node sender = new Node(ip, port);
                IMessage p = null;
                try {
                    p = parseMessage(packet);
                } catch (InvalidFormatException ignore) {
                    // Ignore error message and continue
                    continue;
                }
                if (p == null) {
                    continue;
                }
                p.setSender(sender);
                p.setReceivedTime(receivedTime);
                messages.offer(p);
            } catch (IOException ignored) {
                // -- Retry
            }
        }
    }

    /**
     * Parses the message and returns the parsed object
     *
     * @param packet packet to be parsed
     * @return Message object after parsing
     */
    private IMessage parseMessage(DatagramPacket packet) throws InvalidFormatException {
        String data = new String(packet.getData(), 0, packet.getLength());
        String[] args = data.split(" ");
        if (args.length < 2) return null;
        String id = args[1];
        IMessage message = null;
        switch (id) {
            case HeartbeatPulse.ID:
                message = HeartbeatPulse.parse(data);
                break;
            case JoinRequest.ID:
                message = JoinRequest.parse(data);
                break;
            case JoinResponse.ID:
                message = JoinResponse.parse(data);
                break;
            case LeaveRequest.ID:
                message = LeaveRequest.parse(data);
                break;
            case LeaveResponse.ID:
                message = LeaveResponse.parse(data);
                break;
            case RegisterResponse.ID:
                message = RegisterResponse.parse(data);
                break;
            case SearchRequest.ID:
                message = SearchRequest.parse(data);
                break;
            case SearchResponse.ID:
                message = SearchResponse.parse(data);
                break;
            case UnregisterResponse.ID:
                message = UnregisterResponse.parse(data);
                break;
        }
        return message;
    }

    /**
     * Takes messages received from the queue
     *
     * @return received message
     * @throws InterruptedException Whether receive was interrupted
     */
    public IMessage receive() throws InterruptedException {
        return messages.take();
    }


}
