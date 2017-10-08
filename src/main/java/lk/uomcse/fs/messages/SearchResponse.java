package lk.uomcse.fs.messages;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.utils.InvalidFormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// length SEROK no_files IP port hops filename1 filename2 ... ...
public class SearchResponse implements IResponse {
    public static final String ID = "SEROK";

    private int fileCount;

    private Node node;

    private int hops;

    private List<String> filenames;

    /**
     * Cstr
     *
     * @param fileCount Number of results returned
     * @param node      Node having (stored) the file.
     * @param hops      Hops required to find the file(s).
     * @param filenames Actual names of the files.
     */
    public SearchResponse(int fileCount, Node node, int hops, List<String> filenames) {
        this.fileCount = fileCount;
        this.node = node;
        this.hops = hops;
        this.filenames = filenames;
    }

    /**
     * Number of results returned
     *
     * @return ≥ 1 – Successful
     * 0 – no matching results. Searched key is not in key table
     * 9999 – failure due to node unreachable
     * 9998 – some other error.
     */
    public int getFileCount() {
        return fileCount;
    }

    /**
     * Node having (stored) the file.
     *
     * @return a node having (stored) the file.
     */
    public Node getNode() {
        return node;
    }

    /**
     * Hops required to find the file(s).
     *
     * @return hops required to find the file(s).
     */
    public int getHops() {
        return hops;
    }

    /**
     * Actual names of the files.
     *
     * @return file names.
     */
    public List<String> getFilenames() {
        return filenames;
    }

    /**
     * Parses search response message
     *
     * @param reply reply in string
     * @return Unregister response message
     */
    public static SearchResponse parse(String reply) {
        if (reply == null)
            throw new NullPointerException();
        String[] response = reply.split(" ");
        if (response.length < 6)
            throw new InvalidFormatException("Parsing failed due to not having enough content to match the format.");
        if (!response[1].equals(ID))
            throw new InvalidFormatException("Parsing failed due to not having correct type of message.");
        int n = Integer.parseInt(response[2]);
        String ip = response[3];
        int port = Integer.parseInt(response[4]);
        int hops = Integer.parseInt(response[5]);
        // TODO: Handle Errors
        List<String> filenames = Arrays.asList(response).subList(6, response.length);
        return new SearchResponse(n, new Node(ip, port), hops, filenames);
    }
}
