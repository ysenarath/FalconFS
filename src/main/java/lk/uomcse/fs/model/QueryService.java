package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.SearchRequest;
import lk.uomcse.fs.messages.SearchResponse;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * Initiates search requests and listens for replies
 */
public class QueryService {
    private static final Logger LOGGER = Logger.getLogger(QueryService.class.getName());

    private static final int TTL = 5;

    private final RequestHandler handler;

    private final Node current;

    private final List<String> filenames;

    private final List<Node> neighbours;

    private final Thread handleRepliesThread;

    private final Thread handleQueriesThread;

    private boolean running;

    public QueryService(RequestHandler handler, Node current, List<String> filenames, List<Node> neighbours) {
        this.handler = handler;
        this.current = current;
        this.filenames = filenames;
        this.neighbours = neighbours;
        this.running = false;
        this.handleRepliesThread = new Thread(this::runHandleReplies);
        this.handleQueriesThread = new Thread(this::runHandleQueries);
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
            String requestStr = this.handler.receiveMessage(SearchRequest.ID);
            LOGGER.info(String.format("Request received %s", requestStr));
            SearchRequest request = SearchRequest.parse(requestStr);
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
            List<Node> owners = searchCache(query);
            List<Node> bestNodes = bestNodes(owners, neighbours);
            SearchRequest request = new SearchRequest(this.current, query, hops + 1);
            LOGGER.info(String.format("Sending query to neighbours %s", request.toString()));
            bestNodes.forEach(node -> this.handler.sendMessage(node.getIp(), node.getPort(), request));
        }
        return filenames;
    }

    /**
     * For selecting best nodes
     *
     * @param owners
     * @param neighbours
     * @return
     */
    private List<Node> bestNodes(List<Node> owners, List<Node> neighbours) {
        return neighbours;
    }

    /**
     * For searching cache
     *
     * @param query
     * @return
     */
    private List<Node> searchCache(String query) {
        return new ArrayList<>();
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
     * Sets running status
     *
     * @param running state
     */
    public void setRunning(boolean running) {
        this.running = running;
    }
}
