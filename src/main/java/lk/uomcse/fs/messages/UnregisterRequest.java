package lk.uomcse.fs.messages;

import lk.uomcse.fs.models.Node;

// length UNREG IP_address port_no username
public class UnregisterRequest {
    private String name;

    private Node node;

    public UnregisterRequest(String name, Node node) {
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
        StringBuilder sb = new StringBuilder(" UNREG ");
        sb.append(node.getIp()).append(" ").append(node.getPort()).append(" ").append(name);
        String length = String.format("%04d", sb.length() + 4);
        sb.insert(0, length);
        return sb.toString();
    }
}
