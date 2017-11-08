package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Neighbour;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.LeaveRequest;
import lk.uomcse.fs.messages.LeaveResponse;
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
        LOGGER.trace(String.format("Starting leave service for node at (%s:%d)", self.getIp(), self.getPort()));
        while (running) {
            LeaveRequest request = (LeaveRequest) this.handler.receiveMessage(LeaveRequest.ID);
            LOGGER.info(String.format("Processing leave request by %s", request.getNode()));
            Optional<Neighbour> optionalNode = neighbours.stream().filter(node -> node.getNode().equals(request.getNode())).findAny();
            if (optionalNode.isPresent()) {
                Neighbour n = optionalNode.get();
                n.setLeft(true);
                LeaveResponse response = new LeaveResponse(true);
                handler.sendMessage(n.getNode().getIp(), n.getNode().getPort(), response);
            } else {
                Node n = request.getNode();
                LeaveResponse response = new LeaveResponse(false);
                handler.sendMessage(n.getIp(), n.getPort(), response);
            }
        }
    }

    /**
     * Sends leave requests to neighbours and remove them from routing table if they are alive
     */
    public void leave() {
        neighbours.forEach(neighbour -> {
            LOGGER.info(String.format("Leaving neighbour %s", neighbour.getNode()));
            if (neighbour.getHealth() > 0) {
                LeaveRequest request = new LeaveRequest(self);
                handler.sendMessage(neighbour.getNode().getIp(), neighbour.getNode().getPort(), request);
                int retry = 0;
                LeaveResponse response;
                while (retry < MAX_RETRIES) {
                    try {
                        response = (LeaveResponse) handler.receiveMessage(LeaveResponse.ID, 1);
                        break;
                    } catch (TimeoutException e) {
                        retry++;
                    }
                }
                // TODO: LOG info
            } else {
                // TODO: LOG info
            }
            neighbour.setLeft(true);
        });
        // Assuming that all the nodes has received/ has already left the system
    }
}
