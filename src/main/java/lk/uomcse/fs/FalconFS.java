package lk.uomcse.fs;

import lk.uomcse.fs.messages.IRequest;
import lk.uomcse.fs.messages.JoinRequest;
import lk.uomcse.fs.messages.JoinResponse;
import lk.uomcse.fs.model.BootstrapClient;
import lk.uomcse.fs.entity.BootstrapServer;
import lk.uomcse.fs.model.RequestHandler;
import lk.uomcse.fs.entity.Node;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Falcon File System
 */
public class FalconFS {
    private final static Logger LOGGER = Logger.getLogger(FalconFS.class.getName());

    private String name;

    private Node me;

    private Set<Node> neighbours;

    private BootstrapClient bootstrapClient;

    private RequestHandler handler;

    /**
     * Imports file system requirements
     *
     * @param name            name of this file server
     * @param ip              designated ip of this node
     * @param port            assigned port of this node
     * @param bootstrapServer a bootstrap server entity
     */
    private FalconFS(String name, String ip, int port, BootstrapServer bootstrapServer) {
        this.name = name;
        this.me = new Node(ip, port);
        this.handler = new RequestHandler(port);
        this.bootstrapClient = new BootstrapClient(bootstrapServer, this.handler);
        this.neighbours = new HashSet<>();
    }

    /**
     * Starts the Falcon file system
     */
    private void start() {
        // 1. Start the listener - Blocking
        this.handler.start();
        LOGGER.trace("Request handler started.");
        // 2. Connect to neighbours (bootstrap + join)
        this.bootstrap();
        LOGGER.trace("Bootstrapping completed.");
        // 3. start querying
        // while random keyword in keywords query(keyword)

    }

    /**
     * Joins to provided node and add it as a neighbour
     *
     * @param n a node to join
     */
    private void join(Node n) {
        neighbours.add(n);
//        IRequest jr = new JoinRequest(n);
//        handler.sendRequest(n.getIp(), n.getPort(), jr);
//        String reply = handler.receiveResponse(JoinResponse.ID);
//        JoinResponse rsp = JoinResponse.parse(reply);
//        if (rsp.isSuccess())
//            LOGGER.info("Successfully joined to node.");
    }

    /**
     * Connects with bootstrap server and joins to nodes provided
     */
    private void bootstrap() {
        List<Node> nodes = bootstrapClient.register(name, me);
        nodes.forEach(this::join);
        LOGGER.info(String.format("Joined to nodes: %s", nodes.toString()));
    }

    /**
     * Query and print results
     * ONLY FOR DEBUGGING: have to implement a way to output files received
     *
     * @param keyword a word/ series of continuous words in filename to query in the network
     */
    private void query(String keyword) {
        throw new NotImplementedException();
    }

    /**
     * Main Method
     *
     * @param args No args yet
     */
    public static void main(String[] args) {
        BootstrapServer bc = new BootstrapServer("localhost", 55555);
        FalconFS fs = new FalconFS("uom_cse", "localhost", 5555, bc);
        FalconFS fs2 = new FalconFS("uom_cse1", "localhost", 5556, bc);
        FalconFS fs3 = new FalconFS("uom_cse2", "localhost", 5557, bc);
        FalconFS fs4 = new FalconFS("uom_cse3", "localhost", 5558, bc);
        FalconFS fs5 = new FalconFS("uom_cse4", "localhost", 5559, bc);
        fs.start();
        fs2.start();
        fs3.start();
        fs4.start();
        fs5.start();
    }
}
