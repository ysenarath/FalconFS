package lk.uomcse.fs.messages;

import lk.uomcse.fs.entity.Node;

// length LEAVE IP_address port_no
public class LeaveRequest extends Message implements IRequest {
    public static final String ID = "LEAVE";

    private Node node;

    /**
     * Cstr
     *
     * @param node a node that leaves the system
     */
    public LeaveRequest(Node node) {
        this.node = node;
    }

    public static IMessage parse(String data) {
        return null;
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
