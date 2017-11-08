package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Neighbour;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.IMessage;
import lk.uomcse.fs.messages.IRequest;
import lk.uomcse.fs.messages.JoinRequest;
import lk.uomcse.fs.messages.JoinResponse;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class JoinService extends Thread {
    private final static Logger LOGGER = Logger.getLogger(JoinService.class.getName());

    private static final int MAX_RETRIES = 3;

    private boolean running;

    private final RequestHandler handler;

    private final Node current;

    private final List<Neighbour> neighbours;

    private RequestHandler.SenderType senderType;

    /**
     * Allocates Join service object.
     *
     * @param handler    Request handler
     * @param current    Current node running this join service
     * @param neighbours reference to neighbours
     */
    public JoinService(RequestHandler handler, Node current, List<Neighbour> neighbours, RequestHandler.SenderType senderType) {
        this.handler = handler;
        this.current = current;
        this.neighbours = neighbours;
        this.senderType = senderType;
    }

    /**
     * Thread function
     * Handles incoming join messages
     */
    @Override
    public void run() {
        running = true;
        LOGGER.trace(String.format("Starting join service for node at (%s:%d).", current.getIp(), current.getPort()));
        while (running) {
            // Get request
            JoinRequest request = (JoinRequest) this.handler.receiveMessage(JoinRequest.ID);
            // Send reply
            IMessage reply = new JoinResponse(true);
            LOGGER.info(String.format("Replying to join request: %s", reply.toString()));
            this.handler.sendMessage(request.getNode().getIp(), request.getNode().getPort(), reply, senderType);
            // Add joined neighbours
            Neighbour n = new Neighbour(request.getNode());
            onNeighbourJoin(n);
        }
    }

    /**
     * Joins to provided node and add it as a neighbour
     *
     * @param n a node to join
     * @return whether join request is success or not
     */
    public boolean join(Neighbour n) {
        IRequest jr = new JoinRequest(current);
        JoinResponse reply = null;
        int retries = 0;
        while (retries < MAX_RETRIES) {
            LOGGER.info(String.format("Requesting node(%s:%d) to join: %s", n.getNode().getIp(), n.getNode().getPort(), jr.toString()));
            handler.sendMessage(n.getNode().getIp(), n.getNode().getPort(), jr, senderType);
            LOGGER.debug("Waiting for receive message.");
            try {
                reply = (JoinResponse) handler.receiveMessage(JoinResponse.ID, 3);
                break;
            } catch (TimeoutException e) {
                retries++;
                if (retries == MAX_RETRIES) {
                    LOGGER.debug(String.format("Timeout reached. Unable to connect to node: %s [CANCEL_JOIN]", n.toString()));
                    LOGGER.info(String.format("Join request failed after attempting %d times", retries));
                    return false;
                } else
                    LOGGER.debug(String.format("Timeout reached. Unable to connect to node: %s [RETRYING]", n.toString()));
            }
        }
        if (reply == null) return false;
        LOGGER.info(String.format("Replied to join request: %s", reply.toString()));
        // Add neighbours if success or not.
        // Not success implies it has already registered that node
        onNeighbourJoin(n);
        return reply.isSuccess();
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
