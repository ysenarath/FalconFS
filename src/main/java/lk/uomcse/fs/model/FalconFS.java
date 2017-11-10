package lk.uomcse.fs.model;

import lk.uomcse.fs.model.entity.Neighbour;
import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.service.*;
import lk.uomcse.fs.utils.exceptions.BootstrapException;
import lk.uomcse.fs.utils.exceptions.InitializationException;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
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

    private Protocol protocol;

    private int hb_frequency;

    /**
     * Imports file system requirements
     */
    public FalconFS(Configuration configs) throws InitializationException {
        this.name = configs.getName();
        this.self = new Node(configs.getAddress(), configs.getPort());
        this.neighbours = Collections.synchronizedList(new ArrayList<>());
        this.filenames = new ArrayList<>();
        this.protocol = configs.getProtocol();
        // Only needed if sender type is REST
        if (protocol == Protocol.REST) {
            this.handler = new RequestHandler(self, configs.getBootstrapPort());
            this.hb_frequency = 2;
        } else {
            this.handler = new RequestHandler(configs.getPort());
            this.hb_frequency = 1;
        }
        // Services
        this.leaveService = new LeaveService(handler, self, neighbours);
        this.joinService = new JoinService(handler, self, neighbours);
        this.bootstrapService = new BootstrapService(handler, joinService, leaveService, configs.getBootstrapServer(), name, self);
        this.queryService = new QueryService(handler, self, filenames, neighbours);
        // Heartbeat services
        this.heartbeatService = new HeartbeatService(handler, neighbours, hb_frequency);
        this.pulseReceiverService = new PulseReceiverService(handler, neighbours, joinService);
        this.healthMonitorService = new HealthMonitorService(neighbours, bootstrapService, hb_frequency);
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

    /**
     * Returns name of this node
     *
     * @return name of self node
     */
    public String getName() {
        return name;
    }
}
