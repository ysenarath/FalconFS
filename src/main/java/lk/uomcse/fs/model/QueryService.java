package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Node;

import java.util.List;

public class QueryService extends Thread {

    private final RequestHandler handler;

    private final Node current;

    private final List<String> filenames;

    public QueryService(RequestHandler handler, Node current, List<String> filenames) {
        this.handler = handler;
        this.current = current;
        this.filenames = filenames;
    }

    public List<Node> query(String keywords) {
        return null;
    }

    private boolean satisfy(String query) {
        return false;
    }
}
