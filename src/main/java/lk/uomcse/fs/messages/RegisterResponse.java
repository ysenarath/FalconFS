package lk.uomcse.fs.messages;

import lk.uomcse.fs.entity.Neighbour;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.utils.exceptions.InvalidFormatException;

import java.util.ArrayList;
import java.util.List;

// length REGOK no_nodes IP_1 port_1 IP_2 port_2
public class RegisterResponse extends Message implements IResponse {
    public static final String ID = "REGOK";

    private int nodeCount;

    private List<Neighbour> nodes;

    /**
     * Creates a Register response
     *
     * @param nodeCount number of nodes
     * @param nodes     list of nodes
     */
    private RegisterResponse(int nodeCount, List<Neighbour> nodes) {
        this.nodeCount = nodeCount;
        this.nodes = nodes;
    }

    /**
     * Gets node count
     *
     * @return node count
     */
    public int getNodeCount() {
        return nodeCount;
    }

    /**
     * Gets nodes
     *
     * @return list of nodes
     */
    public List<Neighbour> getNodes() {
        return nodes;
    }

    /**
     * Whether the register response indicates success state
     * Not success error code can be obtained through getNodeCount()
     *
     * @return whether request for registration is a success
     */
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
            throw new InvalidFormatException(String.format("Parsing failed due to not having message id: %s. (Received message ID: %s)", ID, response[1]));
        int n = Integer.parseInt(response[2]);
        if (n > 9995) {
            return new RegisterResponse(n, null);
        }
        List<Neighbour> lst = new ArrayList<>();
        for (int i = 3; i < 3 + n * 2; i += 2) {
            String ip = response[i];
            int port = Integer.parseInt(response[i + 1]);
            Neighbour neighbour = new Neighbour(ip, port);
            lst.add(neighbour);
        }
        return new RegisterResponse(n, lst);
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
