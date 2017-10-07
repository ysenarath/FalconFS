package lk.uomcse.fs.model;

import lk.uomcse.fs.messages.RegisterRequest;
import lk.uomcse.fs.messages.RegisterResponse;
import lk.uomcse.fs.messages.UnregisterRequest;
import lk.uomcse.fs.messages.UnregisterResponse;
import lk.uomcse.fs.entity.BootstrapServer;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.utils.RequestFailedException;
import org.apache.log4j.Logger;

import java.util.List;

public class BootstrapClient {
    private final static Logger LOGGER = Logger.getLogger(BootstrapClient.class.getName());

    private final BootstrapServer server;

    private RequestHandler handler;

    public BootstrapClient(BootstrapServer bs, RequestHandler handler) {
        this.server = bs;
        this.handler = handler;
    }

    /**
     * Registers the node bootstrap from server
     * TODO: Update what happens when there is a timeout
     *
     * @param name name of the client
     * @param me   node represented by the name
     * @return List of nodes if the request is successful
     */
    public List<Node> register(String name, Node me) {
        RegisterRequest msg = new RegisterRequest(name, me);
        LOGGER.info(String.format("Requesting bootstrap server: %s", msg.toString()));
        this.handler.sendRequest(this.server.getHost(), this.server.getPort(), msg);
        String reply = this.handler.receiveResponse(RegisterResponse.ID);
        LOGGER.info(String.format("Bootstrap server replied: %s", reply));
        RegisterResponse rsp = RegisterResponse.parse(reply);
        if (rsp.isSuccess())
            // TODO: Select random 2 and return
            return rsp.getNodes();
        else
            switch (rsp.getNodeCount()) {
                case (9998): {
                    this.unregister(name, me);
                    return this.register(name, me);
                }
                default:
                    break;
            }
        // TODO: Handle other request errors
        throw new RequestFailedException("Unhandled request error!");
    }

    /**
     * Unregisters the node bootstrap from server
     * TODO: Update what happens when there is a timeout
     *
     * @param name name of the client
     * @param me   node represented by the name
     * @return whether the response is success
     */
    public boolean unregister(String name, Node me) {
        UnregisterRequest msg = new UnregisterRequest(name, me);
        LOGGER.info(String.format("Requesting Bootstrap Server: %s", msg.toString()));
        this.handler.sendRequest(this.server.getHost(), this.server.getPort(), msg); // Method will wait for reply
        String reply = this.handler.receiveResponse(UnregisterResponse.ID);
        LOGGER.info(String.format("Bootstrap Server replied: %s", reply));
        UnregisterResponse rsp = UnregisterResponse.parse(reply);
        return rsp.isSuccess();
    }
}
