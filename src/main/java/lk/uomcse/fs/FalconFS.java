package lk.uomcse.fs;

import lk.uomcse.fs.models.BootstrapClient;
import lk.uomcse.fs.models.RequestHandler;
import lk.uomcse.fs.models.Node;
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

    private BootstrapClient bc;

    private RequestHandler handler;

    /**
     * Imports file system requirements
     *
     * @param bc a bootstrap server
     */
    private FalconFS(String name, String ip, int port, BootstrapClient bc) {
        this.name = name;
        this.bc = bc;
        this.me = new Node(ip, port);
        this.handler = new RequestHandler(port);
        this.neighbours = new HashSet<>();
    }

    /**
     * Starts the Falcon file system
     */
    private void start() {
        // 1. Start the listener
        this.handler.start();
        // 2. Connect to neighbours (bootstrap + join)
        this.bootstrap();
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
        throw new NotImplementedException();
    }

    /**
     * Connects with bootstrap server and joins to nodes provided
     */
    private void bootstrap() {
        List<Node> nodes = bc.register(name, me);
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
        BootstrapClient bc = new BootstrapClient("localhost", 55555);
        FalconFS fs = new FalconFS("uom_cse", "localhost", 5555, bc);
        FalconFS fs2 = new FalconFS("uom_cse2", "localhost", 5556, bc);
        fs.start();
        fs2.start();
        fs.query("XXX");
    }
}
