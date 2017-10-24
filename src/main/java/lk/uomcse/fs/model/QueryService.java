package lk.uomcse.fs.model;

import com.google.common.collect.Queues;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.SearchRequest;
import lk.uomcse.fs.messages.SearchResponse;
import org.apache.log4j.Logger;
import com.google.common.collect.EvictingQueue;

import java.util.*;

/**
 * Initiates search requests and listens for replies
 */
public class QueryService {
    private static final Logger LOGGER = Logger.getLogger(QueryService.class.getName());

    private static final int TTL = 5;

    private static final int qIdStoreLength = 50;

    private static final int MAX_NODES = 5;

    private static final int MIN_REQ_HEALTH = 50;

    private final CacheService cacheService;

    private final RequestHandler handler;

    private final Node current;

    private final List<String> filenames;

    private final List<Node> neighbours;

    private final Queue<String> idStore;

    private final Thread handleRepliesThread;

    private final Thread handleQueriesThread;

    private boolean running;


    public QueryService(RequestHandler handler, Node current, List<String> filenames, List<Node> neighbours, CacheService cacheService) {
        this.handler = handler;
        this.current = current;
        this.filenames = filenames;
        this.neighbours = neighbours;
        this.cacheService = cacheService;
        this.running = false;
        this.handleRepliesThread = new Thread(this::runHandleReplies);
        this.handleQueriesThread = new Thread(this::runHandleQueries);

        this.idStore = Queues.synchronizedQueue(EvictingQueue.create(qIdStoreLength));
    }

    /**
     * Starts handle replies thread and handle queries thread
     */
    public void start() {
        this.handleQueriesThread.start();
        this.handleRepliesThread.start();
    }

    /**
     * Thread to handle replies
     */
    private void runHandleReplies() {
        running = true;
        while (running) {
            String reply = this.handler.receiveMessage(SearchResponse.ID);
            SearchResponse response = SearchResponse.parse(reply);
            LOGGER.info(String.format("Response received %s", response.toString()));
        }
    }

    /**
     * Thread to handle queries
     */
    private void runHandleQueries() {
        running = true;
        while (running) {
            String reply = this.handler.receiveMessage(SearchRequest.ID);
            LOGGER.info(String.format("Request received %s", reply));
            SearchRequest request = SearchRequest.parse(reply);
            List<String> matched = search(request.getFilename(), request.getHops());
            if (matched.size() > 0) {
                SearchResponse response = new SearchResponse(0, this.current, request.getHops() + 1, matched);
                this.handler.sendMessage(request.getNode().getIp(), request.getNode().getPort(), response);
                LOGGER.info(String.format("Response sent %s", response.toString()));
            }
        }
    }

    /**
     * Initialize a search query
     *
     * @param query keywords to look for
     * @return
     */
    public List<String> search(String query, int hops) {
        List<String> filenames = searchFiles(query);
        if (filenames.size() > 0) return filenames;
        if (hops < TTL) {
            List<Node> bestNodes = selectBestNodes(query);
            SearchRequest request = new SearchRequest(this.current, query, hops + 1);
            LOGGER.info(String.format("Sending query to neighbours %s", request.toString()));
            bestNodes.forEach(node -> this.handler.sendMessage(node.getIp(), node.getPort(), request));
        }
        return filenames;
    }

    /**
     * Select best nodes from the cached nodes and the neighbours
     *
     * @param fileName
     * @return
     */
    private List<Node> selectBestNodes(String fileName) {
        List<Node> bestNodes = cacheService.search(fileName);

        int nodeGap = MAX_NODES - bestNodes.size();
        int fromNeighbours = nodeGap > 0 ? nodeGap + 2 : 2;

        synchronized (neighbours) {
            Collections.sort(neighbours);

            Iterator<Node> neighbourIterator = neighbours.iterator();
            while (neighbourIterator.hasNext() && fromNeighbours > 0) {
                bestNodes.add(neighbourIterator.next());
                fromNeighbours--;
            }
        }

        return bestNodes;
    }

    /**
     * For searching files in this node
     *
     * @param query
     * @return
     */
    private List<String> searchFiles(String query) {
        List<String> found = new ArrayList<>();
        for (String filename : filenames) {
            // TODO: Update following to match specifications
            if (filename.contains(query)) {
                found.add(filename);
            }
        }
        return found;
    }

    /**
     * Check if the given query is already resolved
     *
     * @param queryId
     * @return true if the query is new otherwise return false
     */
    private boolean checkNewQuery(String queryId) {
        return !idStore.contains(queryId);
    }
}
