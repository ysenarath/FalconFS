package lk.uomcse.fs.model.messages;

import lk.uomcse.fs.model.entity.Node;

// length REG IP_address port_no username
public class RegisterRequest extends Message implements IRequest {
    private static final String ID = "REG";

    private Node node;

    private String name;

    /**
     * Constructs Register request
     *
     * @param name name of requesting node
     * @param node a node indicating the name
     */
    public RegisterRequest(String name, Node node) {
        this.name = name;
        this.node = node;
    }

    /**
     * Returns string representation of the message according to standards provided
     *
     * @return string representation of this request
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" REG ");
        sb.append(node.getIp()).append(" ").append(node.getPort()).append(" ").append(name);
        String length = String.format("%04d", sb.length() + 4);
        sb.insert(0, length);
        return sb.toString();
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
}