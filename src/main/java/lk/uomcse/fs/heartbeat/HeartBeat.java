package lk.uomcse.fs.heartbeat;

import lk.uomcse.fs.entity.Node;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code HeartBeat} class represents UDP Heartbeats.
 * Sends heartbeats to every neighbor in each {@code SLEEP_TIME}
 *
 * @author Dulanjaya Tennekoon
 * @see java.net.DatagramPacket
 * @see Node
 * @since Phase1
 * Todo   Exception Handling
 * Todo   Neighbor Removal Handling
 */
public class HeartBeat implements Runnable {

    /**
     * Neighbors are the neighbor-nodes of the self-node.
     */
    private List<Node> neighbors;

    /**
     * Pulse Rate of the Heart Beats.
     */
    private final int SLEEP_TIME = 1000;

    /**
     * Socket of the Self-Node.
     */
    private DatagramSocket mySocket;

    /**
     * Initializes a new {@code HeartBeat} object.
     * Note that heart beats are done from self node to routing nodes.
     *
     * @param mySocket Socket of the Self-Node
     */
    public HeartBeat(DatagramSocket mySocket) {
        this.mySocket = mySocket;
        this.neighbors = new ArrayList<>();
    }

    /**
     * Starts the heart beating thread.
     */
    @Override
    public void run() {
        this.sendPulses();
    }

    /**
     * Send heartbeat pulses to every neighbor.
     *
     */
    private void sendPulses() {
        InetAddress address;
        int port;
        byte[] pulse;

        while (true) {
            try {
                for (Node neighbor : neighbors) {
                    address = InetAddress.getByName(neighbor.getIp());
                    port = neighbor.getPort();
                    pulse = Protocol.getPulseData();
                    DatagramPacket packet = new DatagramPacket(pulse, pulse.length, address, port);
                    mySocket.send(packet);
                }
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Add new neighbors.
     *
     * @param neighbor New neighbor to be added.
     */
    public void addNeighbor(Node neighbor) {
        this.neighbors.add(neighbor);
    }
}