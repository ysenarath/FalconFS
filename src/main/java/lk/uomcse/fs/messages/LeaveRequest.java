package lk.uomcse.fs.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.utils.exceptions.InvalidFormatException;

// length LEAVE IP_address port_no
@JsonIgnoreProperties(value = {"sender", "receivedTime"}, ignoreUnknown = true)
public class LeaveRequest extends Message implements IRequest {
    public static final String ID = "LEAVE";

    private Node node;

    /**
     * Used by Jakson
     */
    public LeaveRequest(){}

    /**
     * Cstr
     *
     * @param node a node that leaves the system
     */
    public LeaveRequest(Node node) {
        this.node = node;
    }

    /**
     * Parses leave response
     *
     * @param msg message to be parsed
     * @return Message type
     */
    public static IMessage parse(String msg) {
        if (msg == null)
            throw new NullPointerException();
        String[] response = msg.split(" ");
        if (response.length != 4)
            throw new InvalidFormatException("Parsing failed due to not having correct word length.");
        if (!response[1].equals(ID))
            throw new InvalidFormatException(String.format("Parsing failed due to not having message id: %s. (Received message ID: %s)", ID, response[1]));
        String ip = response[2];
        int port = Integer.parseInt(response[3]);
        return new LeaveRequest(new Node(ip, port));
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

    /**
     * Gets node leaving the system
     *
     * @return a node
     */
    public Node getNode() {
        return node;
    }

    /**
     * Sets Node leaving the system
     *
     * @param node a node
     */
    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * To String
     *
     * @return a string representing message
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(String.format(" %s ", ID));
        sb.append(node.getIp()).append(" ").append(node.getPort());
        String length = String.format("%04d", sb.length() + 4);
        sb.insert(0, length);
        return sb.toString();
    }


}
