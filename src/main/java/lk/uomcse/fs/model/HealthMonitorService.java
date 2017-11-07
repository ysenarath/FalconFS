package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Neighbour;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ListIterator;

/**
 * The {@code HealthMonitorService} class measures the health of neighbor nodes.
 * In every 3 seconds, the count of heartbeats will be calculated, so that
 * the health measure can be done by that.
 *
 * @author Dulanjaya Tennekoon
 * @since Phase1
 */
public class HealthMonitorService extends Thread {
    private final static Logger LOGGER = Logger.getLogger(HealthMonitorService.class.getName());

    /**
     * Neighbors are the neighbor-nodes of the self-node.
     */
    private List<Neighbour> neighbors;

    /**
     * Activation of the {@code HealthMonitorService}
     */
    private boolean pulseMeasuring = true;


    /**
     * Bootstrap Service
     */
    private BootstrapService bootstrapService;

    /**
     * Creates ne heartbeat object
     *
     * @param neighbors
     */
    public HealthMonitorService(List<Neighbour> neighbors, BootstrapService bootstrapService) {
        this.neighbors = neighbors;
        this.bootstrapService = bootstrapService;
    }

    /**
     * Measure the health of each neighbor
     */
    private void measureHealth() {
        boolean hasNoActiveNeighbors = true;
        for (final ListIterator<Neighbour> iterator = this.neighbors.listIterator(); iterator.hasNext(); ) {
            final Neighbour neighbor = iterator.next();
            neighbor.setHealth(neighbor.getPulseCount() * 10 / 5);
            LOGGER.debug(String.format("Neighbour %s health updated %d", neighbor.toString(), neighbor.getHealth()));
            if (neighbor.getHealth() != 0) {
                hasNoActiveNeighbors = false;
            }
            iterator.set(neighbor);
        }

        // If health of all neighbors become zero, bootstrapping is done
        if(hasNoActiveNeighbors && neighbors.size() > 0) {
            LOGGER.info("Bootstrapping");
            this.bootstrapService.bootstrap();
        }

    }

    /**
     * Starts the Monitor Thread
     */
    @Override
    public void run() {
        while (pulseMeasuring) {
            measureHealth();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Makes the {@code HealthMonitorService} up or down.
     *
     * @param activate true if up, false to down.
     */
    public void setPulseMeasuring(boolean activate) {
        this.pulseMeasuring = activate;
    }
}
