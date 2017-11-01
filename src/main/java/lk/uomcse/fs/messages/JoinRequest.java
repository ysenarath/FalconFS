package lk.uomcse.fs.messages;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.utils.exceptions.InvalidFormatException;

// length JOIN IP_address port_no
public class JoinRequest implements IRequest {
    public static final String ID = "JOIN";

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
        StringBuilder sb = new StringBuilder(String.format(" %s ", ID));
        sb.append(node.getIp()).append(" ").append(node.getPort());
        String length = String.format("%04d", sb.length() + 4);
        sb.insert(0, length);
        return sb.toString();
    }

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

    public Node getNode() {
        return node;
    }
}
