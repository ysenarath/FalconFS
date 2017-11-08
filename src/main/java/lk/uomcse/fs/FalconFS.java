package lk.uomcse.fs;

import lk.uomcse.fs.entity.Neighbour;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.model.*;
import lk.uomcse.fs.utils.exceptions.BootstrapException;
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

    private List<Neighbour> neighbours;

    private RequestHandler handler;

    private BootstrapService bootstrapService;

    private LeaveService leaveService;

    private JoinService joinService;

    private QueryService queryService;

    private HeartbeatService heartbeatService;

    private HealthMonitorService healthMonitorService;

    private PulseReceiverService pulseReceiverService;

    private RequestHandler.SenderType senderType;

    /**
     * Imports file system requirements
     */
    public FalconFS(Configuration configs) throws InstantiationException {
        //TODO sender type set this using GUI
        this.senderType = configs.getSenderType();

        this.name = configs.getName();
        this.self = new Node(configs.getAddress(), configs.getPort());

        this.neighbours = new ArrayList<>();
        this.filenames = new ArrayList<>();
        this.handler = new RequestHandler(self);
        // Services
        this.leaveService = new LeaveService(handler, self, neighbours, senderType);
        this.joinService = new JoinService(handler, self, neighbours, senderType);
        this.bootstrapService = new BootstrapService(handler, joinService, leaveService, configs.getBootstrapServer(), name, self);
        this.queryService = new QueryService(handler, self, filenames, neighbours, senderType);
        // Heartbeat services
        this.heartbeatService = new HeartbeatService(handler, neighbours, senderType);
        this.pulseReceiverService = new PulseReceiverService(handler, neighbours);
        this.healthMonitorService = new HealthMonitorService(neighbours, bootstrapService);
    }

    /**
     * Starts the Falcon file system
     */
    public void start() throws BootstrapException {
        // 1. Start the listener - Blocking
        this.handler.start();
        // 2. Connect to neighbours (bootstrap + join)
        try {
            leaveService.start();
            bootstrapService.bootstrap();
            // 3. Start heartbeat service
            heartbeatService.start();
            pulseReceiverService.start();
            healthMonitorService.start();
            // 4. Start accepting nodes
            joinService.start();
            // 5. start query service
            queryService.start();
        } catch (BootstrapException e) {
            handler.setRunning(false);
            throw e;
        }
    }

    /**
     * Unregister from all nodes if possible (?)
     */
    public void stop() throws BootstrapException {
        this.bootstrapService.unregister();
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
     * Gets query service
     *
     * @return query service
     */
    public QueryService getQueryService() {
        return queryService;
    }

    /**
     * Gets neighbours
     *
     * @return neighbours list
     */
    public List<Neighbour> getNeighbours() {
        return neighbours;
    }

    /**
     * Gets self node
     *
     * @return self node
     */
    public Node getSelf() {
        return self;
    }

    public String getName() {
        return name;
    }
}
