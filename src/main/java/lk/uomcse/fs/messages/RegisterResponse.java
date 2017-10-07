package lk.uomcse.fs.messages;

import lk.uomcse.fs.models.Node;
import lk.uomcse.fs.utils.InvalidFormatException;
import lk.uomcse.fs.utils.RequestFailedException;

import java.util.ArrayList;
import java.util.List;

// length REGOK no_nodes IP_1 port_1 IP_2 port_2
public class RegisterResponse {
    private int nodeCount;

    private List<Node> nodes;

    private RegisterResponse(int nodeCount, List<Node> nodes) {
        this.nodeCount = nodeCount;
        this.nodes = nodes;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public boolean isSuccess() {
        return nodeCount < 9996;
    }

    /**
     * Parse the content and returns a {{{@link RegisterResponse}}}
     *
     * @param reply a reply to parse
     * @return {{{@link RegisterResponse}}} of the message
     */
    public static RegisterResponse parse(String reply) {
        if (reply == null)
            throw new NullPointerException();
        String[] response = reply.split(" ");
        if (response.length < 3)
            throw new InvalidFormatException("Parsing failed due to not having enough content to match the format.");
        if (!response[1].equals("REGOK"))
            throw new InvalidFormatException("Parsing failed due to not having correct type of message.");
        int n = Integer.parseInt(response[2]);
        if (n > 9995) {
            return new RegisterResponse(n, null);
        }
        List<Node> lst = new ArrayList<>();
        for (int i = 3; i < 3 + n * 2; i += 2) {
            String ip = response[i];
            int port = Integer.parseInt(response[i + 1]);
            Node node = new Node(ip, port);
            lst.add(node);
        }
        return new RegisterResponse(n, lst);
    }
}
