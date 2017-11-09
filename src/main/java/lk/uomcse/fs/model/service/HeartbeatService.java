package lk.uomcse.fs.model.service;

import lk.uomcse.fs.model.entity.Neighbour;
import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.messages.HeartbeatPulse;
import lk.uomcse.fs.model.RequestHandler;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * The {@code HeartbeatService} class represents UDP Heartbeats.
 * Sends heartbeats to every neighbor in each {@code SLEEP_TIME}
 *
 * @author Dulanjaya Tennekoon
 * @see Node
 * @see HeartbeatPulse
 * @see RequestHandler
 * @since Phase1
 */
public class HeartbeatService extends Thread {
    private final static Logger LOGGER = Logger.getLogger(HeartbeatService.class.getName());

    /**
     * Pulse Rate of the Heart Beats.
     */
    private int frequency = 1;


    /**
     * Neighbors are the neighbor-nodes of the self-node.
     */
    private List<Neighbour> neighbors;

    /**
     * Request Handler of the heartbeats.
     *
     * @see RequestHandler
     */
    private RequestHandler requestHandler;

    /**
     * Activation of the heartbeat
     */
    private boolean pulseBeating;

    /**
     * Heartbeat Pulse of each heartbeat
     *
     * @see HeartbeatPulse
     */
    private HeartbeatPulse pulse;


    /**
     * Initializes a new {@code HeartBeat} object.
     * Note that heart beats are done from self node to routing nodes.
     *
     * @param requestHandler requestHandler of the Self-Node
     * @param neighbors      Neighbors list of the Self-Node
     */
    public HeartbeatService(RequestHandler requestHandler, List<Neighbour> neighbors, int sleepTime) {
        this.requestHandler = requestHandler;
        this.neighbors = neighbors;
        this.pulse = new HeartbeatPulse();
        this.pulseBeating = true;
        this.frequency = sleepTime;
    }

    /**
     * Starts the heart beating thread.
     */
    @Override
    public void run() {
        while (this.pulseBeating) {
            this.sendPulses();
        }
    }

    /**
     * Send heartbeat pulses to every neighbor.
     */
    private void sendPulses() {
        try {
            for (Neighbour neighbor : neighbors) {
                if(neighbor.isLeft()) continue;
                LOGGER.debug(String.format("Sending Heartbeat Message to %s", neighbor.getNode()));
                this.requestHandler.sendMessage(neighbor.getNode().getIp(), neighbor.getNode().getPort(), this.pulse, false);
            }
            Thread.sleep(frequency * 1000);
        } catch (InterruptedException e) {
            LOGGER.error(e);
        }

    }

    /**
     * Makes the heart activated or de-activated.
     *
     * @param activate activates/deactivates heart beating.
     */
    public void setPulseBeating(boolean activate) {
        this.pulseBeating = activate;
    }
}
