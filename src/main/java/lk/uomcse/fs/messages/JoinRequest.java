package lk.uomcse.fs.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.utils.exceptions.InvalidFormatException;

// length JOIN IP_address port_no
@JsonIgnoreProperties(value = {"receivedTime" }, ignoreUnknown = true)
public class JoinRequest extends Message implements IRequest {
    public static final String ID = "JOIN";

    private Node node;

    /**
     * Used by Jakson
     */
    public JoinRequest(){}

    public JoinRequest(Node node) {
        this.node = node;
    }

    /**
     * Returns string representation of the message according to standards provided
     *
     * @return string representation of this request
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format(" %s ", ID));
        sb.append(node.getIp()).append(" ").append(node.getPort());
        String length = String.format("%04d", sb.length() + 4);
        sb.insert(0, length);
        return sb.toString();
    }

    /**
     * Parses the join request
     *
     * @param msg message type
     * @return Join request message
     */
    public static JoinRequest parse(String msg) {
        if (msg == null)
            throw new NullPointerException();
        String[] response = msg.split(" ");
        if (response.length != 4)
            throw new InvalidFormatException("Parsing failed due to not having correct word length.");
        if (!response[1].equals(ID))
            throw new InvalidFormatException(String.format("Parsing failed due to not having message id: %s. (Received message ID: %s)", ID, response[1]));
        String ip = response[2];
        int port = Integer.parseInt(response[3]);
        return new JoinRequest(new Node(ip, port));
    }

    /**
     * Returns Node one should join with
     *
     * @return node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Returns ID
     *
     * @return ID of this message type
     */
    @Override
    public String getID() {
        return ID;
    }

    public void setNode(Node node) {
        this.node = node;
    }


}
