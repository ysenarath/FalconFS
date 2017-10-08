package lk.uomcse.fs;

import lk.uomcse.fs.model.BootstrapService;
import lk.uomcse.fs.entity.BootstrapServer;
import lk.uomcse.fs.model.JoinService;
import lk.uomcse.fs.model.QueryService;
import lk.uomcse.fs.model.RequestHandler;
import lk.uomcse.fs.entity.Node;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.ArrayList;
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

    private List<String> filenames;

    private Set<Node> neighbours;

    private RequestHandler handler;

    private BootstrapService bootstrapService;

    private JoinService joinService;

    private QueryService queryService;

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
        // TODO: Support concurrent changes!!
        this.neighbours = new HashSet<>();
        this.filenames = new ArrayList<>();
        this.handler = new RequestHandler(port);
        this.bootstrapService = new BootstrapService(handler, bootstrapServer);
        this.joinService = new JoinService(handler, me, neighbours);
        this.queryService = new QueryService(handler, me, filenames);
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
        // 3. Start accepting nodes
        this.joinService.start();
        LOGGER.trace("Listening to join messages.");
        // 4. start querying
        // while random keyword in keywords query(keyword)
    }

    /**
     * Connects with bootstrap server and joins to nodes provided
     */
    private void bootstrap() {
        List<Node> nodes = bootstrapService.register(name, me);
        nodes.forEach(joinService::join);
        LOGGER.info(String.format("Joined to nodes: %s by bootstrapping ", nodes.toString()));
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
