package lk.uomcse.fs.messages;

import lk.uomcse.fs.entity.Node;

// length JOIN IP_address port_no
public class JoinRequest implements IRequest {
    private Node node;

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
        StringBuilder sb = new StringBuilder(" JOIN ");
        sb.append(node.getIp()).append(" ").append(node.getPort());
        String length = String.format("%04d", sb.length() + 4);
        sb.insert(0, length);
        return sb.toString();
    }
}
