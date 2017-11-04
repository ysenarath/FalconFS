package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.entity.Packet;
import lk.uomcse.fs.messages.HeartbeatPulse;
import org.apache.log4j.Logger;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.ListIterator;

/**
 * The class {@code PulseReceiverService} updates the received time
 * of pulses from respective neighbors.
 *
 * @author Dulanjaya
 * @see Node
 * @see RequestHandler
 * @since Phase1
 */
public class PulseReceiverService extends Thread {
    private final static Logger LOGGER = Logger.getLogger(BootstrapService.class.getName());

    // -----------------------------------------------------------------------------------------------------------------

    /**
     * List of {@code neighbors}.
     */
    private List<Node> neighbors;

    /**
     * Message Request Handler.
     */
    private RequestHandler requestHandler;


    /**
     * Represents whether the service is up or down.
     */
    private boolean isActive = true;

    /**
     * Constructor of {@code PulseReceiverService}.
     *
     * @param requestHandler RequestHandler of the self-node.
     * @param neighbors      List of neighbors of the self-node.
     */
    public PulseReceiverService(RequestHandler requestHandler, List<Node> neighbors) {
        this.neighbors = neighbors;
        this.requestHandler = requestHandler;
    }

    /**
     * Receives Pulses in a separate thread.
     */
    @Override
    public void run() {
        while (isActive) {
            receivePulses();
        }
    }

    /**
     * Receives pulses from each neighbor and update the respective node.
     */
    private void receivePulses() {
        Packet packet = this.requestHandler.receivePacket(HeartbeatPulse.ID);
        try {
            InetAddress packetAddress = InetAddress.getByName(packet.getReceiverNode().getIp());
            for (final ListIterator<Node> iterator = this.neighbors.listIterator(); iterator.hasNext(); ) {
                final Node neighbor = iterator.next();
                InetAddress addressNeighbor = InetAddress.getByName(neighbor.getIp());
                if (addressNeighbor.equals(packetAddress) && neighbor.getPort() == packet.getReceiverNode().getPort()) {
                    neighbor.addPulseResponse(packet.getReceivedTime());
                    iterator.set(neighbor);
                }
            }
        } catch (UnknownHostException e) {
            LOGGER.error(e);
        }
    }

    /**
     * Makes the {@code PulseReceiverService} up or down.
     *
     * @param isActive true to up, false to down.
     */
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
