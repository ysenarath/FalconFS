package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.IMessage;
import lk.uomcse.fs.messages.IRequest;
import lk.uomcse.fs.messages.JoinRequest;
import lk.uomcse.fs.messages.JoinResponse;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeoutException;

public class JoinService extends Thread {
    private final static Logger LOGGER = Logger.getLogger(JoinService.class.getName());

    private boolean running;

    private final RequestHandler handler;

    private final Node current;

    private final List<Node> neighbours;

    private int joinRetries;

    /**
     * Allocates Join service object.
     *
     * @param handler    Request handler
     * @param current    Current node running this join service
     * @param neighbours reference to neighbours
     */
    public JoinService(RequestHandler handler, Node current, List<Node> neighbours) {
        this.handler = handler;
        this.current = current;
        this.neighbours = neighbours;
        this.joinRetries = 3;
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
            JoinRequest request = (JoinRequest) this.handler.receiveMessage(JoinRequest.ID);
            IMessage reply = new JoinResponse(true);
            LOGGER.info(String.format("Replying to join request: %s", reply.toString()));
            // Request handling section
            this.handler.sendMessage(request.getNode().getIp(), request.getNode().getPort(), reply);
            Node n = request.getNode();
            synchronized (neighbours) {
                // Do not add duplicates (behave like a set)
                if (!neighbours.contains(n)) {
                    neighbours.add(n);
                }
            }
            LOGGER.info(String.format("Node(%s:%d) is joined to nodes: %s", current.getIp(), current.getPort(), neighbours.toString()));
        }
    }

    /**
     * Joins to provided node and add it as a neighbour
     *
     * @param n a node to join
     * @return whether join request is success or not
     */
    public boolean join(Node n) {
        IRequest jr = new JoinRequest(current);
        JoinResponse reply = null;
        for (int i = 0; i < this.joinRetries; i++) {
            LOGGER.info(String.format("Requesting node(%s:%d) to join: %s", n.getIp(), n.getPort(), jr.toString()));
            handler.sendMessage(n.getIp(), n.getPort(), jr);
            LOGGER.debug("Waiting for receive message.");
            try {
                reply = (JoinResponse) handler.receiveMessage(JoinResponse.ID, 5);
                break;
            } catch (TimeoutException e) {
                if (i == this.joinRetries - 1) {
                    LOGGER.debug(String.format("Timeout reached. Unable to connect to node: %s [CANCEL_JOIN]", n.toString()));
                    LOGGER.info(String.format("Join request failed after attempting %d times", this.joinRetries));
                    return false;
                } else
                    LOGGER.debug(String.format("Timeout reached. Unable to connect to node: %s [RETRYING]", n.toString()));
            }
        }
        if (reply == null) return false;
        LOGGER.info(String.format("Replied to join request: %s", reply.toString()));
        // Add neighbours if success or not.
        // Not success implies it has already registered that node
        synchronized (neighbours) {
            // Do not add duplicates (behave like a set)
            if (!neighbours.contains(n))
                neighbours.add(n);
        }
        return reply.isSuccess();
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
