package lk.uomcse.fs.model;

import lk.uomcse.fs.FalconFS;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.SearchRequest;
import lk.uomcse.fs.messages.SearchResponse;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Initiates search requests and listens for replies
 */
public class QueryService {
    private final static Logger LOGGER = Logger.getLogger(QueryService.class.getName());

    private final RequestHandler handler;

    private final Node current;

    private final List<String> filenames;

    private final Set<Node> neighbours;

    private final Thread handleRepliesThread;

    private final Thread handleQueriesThread;

    private boolean running;

    public QueryService(RequestHandler handler, Node current, List<String> filenames, Set<Node> neighbours) {
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
            String reply = this.handler.receiveMessage(SearchRequest.ID);
            LOGGER.info(String.format("Request received %s", reply));
            SearchRequest request = SearchRequest.parse(reply);
            List<String> matched = new ArrayList<>();
            SearchResponse response = new SearchResponse(0, this.current, request.getHops() + 1, matched);
            this.handler.sendMessage(request.getNode().getIp(), request.getNode().getPort(), response);
            LOGGER.info(String.format("Response sent %s", response.toString()));
        }
    }

    /**
     * Initialize a search query
     *
     * @param query keywords to look for
     * @return
     */
    public List<Node> search(String query) {
        SearchRequest request = new SearchRequest(this.current, query, 0);
        LOGGER.info(String.format("Sending query to neighbours %s", request.toString()));
        neighbours.forEach(node -> this.handler.sendMessage(node.getIp(), node.getPort(), request));
        return null;
    }
}
