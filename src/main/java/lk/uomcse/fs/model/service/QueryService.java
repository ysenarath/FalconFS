package lk.uomcse.fs.model.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
import lk.uomcse.fs.model.RequestHandler;
import lk.uomcse.fs.model.ResultList;
import lk.uomcse.fs.model.Statistics;
import lk.uomcse.fs.model.entity.Neighbour;
import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.messages.SearchRequest;
import lk.uomcse.fs.model.messages.SearchResponse;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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

    private static final int MAX_NODES = 4;

    private static final int MIN_REQ_HEALTH = 50;

    private static final int MAX_INDEX_SIZE = 100;

    private static final int MAX_NODE_QUEUE_LENGTH = 10;

    private boolean useCache;
    // -----------------------------------------------------------------------------------------------------------------

    private final CacheService cacheService;

    private final RequestHandler handler;

    private final Node current;

    private final List<String> filenames; //  Split file names which are in lowercase

    private final List<Neighbour> neighbours;

    private final ConcurrentMap<String, Queue<String>> queryIdStore;

    private final Thread handleResponsesThread;

    private final Thread handleQueriesThread;

    private final ConcurrentHashMap<Node, ResultList> results;

    private final Statistics statistics;

    // -----------------------------------------------------------------------------------------------------------------

    private int currentQueryID;

    private String currentQuery;

    private long sentTime;

    private boolean running;

    /**
     * Query service
     *
     * @param handler    a request handler
     * @param current    current node (me)
     * @param filenames  reference to list of filenames in this node
     * @param neighbours reference to list of neighbours
     */
    public QueryService(RequestHandler handler, Node current, List<String> filenames, List<Neighbour> neighbours) {
        this.handler = handler;
        this.current = current;
        this.filenames = filenames;
        this.neighbours = neighbours;
        //  Cache of nodes
        this.cacheService = new CacheService(MAX_INDEX_SIZE, MAX_NODE_QUEUE_LENGTH);
        this.running = false;
        this.results = new ConcurrentHashMap<>();
        statistics = new Statistics();
        this.handleResponsesThread = new Thread(this::runHandleResponses);
        this.handleQueriesThread = new Thread(this::runHandleQueries);
        this.queryIdStore = CacheBuilder.newBuilder()
                .maximumSize(ID_STORE_INDEX_SIZE)
                .<String, Queue<String>>build().asMap();

        this.useCache = true;

    }

    /**
     * Starts handle replies thread and handle queries thread
     */
    public void start() {
        running = true;
        this.handleQueriesThread.start();
        this.handleResponsesThread.start();
    }

    /**
     * Thread to handle replies
     */
    private void runHandleResponses() {
        LOGGER.trace("Starting query response handling service.");
        while (running) {
            SearchResponse response = (SearchResponse) this.handler.receiveMessage(SearchResponse.ID);
            if (response == null) {
                continue;
            }
            if (Integer.parseInt(response.getQueryID()) == currentQueryID) {
                LOGGER.info(String.format("Response received matching current query: %s", response.toString()));
                this.updateResults(response.getNode(), response.getFilenames(), response.getHops());
            } else {
                LOGGER.info(String.format("Response received matching old query: %s", response.toString()));
            }
        }
        LOGGER.trace("Stopping query response handling service.");
    }

    /**
     * Thread to handle queries
     */
    private void runHandleQueries() {
        LOGGER.trace("Starting query handling service.");
        while (running) {
            SearchRequest request = (SearchRequest) this.handler.receiveMessage(SearchRequest.ID);
            if (request == null)
                continue;
            LOGGER.info(String.format("Request received %s", request.toString()));
            //check for already served queries
            if (!isNewQuery(request)) {
                continue;
            }
            statistics.addReceived(1);
            List<String> matches = searchUtils(request, request.getSender());
            if (matches.size() > 0) {
                statistics.addResolved(1);
                SearchResponse response = new SearchResponse(request.getQueryId(), matches.size(), this.current, request.getHops() + 1, matches);
                this.handler.sendMessage(request.getNode().getIp(), request.getNode().getPort(), response, false);
                LOGGER.info(String.format("Response sent %s", response.toString()));
            }
        }
        LOGGER.trace("Stopping query handling service.");
    }

    /**
     * Sets current search query
     *
     * @param query query
     */
    public synchronized void search(String query) {
        clear(); // Clear current query
        sentTime = System.currentTimeMillis();
        SearchRequest request = new SearchRequest(String.valueOf(currentQueryID), current, query, 0);
        List<String> matches = searchUtils(request, null);
        if (matches.size() > 0) {
            this.updateResults(current, matches, 0);
        }
    }

    /**
     * Initialize a search query
     *
     * @return list of filenames
     */
    private List<String> searchUtils(SearchRequest request, Node ignore) {
        String query = request.getFilename();
        List<String> matches = searchFiles(query);
        if (matches.size() > 0) {
            LOGGER.info("Found searched file in this node. Terminating the search.");
            return matches;
        }
        if (request.getHops() < TTL) {
            List<Node> bestNodes = selectBestNodes(query);
            // Ignore nodes indicated by ignore args
            if (ignore != null)
                bestNodes.remove(ignore);
            request.incrementHops();
            bestNodes.forEach(node -> {
                LOGGER.info(String.format("Sending query %s to neighbour %s ", request.toString(), node.toString()));
                this.handler.sendMessage(node.getIp(), node.getPort(), request, false);
            });
        } else {
            LOGGER.info("TTL reached. Terminating the search.");
        }
        return matches;
    }

    /**
     * Update results when queries are search and results are found
     *
     * @param node      Node containing the files
     * @param filenames filenames matching the query
     */
    private void updateResults(Node node, List<String> filenames, int hops) {
        synchronized (results) {
            if (!results.containsKey(node)) {
                Long latency = System.currentTimeMillis() - sentTime;
                ResultList temp = new ResultList(filenames);
                temp.setHops(hops);
                temp.setLatency(latency);
                results.put(node, temp);
            }
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
        List<Node> bestNodes = null;
        if (useCache) {
            bestNodes = cacheService.search(filename);
        }
        if (bestNodes == null)
            bestNodes = new ArrayList<>();

        int nodeGap = MAX_NODES - bestNodes.size();
        int fromNeighbours = nodeGap > 0 ? nodeGap + 2 : 2;

        synchronized (neighbours) {
            Collections.sort(neighbours);
            for (int i = 0; i < (fromNeighbours > neighbours.size() ? neighbours.size() : fromNeighbours); i++) {
                if (!neighbours.get(i).isLeft())
                    bestNodes.add(neighbours.get(i).getNode());
            }
        }

        return bestNodes;
    }

    /**
     * For searching local file names
     *
     * @param query a query to search files over
     * @return list of filenames matching (containing) query
     */
    private List<String> searchFiles(String query) {
        query = query.toLowerCase();
        List<String> found = new ArrayList<>();
        boolean isMatch;
        String[] fNameArry;
        for (String filename : filenames) {
            fNameArry = filename.toLowerCase().split(" +");
            isMatch = true;
            for (String keyword : query.split(" +")) {
                if (!Arrays.asList(fNameArry).contains(keyword)) {
                    isMatch = false;
                    break;
                }
            }
            if (isMatch) {
                LOGGER.debug(String.format("Found file with name %s for query %s", filename, query));
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
    public String getQuery() {
        return currentQuery;
    }

    /**
     * returns map containing search results
     *
     * @return map of nodes with respective files files
     */
    public Map<Node, ResultList> getResults() {
        return results;
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

    public Statistics getStatistics() {
        return statistics;
    }

    /**
     * Sets running status
     *
     * @param running state
     */
    public void setRunning(boolean running) {
        this.running = running;
        this.handleQueriesThread.interrupt();
        this.handleResponsesThread.interrupt();
    }

    /**
     * Clears the results list
     */
    public synchronized void clear() {
        this.results.clear();
        this.currentQuery = "";
        this.currentQueryID++;
    }

    public void setUseCache(boolean useCache) {
        this.useCache = useCache;
    }

    public synchronized void reset() {
        statistics.reset();
    }

    /**
     * Evaluation
     */
    public void autoSearch() {
        String[] queries = {"Twilight", "Jack", "American Idol", "Happy Feet", "Twilight saga", "Happy Feet", "Happy Feet", "Feet", "Happy Feet", "Twilight", "Windows", "Happy Feet", "Mission Impossible", "Twilight", "Windows 8", "The", "Happy", "Windows 8", "Happy Feet", "Super Mario", "Jack and Jill", "Happy Feet", "Impossible", "Happy Feet", "Turn Up The Music", "Adventures of Tintin", "Twilight saga", "Happy Feet", "Super Mario", "American Pickers", "Microsoft Office 2010", "Twilight", "Modern Family", "Jack and Jill", "Jill", "Glee", "The Vampire Diarie", "King Arthur", "Jack and Jill", "King Arthur", "Windows XP", "Harry Potter", "Feet", "Kung Fu Panda", "Lady Gaga", "Gaga", "Happy Feet", "Twilight", "Hacking", "King"};
        PrintWriter writer = null;
        try {
            writer = new PrintWriter("output1.txt", "UTF-8");
            for (String q : queries) {
                System.out.println("Searching " + q);
                search(q);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.err.println("Cannot wait :(");
                }
                Map<Node, ResultList> results = getResults();
                for (Node n : results.keySet()) {
                    System.out.println("Node responded " + n);
                    ResultList rs = results.get(n);
                    Long latency = rs.getLatency();
                    int hops = rs.getHops();
                    String filenames = String.valueOf(rs.getFilenames());
                    StringBuilder line = new StringBuilder();
                    line.append(q).append(" ");
                    line.append(n).append(" ");
                    line.append(latency).append(" ");
                    line.append(hops).append(" ");
                    line.append(filenames);
                    writer.println(line.toString());
                }
            }
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            writer.close();
        }
    }
}
