package lk.uomcse.fs;

import lk.uomcse.fs.model.*;
import lk.uomcse.fs.entity.BootstrapServer;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.utils.RequestFailedException;
import lk.uomcse.fs.view.FrameView;
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

    private static final int MAX_INDEX_SIZE = 100;

    private static final int MAX_NODE_QUEUE_LENTH = 10;

    private String name;

    private Node me;

    private List<String> filenames;

    private List<Node> neighbours;

    private RequestHandler handler;

    private BootstrapService bootstrapService;

    private JoinService joinService;

    private QueryService queryService;

    private CacheService cacheService;

    private HeartbeatService heartbeatService;

    private HealthMonitorService healthMonitorService;

    private PulseReceiverService pulseReceiverService;

    private Thread heartbeatServiceThread;

    private Thread pulseReceiverServiceThread;

    private Thread healthMonitorServiceThread;

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
        //cache of nodes
        this.cacheService = new CacheService(MAX_INDEX_SIZE, MAX_NODE_QUEUE_LENTH);
        // Services
        this.bootstrapService = new BootstrapService(handler, bootstrapServer);
        this.joinService = new JoinService(handler, me, neighbours);
        this.queryService = new QueryService(handler, me, filenames, neighbours, cacheService);
        // Heartbeat services
        this.heartbeatService = new HeartbeatService(handler, neighbours);
        this.pulseReceiverService = new PulseReceiverService(handler, neighbours);
        this.healthMonitorService = new HealthMonitorService(neighbours);
        // Heartbeat service threads {
        this.heartbeatServiceThread = new Thread(heartbeatService);
        this.pulseReceiverServiceThread = new Thread(pulseReceiverService);
        this.healthMonitorServiceThread = new Thread(healthMonitorService);
        // }

        Properties props = loadDataFromConfig();
        try {
            String fileData = props.getProperty("files");
            List<String> allFiles = Arrays.asList(fileData.trim().toLowerCase().split(","));
            Collections.shuffle(allFiles);
            Random random = new Random();
            int randomIndex = random.nextInt() % 2 + 4;
            filenames.addAll(allFiles.subList(0, randomIndex));
        } catch (NullPointerException e) {
            LOGGER.error("Unable to load default file names from Config File");
        }

        FrameView ui = new FrameView(this.me, (ArrayList<Node>) neighbours, queryService, (ArrayList<String>) filenames);
    }

    private Properties loadDataFromConfig() {
        Properties prop = new Properties();
        InputStream inputStream = FalconFS.class.getClassLoader().getResourceAsStream("config.properties");
        if (inputStream != null) {
            try {
                prop.load(inputStream);
            } catch (IOException e) {
                System.err.println("Property file 'config.properties' could not be loaded");
                return null;
            }
        } else {
            System.err.println("Property file 'config.properties' not found in the classpath");
            return null;
        }
        return prop;
    }

    /**
     * Starts the Falcon file system
     */
    public boolean start() {
        // 1. Start the listener - Blocking
        this.handler.start();
        LOGGER.trace("Request handler started.");
        // 2. Connect to neighbours (bootstrap + join)
        boolean bootstrapState = this.bootstrap();
        if (bootstrapState) {
            LOGGER.trace("Bootstrapping completed.");
            // 3. Start heartbeat service
            heartbeatServiceThread.start();
            pulseReceiverServiceThread.start();
            healthMonitorServiceThread.start();
            LOGGER.trace("Heartbeat service started.");
            // 4. Start accepting nodes
            this.joinService.start();
            LOGGER.trace("Listening to join messages.");
            // 5. start querying
            // while random keyword in keywords query(keyword)
            this.queryService.start();
            LOGGER.trace("Listening to query messages.");
        } else {
            LOGGER.trace("Bootstrap failed. Stopping started services.");
            this.handler.setRunning(false);
            LOGGER.trace("Stopped all started services.");
            return false;
        }
        return true;
    }

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
        } catch (RequestFailedException ex) {
            return false;
        }
        return true;
    }

    /**
     * Query and print results
     * ONLY FOR DEBUGGING: have to implement a way to output files received
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
        Properties prop = new Properties();
//        InputStream inputStream = FalconFS.class.getClassLoader().getResourceAsStream("config.properties");
        InputStream inputStream = new FileInputStream("./config.properties");

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
    }
}
