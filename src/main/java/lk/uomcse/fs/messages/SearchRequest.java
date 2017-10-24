package lk.uomcse.fs.messages;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.utils.InvalidFormatException;

// length SER IP port file_name hops
public class SearchRequest implements IRequest {
    public static final String ID = "SER";

    private Node node;

    private String filename;

    private int hops;

    private String queryId;

    public SearchRequest(Node node, String filename, int hops) {
        this.node = node;
        this.filename = filename;
        this.hops = hops;
    }

    /**
     * IP:Port of the requester
     *
     * @return node representing requester
     */
    public Node getNode() {
        return node;
    }

    /**
     * File name being searched.
     *
     * @return search filename
     */
    public String getFilename() {
        return filename;
    }

    /**
     * A hop count. May be of use for cost calculations (optional).
     *
     * @return a hop count
     */
    public int getHops() {
        return hops;
    }

    /**
     * Query Id. All queries must have unique query id.
     * @return
     */
    public String getQueryId() {return queryId;}

    /**
     * Parses search search request
     *
     * @param reply reply in string
     * @return Search request message
     */
    public static SearchRequest parse(String reply) {
        if (reply == null)
            throw new NullPointerException();
        String[] response = reply.split(" ");
        if (response.length < 6)
            throw new InvalidFormatException("Parsing failed due to not having enough content to match the format.");
        if (!response[1].equals(ID))
            throw new InvalidFormatException("Parsing failed due to not having correct type of message.");
        String ip = response[2];
        int port = Integer.parseInt(response[3]);
        String filename = response[4];
        int hops = Integer.parseInt(response[5]);
        return new SearchRequest(new Node(ip, port), filename, hops);
    }

    /**
     * To string method (length SER IP port file_name hops)
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(" ");
        sb.append(ID).append(" ")
                .append(node.getIp()).append(" ")
                .append(node.getPort()).append(" ")
                .append(this.filename).append(" ")
                .append(this.hops).append(" ");
        String length = String.format("%04d", sb.length() + 4);
        sb.insert(0, length);
        return sb.toString();
    }


}
