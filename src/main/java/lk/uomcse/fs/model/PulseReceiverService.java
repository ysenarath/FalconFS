package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.entity.Packet;
import lk.uomcse.fs.messages.HeartbeatPulse;

import java.util.List;
import java.util.ListIterator;

/**
 * @author Dulanjaya
 * @since 10/23/2017
 */
public class PulseReceiverService implements Runnable{
    private List<Node> neighbors;

    private RequestHandler requestHandler;

    private boolean isActive = true;

    public PulseReceiverService(RequestHandler requestHandler, List<Node> neighbors) {
        this.neighbors = neighbors;
        this.requestHandler = requestHandler;
    }


    @Override
    public void run() {
        while (isActive) {
            receivePulses();
        }
    }

    private void receivePulses() {
        Packet packet = this.requestHandler.receivePacket(HeartbeatPulse.ID);
        for (final ListIterator<Node> iterator = this.neighbors.listIterator(); iterator.hasNext(); ) {
            final Node neighbor = iterator.next();
            if (neighbor.equals(packet.getReceiverNode())) {

                iterator.set(neighbor);
            }

        }
    }
}
