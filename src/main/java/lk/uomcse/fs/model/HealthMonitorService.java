package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Node;

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
public class HealthMonitorService implements Runnable {
    private List<Node> neighbors;
    private boolean pulseMeasuring = true;


    /**
     * Creates ne heartbeat object
     *
     * @param neighbors
     */
    public HealthMonitorService(List<Node> neighbors) {
        this.neighbors = neighbors;
    }

    /**
     * Measure the health of each neighbor
     */
    private void measureHealth() {
        for (final ListIterator<Node> iterator = this.neighbors.listIterator(); iterator.hasNext(); ) {
            final Node neighbor = iterator.next();
            neighbor.setHealth((int) (neighbor.getHealth() / 3));
            iterator.set(neighbor);
        }
    }


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

    public void setPulseMeasuring(boolean activate) {
        this.pulseMeasuring = activate;
    }
}
