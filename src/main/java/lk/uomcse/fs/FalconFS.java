package lk.uomcse.fs;

import lk.uomcse.fs.models.BootstrapClient;
import lk.uomcse.fs.models.Node;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.Set;

/**
 * Falcon File System
 */
public class FalconFS {
    private String name;

    private Node me;

    private Set<Node> neighbours;

    private BootstrapClient bs;

    /**
     * Imports file system requirements
     *
     * @param bs a bootstrap server
     */
    private FalconFS(String ip, int port, BootstrapClient bs) {
        this.neighbours = new HashSet<>();
        this.bs = bs;
        this.me = new Node(ip, port);
    }

    /**
     * Starts the Falcon file system
     */
    private void start() {
        this.bootstrap();
    }

    /**
     * Joins to provided node and add it as a neighbour
     *
     * @param n a node to join
     */
    private void join(Node n) {
        neighbours.add(n);
        throw new NotImplementedException();
    }

    /**
     * Connects with bootstrap server
     */
    private void bootstrap() {
        Set<Node> nodes = bs.register(me, name);
        nodes.forEach(this::join);
    }

    /**
     * Main Method
     *
     * @param args No args
     */
    public static void main(String[] args) {
        BootstrapClient bs = new BootstrapClient("", 0);
        FalconFS fs = new FalconFS("", 0, bs);
        fs.start();
    }
}
