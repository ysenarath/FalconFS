package lk.uomcse.fs.model.service;

import lk.uomcse.fs.model.entity.BootstrapServer;
import lk.uomcse.fs.model.entity.Neighbour;
import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.messages.RegisterRequest;
import lk.uomcse.fs.model.messages.RegisterResponse;
import lk.uomcse.fs.model.messages.UnregisterRequest;
import lk.uomcse.fs.model.messages.UnregisterResponse;
import lk.uomcse.fs.model.RequestHandler;
import lk.uomcse.fs.utils.ListUtils;
import lk.uomcse.fs.utils.TextFormatUtils;
import lk.uomcse.fs.utils.error.BootstrapError;
import lk.uomcse.fs.utils.error.BootstrapFullError;
import lk.uomcse.fs.utils.error.CommandError;
import lk.uomcse.fs.utils.exceptions.BootstrapException;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

public class BootstrapService {
    private final static Logger LOGGER = Logger.getLogger(BootstrapService.class.getName());

    private final static int MAX_RETRIES = 3;

    private final BootstrapServer server;

    private final JoinService joinService;

    private final LeaveService leaveService;

    private final RequestHandler handler;

    private final String name;

    private final Node self;

    /**
     * Constructs bootstrap service providing register and unregister functions
     *
     * @param handler     Request handler
     * @param joinService join service to join to nodes ones the registration is complete
     * @param bs          Bootstrap server (details)
     */
    public BootstrapService(RequestHandler handler, JoinService joinService, LeaveService leaveService, BootstrapServer bs, String name, Node self) {
        this.server = bs;
        this.handler = handler;
        this.joinService = joinService;
        this.leaveService = leaveService;
        this.name = name;
        this.self = self;
    }

    /**
     * Registers the node bootstrap from server
     *
     * @return List of nodes if the request is successful
     */
    public List<Neighbour> register(boolean leaveNodes) throws BootstrapException {
        RegisterRequest msg = new RegisterRequest(name, self);
        LOGGER.info(String.format("Requesting bootstrap server: %s", msg.toString()));
        RegisterResponse response = null;
        int retries = 0;
        while (true)
            try {
                this.handler.sendMessage(this.server.getAddress(), this.server.getPort(), msg, true);
                response = (RegisterResponse) this.handler.receiveMessage(RegisterResponse.ID, 1);
                break;
            } catch (TimeoutException e) {
                if (retries < MAX_RETRIES) {
                    LOGGER.debug("Failed the " + TextFormatUtils.toRankedText(retries + 1) + " attempt to register");
                    retries++;
                } else {
                    LOGGER.error("Failed the attempt to register. Unable to receive message from bootstrap.");
                    throw new BootstrapException(0, "Failed the attempt to register. Unable to receive message from bootstrap.");
                }
            }
        LOGGER.info(String.format("Bootstrap server replied: %s", response.toString()));
        BootstrapError err;
        if (response.isSuccess()) {
            List<Node> subList = ListUtils.randomSubList(response.getNodes(), 2);
            return subList.stream().map(Neighbour::new).collect(Collectors.toList());
        } else {
            switch (response.getNodeCount()) {
                case (9998):
                    boolean status = this.unregister(leaveNodes);
                    if (!status) throw new BootstrapException(9998, "Un-registration failed. Unable to bootstrap.");
                    return this.register(leaveNodes);
                case (9999):
                    err = new CommandError.Builder(9999)
                            .setError("Failed, there is some error in the command")
                            .build();
                    break;
                case (9997):
                    err = new BootstrapFullError.Builder(9997)
                            .setError("Failed, registered to another user, try a different IP and port")
                            .build();
                    break;
                case (9996):
                    err = new BootstrapFullError.Builder(9996)
                            .setError("Failed, can’t register. BS full.")
                            .build();
                    break;
                default:
                    throw new UnknownError("Unknown error code received from bootstrap server.");
            }
        }
        throw new BootstrapException(err.getErrorCode(), err.getErrorMessage());
    }

    /**
     * Unregisters the node bootstrap from server
     *
     * @param leaveNodes whether to leave nodes or not
     * @return whether its complete or not
     * @throws BootstrapException Bootstrap exception
     */
    public boolean unregister(Boolean leaveNodes) throws BootstrapException {
        UnregisterRequest msg = new UnregisterRequest(name, self);
        LOGGER.info(String.format("Requesting Bootstrap Server: %s", msg.toString()));
        UnregisterResponse response;
        int count = 0;
        //try again for three times to unregister
        while (count < MAX_RETRIES) {
            try {
                // Method will wait for response
                this.handler.sendMessage(this.server.getAddress(), this.server.getPort(), msg, true);
                response = (UnregisterResponse) this.handler.receiveMessage(UnregisterResponse.ID, 1);
                LOGGER.info(String.format("Bootstrap Server replied: %s", response.toString()));
                if (leaveNodes)
                    leaveService.leave();
                return response.isSuccess();
            } catch (TimeoutException e) {
                count += 1;
                LOGGER.error("Failed the " + count + " attempt to unregister");
            }
        }
        throw new BootstrapException(0, "Failed to unregister node. No response received from bootstrap server.");
    }

    /**
     * Connects with bootstrap server and joins to nodes provided
     */
    public void bootstrap(Boolean leaveNodes) throws BootstrapException {
        try {
            List<Neighbour> nodes = this.register(leaveNodes);
            nodes.forEach(joinService::join);
            for (Neighbour n : nodes) {
                boolean status = joinService.join(n);
                if (status)
                    LOGGER.info(String.format("Joined to neighbour: %s", n.toString()));
                else
                    LOGGER.error(String.format("Failed join to neighbour: %s", n.toString()));
            }
        } catch (BootstrapException ex) {
            throw new BootstrapException(ex.getErrorCode(), ex.getMessage());
        }
    }

    /**
     * Connects with bootstrap server and joins to nodes provided
     */
    public void bootstrap() throws BootstrapException {
        bootstrap(true);
    }

    /**
     * Registers the node bootstrap from server
     *
     * @return List of nodes if the request is successful
     */
    public List<Neighbour> register() throws BootstrapException {
        return register(true);
    }

    /**
     * Unregisters the node bootstrap from server
     *
     * @return whether the response is success
     */
    public boolean unregister() throws BootstrapException {
        return unregister(true);
    }
}
