package lk.uomcse.fs;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.model.*;
import lk.uomcse.fs.view.MainUI;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

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
     */
    public FalconFS(Configurations model) {
        this.name = model.getName();
        this.self = new Node(model.getAddress(), model.getPort());

        this.neighbours = new ArrayList<>();
        this.filenames = new ArrayList<>();
//        TODO - handle errors
        try {
            this.handler = new RequestHandler(model.getPort(), RequestHandler.CONNECTION_UDP);
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        // Services
        this.joinService = new JoinService(handler, self, neighbours);
        this.bootstrapService = new BootstrapService(handler, joinService, model.getBootstrapServer(), name, self);
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
        boolean state = bootstrapService.bootstrap();
        if (state) {
            // 3. Start heartbeat service
            heartbeatService.start();
            pulseReceiverService.start();
            healthMonitorService.start();
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
        MainUI mainUI = new MainUI(self, neighbours, queryService, filenames);
    }

    /**
     * Stops all services
     *
     * @return success status
     */
    public boolean stop() {
        this.bootstrapService.unregister();
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
}
