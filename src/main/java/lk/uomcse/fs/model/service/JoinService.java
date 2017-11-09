package lk.uomcse.fs.model.service;

import lk.uomcse.fs.entity.Neighbour;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.JoinRequest;
import lk.uomcse.fs.messages.JoinResponse;
import lk.uomcse.fs.model.RequestHandler;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class JoinService extends Thread {
    private final static Logger LOGGER = Logger.getLogger(JoinService.class.getName());

    private static final int MAX_RETRIES = 3;

    private final RequestHandler handler;

    private final Node current;

    private final List<Neighbour> neighbours;

    private boolean running;

    /**
     * Allocates Join service object.
     *
     * @param handler    Request handler
     * @param current    Current node running this join service
     * @param neighbours reference to neighbours
     */
    public JoinService(RequestHandler handler, Node current, List<Neighbour> neighbours) {
        this.handler = handler;
        this.current = current;
        this.neighbours = neighbours;
    }

    /**
     * Thread function
     * Handles incoming join messages
     */
    @Override
    public void run() {
        running = true;
        LOGGER.trace("Starting join service");
        while (running) {
            // Get request
            JoinRequest request = (JoinRequest) this.handler.receiveMessage(JoinRequest.ID);
            // Send reply
            JoinResponse reply = new JoinResponse(true);
            LOGGER.info(String.format("Replying to join request %s", request.getNode()));
            this.handler.sendMessage(request.getNode().getIp(), request.getNode().getPort(), reply, false);
            // Add joined neighbours
            Neighbour n = new Neighbour(request.getNode());
            onNeighbourJoin(n);
        }
        LOGGER.trace("Stopping join service");
    }

    /**
     * Joins to provided node and add it as a neighbour
     *
     * @param n a node to join
     * @return whether join request is success or not
     */
    public boolean join(Neighbour n) {
        JoinRequest request = new JoinRequest(current);
        int retries = 0;
        JoinResponse reply = null;
        while (retries < MAX_RETRIES) {
            handler.sendMessage(n.getNode().getIp(), n.getNode().getPort(), request, false);
            try {
                // NOTE: Join reply is always a success
                reply = (JoinResponse) handler.receiveMessage(JoinResponse.ID, 3);
                break;
            } catch (TimeoutException e) {
                retries++;
            }
        }
        if (reply == null) {
            LOGGER.info(String.format("Failed joining with node %s", n.getNode()));
            return false;
        } else {
            LOGGER.info(String.format("Successfully joined with node %s", n.getNode()));
        }
        // Add neighbours if success or not. Not success implies it has already registered that node.
        onNeighbourJoin(n);
        return true;
    }

    /**
     * onNeighbourJoin update/ add neighbour objects in this node
     *
     * @param n a neighbour
     */
    private void onNeighbourJoin(Neighbour n) {
        synchronized (neighbours) {
            // Do not add duplicates (behave like a set)
            Optional<Neighbour> neighbour = neighbours.stream().filter(t -> t.equals(n)).findAny();
            if (neighbour.isPresent())
                neighbour.get().setLeft(false);
            else
                neighbours.add(n);
            LOGGER.info(String.format("Joined to nodes: %s", neighbours.toString()));
        }
    }

    /**
     * Changes state of execution
     *
     * @param running whether to run/stop this thread
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
}
