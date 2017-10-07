package lk.uomcse.fs.models;

import lk.uomcse.fs.messages.RegisterRequest;
import lk.uomcse.fs.messages.RegisterResponse;
import lk.uomcse.fs.messages.UnregisterRequest;
import lk.uomcse.fs.messages.UnregisterResponse;
import lk.uomcse.fs.udp.UDPClient;
import lk.uomcse.fs.utils.RequestFailedException;

import java.util.List;
import java.util.logging.Logger;

public class BootstrapClient extends UDPClient {
    private final static Logger LOGGER = Logger.getLogger(BootstrapClient.class.getName());

    public BootstrapClient(String host, int port) {
        super(host, port);
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
        super.open();
        RegisterRequest msg = new RegisterRequest(name, me);
        LOGGER.info(String.format("Requesting Bootstrap Server: %s", msg.toString()));
        String reply = request(msg.toString()); // Method will wait for reply
        LOGGER.info(String.format("Bootstrap Server replied: %s", reply));
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
        String reply = request(msg.toString()); // Method will wait for reply
        LOGGER.info(String.format("Bootstrap Server replied: %s", reply));
        UnregisterResponse rsp = UnregisterResponse.parse(reply);
        super.close();
        return rsp.isSuccess();
    }
}
