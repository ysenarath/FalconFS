package lk.uomcse.fs;

import lk.uomcse.fs.models.BootstrapClient;
import lk.uomcse.fs.models.FileServer;
import lk.uomcse.fs.models.Node;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Falcon File System
 */
public class FalconFS {
    private final static Logger LOGGER = Logger.getLogger(FalconFS.class.getName());

    private String name;

    private Node me;

    private Set<Node> neighbours;

    private BootstrapClient bs;

    private FileServer fs;

    private Thread fst;

    /**
     * Imports file system requirements
     *
     * @param bs a bootstrap server
     */
    private FalconFS(String name, String ip, int port, BootstrapClient bs) {
        this.name = name;
        this.bs = bs;
        this.me = new Node(ip, port);
        this.fs = new FileServer(port);
        this.neighbours = new HashSet<>();
        fst = new Thread(fs);
    }

    /**
     * Starts the Falcon file system
     */
    private void start() {
        // 1. Start the file server
        this.fst.start();
        // 2. Connect to neighbours (bootstrap + join)
        this.bootstrap();
        // 3. start querying
        // while random keyword in keywords query(keyword)
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
     * Connects with bootstrap server and joins to nodes provided
     */
    private void bootstrap() {
        List<Node> nodes = bs.register(name, me);
        nodes.forEach(this::join);
        LOGGER.info(String.format("Joined to nodes: %s", nodes.toString()));
    }


    /**
     * Query and print results
     * ONLY FOR DEBUGGING: have to implement a way to output files received
     *
     * @param keyword a word/ series of continuous words in filename to query in the network
     */
    private void query(String keyword) {
        // use keyword
    }

    /**
     * Main Method
     *
     * @param args No args yet
     */
    public static void main(String[] args) {
        BootstrapClient bs = new BootstrapClient("localhost", 55555);
        FalconFS fs = new FalconFS("uom_cse", "localhost", 5555, bs);
        fs.start();
        fs.query("XXX");
    }
}
