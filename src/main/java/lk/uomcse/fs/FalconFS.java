package lk.uomcse.fs;

import lk.uomcse.fs.model.BootstrapService;
import lk.uomcse.fs.entity.BootstrapServer;
import lk.uomcse.fs.model.JoinService;
import lk.uomcse.fs.model.QueryService;
import lk.uomcse.fs.model.RequestHandler;
import lk.uomcse.fs.entity.Node;
import org.apache.log4j.Logger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
        LOGGER.trace("Listening to query messages.");
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
     */
    public static void main(String[] args) {
        Properties prop = new Properties();
        InputStream inputStream = FalconFS.class.getClassLoader().getResourceAsStream("config.properties");
        if (inputStream != null) {
            try {
                prop.load(inputStream);
            } catch (IOException e) {
                System.err.println("Property file 'config.properties' could not be loaded");
                return;
            }
        } else {
            System.err.println("Property file 'config.properties' not found in the classpath");
            return;
        }
        BootstrapServer bc = new BootstrapServer(prop.getProperty("bs.ip"), Integer.parseInt(prop.getProperty("bs.port")));
        FalconFS fs = new FalconFS(prop.getProperty("fs.name"), prop.getProperty("fs.ip"), Integer.parseInt(prop.getProperty("fs.port")), bc);
        fs.start();

        fs.query("hello");
    }
}
