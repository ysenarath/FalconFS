package lk.uomcse.fs.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Queues;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.entity.Packet;
import lk.uomcse.fs.messages.SearchRequest;
import lk.uomcse.fs.messages.SearchResponse;
import org.apache.log4j.Logger;
import com.google.common.collect.EvictingQueue;

import java.net.IDN;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Initiates search requests and listens for replies
 */
public class QueryService {
    private static final Logger LOGGER = Logger.getLogger(QueryService.class.getName());

    private static final int TTL = 5;

    private static final int ID_STORE_QUERY_LENGTH = 50;

    private static final int ID_STORE_INDEX_SIZE = 10;

    private static final int MAX_NODES = 5;

    private static final int MIN_REQ_HEALTH = 50;

    private final CacheService cacheService;

    private final RequestHandler handler;

    private final Node current;

    private final List<String> filenames; //split file names which are lowercased

    private final List<Node> neighbours;

    private final ConcurrentMap<String, Queue<String>> queryIdStore;

    private final Thread handleRepliesThread;

    private final Thread handleQueriesThread;

    private final ConcurrentHashMap<Node, List<String>> results;

    private String currentQuery;

    private int currentQueryID;

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

        this.queryIdStore = CacheBuilder.newBuilder()
                .maximumSize(ID_STORE_INDEX_SIZE)
                .<String, Queue<String>>build().asMap();
//                Queues.synchronizedQueue(EvictingQueue.create(qIdStoreLength));
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
            String responseStr = this.handler.receiveMessage(SearchResponse.ID);
            SearchResponse response = SearchResponse.parse(responseStr);
            if (Integer.parseInt(response.getQueryID()) == currentQueryID) {
                this.updateResults(response.getNode(), response.getFilenames());
                LOGGER.info(String.format("Response received matching current query: %s", response.toString()));
            } else {
                LOGGER.info(String.format("Response received matching old query: %s", response.toString()));
            }
        }
    }

    /**
     * Sets current search query
     *
     * @param query query
     */
    public void search(String query) {
        results.clear();
        currentQuery = query;
        currentQueryID += 1;
        List<String> matches = searchUtils(query, 0, null);
        if (matches.size() > 0)
            this.updateResults(current, matches);
    }

    /**
     * Thread to handle queries
     */
    private void runHandleQueries() {
        running = true;
        while (running) {
            Packet packet = this.handler.receivePacket(SearchRequest.ID);
            String requestStr = packet.getMessage();
            LOGGER.info(String.format("Request received %s", requestStr));
            SearchRequest request = SearchRequest.parse(requestStr);
            //check for already served queries
            if (!isNewQuery(request)) {
                continue;
            }
            List<String> matches = searchUtils(request.getFilename(), request.getHops(), packet.getReceiverNode());
            if (matches.size() > 0) {
                SearchResponse response = new SearchResponse(request.getQueryId(), matches.size(), this.current, request.getHops() + 1, matches);
                this.handler.sendMessage(request.getNode().getIp(), request.getNode().getPort(), response);
                LOGGER.info(String.format("Response sent %s", response.toString()));
            }
        }
    }

    /**
     * Initialize a search query
     *
     * @param query keywords to look for
     * @return list of filenames
     */
    private List<String> searchUtils(String query, int hops, Node ignore) {
        List<String> matches = searchFiles(query);
        if (matches.size() > 0) {
            return matches;
        }
        if (hops < TTL) {
            List<Node> bestNodes = selectBestNodes(query);
            // TODO: Do this in selectBestNodes section
            // Ignore nodes indicated by ignore args
            if (ignore != null)
                bestNodes.remove(ignore);
            SearchRequest request = new SearchRequest(String.valueOf(currentQueryID), this.current, query, hops + 1);
            LOGGER.info(String.format("Sending query to neighbours %s", request.toString()));
            bestNodes.forEach(node -> this.handler.sendMessage(node.getIp(), node.getPort(), request));
        }
        return matches;
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
     * Select best nodes from the cached nodes and the neighbours
     *
     * @param filename a name of a file
     * @return a list of nodes possibly containing the file
     */
    private List<Node> selectBestNodes(String filename) {
        List<Node> bestNodes = cacheService.search(filename);

        if (bestNodes == null)
            bestNodes = new ArrayList<>();

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
        boolean isMatch;
        String[] fNameArry;
        for (String filename : filenames) {
            fNameArry = filename.split(" +");
            isMatch = true;
            for (String keyword : query.split(" +")) {
                if (!Arrays.asList(fNameArry).contains(keyword)) {
                    isMatch = false;
                    break;
                }
            }
            LOGGER.debug(String.format("Found file with name %s for query %s", filename, query));
            if (isMatch) {
                found.add(filename.replace(' ', '_'));
            }

        }
        return found;
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
     * @param request a Search request
     * @return true if the query is new otherwise return false
     */
    private synchronized boolean isNewQuery(SearchRequest request) {
        String nodeName = request.getNode().toString();
        String queryId = request.getQueryId();

        Queue<String> idList = queryIdStore.get(nodeName);
        if (idList == null) {
            idList = Queues.synchronizedQueue(EvictingQueue.create(ID_STORE_QUERY_LENGTH));
            queryIdStore.putIfAbsent(nodeName, idList);
        } else if (idList.contains(queryId)) {
            return false;
        }

        idList.add(queryId);
        return true;
    }
}
