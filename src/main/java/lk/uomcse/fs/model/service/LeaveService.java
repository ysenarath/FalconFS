package lk.uomcse.fs.model.service;

import lk.uomcse.fs.model.entity.Neighbour;
import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.messages.LeaveRequest;
import lk.uomcse.fs.model.messages.LeaveResponse;
import lk.uomcse.fs.model.RequestHandler;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeoutException;

public class LeaveService extends Thread {
    private final static Logger LOGGER = Logger.getLogger(LeaveService.class.getName());

    private final static int MAX_RETRIES = 3;

    private boolean running;

    private Node self;

    private RequestHandler handler;

    private List<Neighbour> neighbours;


    /**
     * Constructor
     */
    public LeaveService(RequestHandler handler, Node self, List<Neighbour> neighbours) {
        this.self = self;
        this.handler = handler;
        this.neighbours = neighbours;
        this.running = false;
    }

    /**
     * Thread function
     * Handles incoming leave messages
     */
    @Override
    public void run() {
        running = true;
        LOGGER.trace("Starting leave service");
        while (running) {
            LeaveRequest request = (LeaveRequest) this.handler.receiveMessage(LeaveRequest.ID);
            LOGGER.info(String.format("Processing leave request by %s", request.getNode()));
            Optional<Neighbour> optionalNode = neighbours.stream().filter(node -> node.getNode().equals(request.getNode())).findAny();
            if (optionalNode.isPresent()) {
                Neighbour n = optionalNode.get();
                n.setLeft(true);
                LeaveResponse response = new LeaveResponse(true);
                handler.sendMessage(n.getNode().getIp(), n.getNode().getPort(), response, false);
            } else {
                Node n = request.getNode();
                LeaveResponse response = new LeaveResponse(false);
                handler.sendMessage(n.getIp(), n.getPort(), response, false);
            }
        }
        LOGGER.trace("Stopping leave service");
    }

    /**
     * Sends leave requests to neighbours and remove them from routing table if they are alive
     */
    public void leave() {
        neighbours.forEach(n -> {
            if (n.getHealth() > 0) {
                LeaveRequest request = new LeaveRequest(self);
                handler.sendMessage(n.getNode().getIp(), n.getNode().getPort(), request, false);
                int retries = 0;
                LeaveResponse response = null;
                while (retries < MAX_RETRIES) {
                    try {
                        response = (LeaveResponse) handler.receiveMessage(LeaveResponse.ID, 1);
                        break;
                    } catch (TimeoutException e) {
                        retries++;
                    }
                }
                if (response != null && response.isSuccess()) {
                    LOGGER.info(String.format("Successfully left the node %s", n.getNode()));
                } else {
                    LOGGER.info(String.format("Failed leaving the node the node %s", n.getNode()));
                }
            }
            n.setLeft(true);
        });
        // Assuming that all the nodes has received/ has already left the system
    }

    /**
     * Gets whether the node is running
     *
     * @return whether the node is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Sets run status
     *
     * @param running update run status
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
}
