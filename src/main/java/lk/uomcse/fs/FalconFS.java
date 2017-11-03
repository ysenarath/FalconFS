package lk.uomcse.fs;

import lk.uomcse.fs.model.*;
import lk.uomcse.fs.entity.BootstrapServer;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.utils.ListUtils;
import lk.uomcse.fs.utils.exceptions.BootstrapException;
import lk.uomcse.fs.utils.exceptions.RequestFailedException;
import lk.uomcse.fs.view.FrameView;
import lk.uomcse.fs.view.MainUI;
import org.apache.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Falcon File System
 */
public class FalconFS {
    private final static Logger LOGGER = Logger.getLogger(FalconFS.class.getName());

    private String name;

    private Node me;

    private List<String> filenames;

    private List<Node> neighbours;

    private RequestHandler handler;

    private BootstrapService bootstrapService;

    private JoinService joinService;

    private QueryService queryService;

    private HeartbeatService heartbeatService;

    private HealthMonitorService healthMonitorService;

    private PulseReceiverService pulseReceiverService;

    /**
     * Imports file system requirements
     *
     * @param name            name of this file server
     * @param ip              designated ip of this node
     * @param port            assigned port of this node
     * @param bootstrapServer a bootstrap server entity
     */
    public FalconFS(String name, String ip, int port, BootstrapServer bootstrapServer) {
        this.name = name;
        this.me = new Node(ip, port);
        this.neighbours = new ArrayList<>();
        this.filenames = new ArrayList<>();
        this.handler = new RequestHandler(port);
        // Services
        this.bootstrapService = new BootstrapService(handler, bootstrapServer);
        this.joinService = new JoinService(handler, me, neighbours);
        this.queryService = new QueryService(handler, me, filenames, neighbours);
        // Heartbeat services
        this.heartbeatService = new HeartbeatService(handler, neighbours);
        this.pulseReceiverService = new PulseReceiverService(handler, neighbours);
        this.healthMonitorService = new HealthMonitorService(neighbours);
        // }
    }

    /**
     * Starts the Falcon file system
     */
    public void start() {
        // 1. Start the listener - Blocking
        this.handler.start();
        // 2. Connect to neighbours (bootstrap + join)
        boolean bootstrapState = this.bootstrap();
        if (bootstrapState) {
            // 3. Start heartbeat service
            heartbeatService.start();
            pulseReceiverService.start();
            healthMonitorService.start();
            // 4. Start accepting nodes
            this.joinService.start();
            // 5. start query service
            this.queryService.start();
        } else {
            LOGGER.error("Bootstrap failed. Stopping request handler.");
            this.handler.setRunning(false);
            return;
        }
//        FrameView ui = new FrameView(this.me, (ArrayList<Node>) neighbours, queryService, (ArrayList<String>) filenames);
        MainUI ui1 = new MainUI(this.me, (ArrayList<Node>) neighbours, queryService, (ArrayList<String>) filenames);
    }

    /**
     * Stops all services
     *
     * @return success status
     */
    public boolean stop() {
        this.queryService.setRunning(false);
        this.joinService.setRunning(false);
        this.handler.setRunning(false);
        return true;
    }

    /**
     * Connects with bootstrap server and joins to nodes provided
     */
    private boolean bootstrap() {
        try {
            List<Node> nodes = bootstrapService.register(name, me);
            nodes.forEach(joinService::join);
            LOGGER.trace(String.format("Joined to neighbours: %s", neighbours.toString()));
        } catch (RequestFailedException | BootstrapException ex) {
            return false;
        }
        return true;
    }

    /**
     * Query and print results
     * CLI only function
     *
     * @param keyword a word/ series of continuous words in filename to query in the network
     */
    public void query(String keyword) {
        queryService.search(keyword);
    }


    /**
     * Gets list of file names
     *
     * @return list of filenames contained in this node
     */
    public List<String> getFilenames() {
        return filenames;
    }

    /**
     * Main Method
     *
     * @param args No args yet
     */
    public static void main(String[] args) throws FileNotFoundException {
        Properties props = new Properties();
        InputStream inputStream = FalconFS.class.getClassLoader().getResourceAsStream("config.properties");
        if (inputStream == null)
            try {
                String configPath = "./config.properties";
                if (args.length >= 1) {
                    configPath = args[0];
                    System.out.println(String.format("Taking '%s' as path to configurations.", configPath));
                }
                inputStream = new FileInputStream(configPath);
            } catch (FileNotFoundException ex) {
                inputStream = null;
            }
        if (inputStream != null) {
            try {
                props.load(inputStream);
            } catch (IOException e) {
                System.err.println("Property file 'config.properties' could not be loaded");
                return;
            }
        } else {
            System.err.println("Please provide path to configurations after the name of application.");
            return;
        }
        BootstrapServer bc = new BootstrapServer(props.getProperty("bs.ip"), Integer.parseInt(props.getProperty("bs.port")));
        FalconFS fs = new FalconFS(props.getProperty("fs.name"), props.getProperty("fs.ip"), Integer.parseInt(props.getProperty("fs.port")), bc);
        String filesStr = props.getProperty("files");
        List<String> files = Arrays.asList(filesStr.trim().toLowerCase().split(","));
        fs.getFilenames().addAll(ListUtils.randomSubList(files, 4, 2));
        fs.start();
    }
}
