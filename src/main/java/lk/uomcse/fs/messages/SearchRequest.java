package lk.uomcse.fs.messages;

import lk.uomcse.fs.models.Node;

import java.util.List;

// length SER IP port file_name hops
public class SearchRequest {
    private Node node;

    private String filename;

    private int hops;
}
