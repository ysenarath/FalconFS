package lk.uomcse.fs;

import lk.uomcse.fs.model.*;
import lk.uomcse.fs.entity.BootstrapServer;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.utils.FrameUtils;
import lk.uomcse.fs.utils.ListUtils;
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

    private Node self;

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
        this.self = new Node(ip, port);
        this.neighbours = new ArrayList<>();
        this.filenames = new ArrayList<>();
        this.handler = new RequestHandler(port);
        // Services {
        this.joinService = new JoinService(handler, self, neighbours);
        this.bootstrapService = new BootstrapService(handler, joinService, bootstrapServer);
        this.queryService = new QueryService(handler, self, filenames, neighbours);
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
        boolean state = bootstrapService.bootstrap(name, self);
        if (state) {
            // 3. Start heartbeat service
            this.heartbeatService.start();
            this.pulseReceiverService.start();
            this.healthMonitorService.start();
            // 4. Start accepting nodes
            this.joinService.start();
            // 5. start query service
            this.queryService.start();
        } else {
            this.handler.setRunning(false);
            // TODO: Request user to enter Name(IP:Port) and update config properties
            // TODO: Retry: start() with new parameters
            // TODO: Cancel: show following message
            LOGGER.error("Bootstrap failed. Stopping request handler.");
            // TODO: Show error message box with above message
            return;
        }
//        FrameView ui = new FrameView(this.self, (ArrayList<Node>) neighbours, queryService, (ArrayList<String>) filenames);
        MainUI ui1 = new MainUI(this.self, neighbours, queryService, filenames);
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
        FrameUtils.setLookAndFeel("Darcula");
        Properties props = new Properties();
        InputStream inputStream = FalconFS.class.getClassLoader().getResourceAsStream("config.properties");
        if (inputStream == null)
            try {
                String configPath = "./config.properties";
                if (args.length >= 1) {
                    configPath = args[0];
                    System.out.println(String.format("Taking '%s' as path to configuration.", configPath));
                    // TODO: Show ok/default message box with above message
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
                // TODO: Show error message box with above message
                return;
            }
        } else {
            System.err.println("Please provide path to configurations after the name of application.");
            // TODO: Show error message box with above message
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
