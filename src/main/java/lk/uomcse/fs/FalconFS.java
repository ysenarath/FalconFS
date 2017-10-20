package lk.uomcse.fs;

import lk.uomcse.fs.model.BootstrapService;
import lk.uomcse.fs.entity.BootstrapServer;
import lk.uomcse.fs.model.JoinService;
import lk.uomcse.fs.model.QueryService;
import lk.uomcse.fs.model.RequestHandler;
import lk.uomcse.fs.entity.Node;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.*;

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
        this.queryService = new QueryService(handler, me, filenames, neighbours);
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
        this.queryService.start();
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
        queryService.search(keyword);
    }

    /**
     * Main Method
     *
     * @param args No args yet
     *             TODO: Change main to get server and client parameters from config file
     */
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BootstrapServer bc = null;
        label:
        while (true) {
            String cmd = sc.next();
            switch (cmd) {
                case "bs":
                    bc = new BootstrapServer(sc.next(), sc.nextInt());
                    break;
                case "fs":
                    if (bc == null) System.out.println("Please create the bootstrap server first.");
                    FalconFS fs = new FalconFS(sc.next(), sc.next(), sc.nextInt(), bc);
                    fs.start();
                    break;
                case "exit":
                    break label;
            }
        }
    }
}
