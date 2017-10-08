package lk.uomcse.fs.messages;

import lk.uomcse.fs.entity.Node;

// length SER IP port file_name hops
public class SearchRequest implements IRequest {
    public static final String ID = "SER";

    private Node node;

    private String filename;

    private int hops;

    public static SearchRequest parse(String reply) {
        return null;
    }
}
