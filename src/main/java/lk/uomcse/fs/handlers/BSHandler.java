package lk.uomcse.fs.handlers;

import lk.uomcse.fs.models.Node;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.Set;

public class BSHandler {
    private String ip;

    private int port;

    private Node node;

    public BSHandler(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Set<Node> register(Node me, String name) {
        throw new NotImplementedException();
    }

    public boolean unregister(Node me) {
        throw new NotImplementedException();
    }
}
