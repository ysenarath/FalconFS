package lk.uomcse.fs.messages;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.utils.exceptions.InvalidFormatException;

// length SER qid IP port file_name hops
@JsonIgnoreProperties(value = {"receivedTime" }, ignoreUnknown = true)
public class SearchRequest extends Message implements IRequest {
    public static final String ID = "SER";

    private String queryId;

    private Node node;

    private String filename;

    private int hops;

    /**
     * Used by Jakson
     */
    public SearchRequest(){}

    /**
     * Cstr of SearchRequest
     *
     * @param queryId  id of the query
     * @param node     a node
     * @param filename filename to search for
     * @param hops     current number of hops
     */
    public SearchRequest(String queryId, Node node, String filename, int hops) {
        this.queryId = queryId;
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
     *
     * @return
     */
    public String getQueryId() {
        return queryId;
    }

    /**
     * Increments number of hops for this request
     */
    public void incrementHops() {
        this.hops += 1;
    }

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
        if (response.length < 7)
            throw new InvalidFormatException("Parsing failed due to not having enough content to match the format.");
        if (!response[1].equals(ID))
            throw new InvalidFormatException(String.format("Parsing failed due to not having message id: %s. (Received message ID: %s)", ID, response[1]));
        String qid = response[2];
        String ip = response[3];
        int port = Integer.parseInt(response[4]);
        String filename = response[5];
        int hops = Integer.parseInt(response[6]);
        return new SearchRequest(qid, new Node(ip, port), filename, hops);
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
                .append(this.queryId).append(" ")
                .append(node.getIp()).append(" ")
                .append(node.getPort()).append(" ")
                .append(this.filename).append(" ")
                .append(this.hops);
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


    public void setQueryId(String queryId) {
        this.queryId = queryId;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public void setHops(int hops) {
        this.hops = hops;
    }
}
