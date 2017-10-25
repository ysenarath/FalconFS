package lk.uomcse.fs.model;

import com.google.common.collect.Queues;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.messages.SearchRequest;
import lk.uomcse.fs.messages.SearchResponse;
import org.apache.log4j.Logger;
import com.google.common.collect.EvictingQueue;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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

    private final Queue<String> queryIdStore;

    private final Thread handleRepliesThread;

    private final Thread handleQueriesThread;

    private final ConcurrentHashMap<Node, List<String>> results;

    private String currentQuery;

    private boolean running;

    /**
     * Query service
     *
     * @param handler      a request handler
     * @param current      current node (me)
     * @param filenames    reference to list of filenames in this node
     * @param neighbours   reference to list of neighbours
     * @param cacheService cacheService to select best nodes
     */
    public QueryService(RequestHandler handler, Node current, List<String> filenames, List<Node> neighbours, CacheService cacheService) {
        this.handler = handler;
        this.current = current;
        this.filenames = filenames;
        this.neighbours = neighbours;
        this.cacheService = cacheService;
        this.running = false;
        this.results = new ConcurrentHashMap<>();
        this.handleRepliesThread = new Thread(this::runHandleReplies);
        this.handleQueriesThread = new Thread(this::runHandleQueries);

        this.queryIdStore = Queues.synchronizedQueue(EvictingQueue.create(qIdStoreLength));
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
            this.updateResults(response.getNode(), response.getFilenames());
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
            synchronized (queryIdStore) {
                //check for already served queries
                if (isNewQuery(request.getQueryId())) {
                    continue;
                }
                queryIdStore.add(request.getQueryId());
            }
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
        results.clear();
        currentQuery = query;
        List<String> filenames = searchFiles(query);
        if (filenames.size() > 0) {
            this.updateResults(current, filenames);
            return filenames;
        }
        if (hops < TTL) {
            List<Node> bestNodes = selectBestNodes(query);
            SearchRequest request = new SearchRequest(this.current, query, hops + 1);
            LOGGER.info(String.format("Sending query to neighbours %s", request.toString()));
            bestNodes.forEach(node -> this.handler.sendMessage(node.getIp(), node.getPort(), request));
        }
        return filenames;
    }

    /**
     * Update results when queries are search and results are found
     *
     * @param node      Node containing the files
     * @param filenames filenames matching the query
     */
    private void updateResults(Node node, List<String> filenames) {
        synchronized (results) {
            if (!results.containsKey(node))
                results.put(node, filenames);
        }
        cacheService.update(node, filenames);
    }


    /**
     * Returns current query
     *
     * @return current query in progress
     */
    public String getCurrentQuery() {
        return currentQuery;
    }

    /**
     * returns map containing search results
     *
     * @return map of nodes with respective files files
     */
    public Map<Node, List<String>> getSearchResults() {
        return results;
    }

    /**
     * Select best nodes from the cached nodes and the neighbours
     *
     * @param filename a name of a file
     * @return a list of nodes possibly containing the file
     */
    private List<Node> selectBestNodes(String filename) {
        List<Node> bestNodes = cacheService.search(filename);

        int nodeGap = MAX_NODES - bestNodes.size();
        int fromNeighbours = nodeGap > 0 ? nodeGap + 2 : 2;

        synchronized (neighbours) {
            Collections.sort(neighbours);
            bestNodes.addAll(neighbours.subList(0, fromNeighbours > neighbours.size() ? neighbours.size() : fromNeighbours));
        }

        return bestNodes;
    }

    /**
     * For searching files in this node
     *
     * @param query a query to search files over
     * @return list of filenames matching (containing) query
     */
    private List<String> searchFiles(String query) {
        List<String> found = new ArrayList<>();
        for (String filename : filenames) {
            // TODO: Update following to match specifications
            if (filename.toLowerCase().contains(query.toLowerCase())) {
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

    /**
     * Check if the given query is already resolved
     *
     * @param queryId a query id
     * @return true if the query is new otherwise return false
     */
    private boolean isNewQuery(String queryId) {
        return !queryIdStore.contains(queryId);
    }
}
