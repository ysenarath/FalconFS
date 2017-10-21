package lk.uomcse.fs.model;

import com.google.common.cache.CacheBuilder;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.Queues;
import lk.uomcse.fs.entity.Node;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentMap;

/**
 * Handle the cached node service
 */
public class CacheService {


    private final ConcurrentMap<String, Queue<Node>> cacheTable;


    private int indexSize, queueLength;


    /**
     * Allocates the cache service
     *
     * @param indexSize   max number of key words stored
     * @param queueLength max number of nodes store for single key word
     */
    public CacheService(int indexSize, int queueLength) {

        if (indexSize > 0 && queueLength > 0) {
            cacheTable = CacheBuilder.newBuilder()
                    .maximumSize(indexSize)
                    .<String, Queue<Node>>build().asMap();
            this.indexSize = indexSize;
            this.queueLength = queueLength;
        } else {
            throw new InvalidParameterException("The indexSize and queueLength should be greater than zero");
        }
    }


    /**
     * Update the cache table with new information
     *
     * @param node      newly discovered nodes
     * @param fileNames list of file names that are in the given node
     * @return if registering node to the cache was successful
     */
    public boolean update(Node node, List<String> fileNames) {
        try {
            for (String fileName : fileNames) {
                fileName = fileName.toLowerCase();
                for (String keyWord : fileName.trim().split(" +")) {
                    synchronized (cacheTable) {
                        cacheTable.putIfAbsent(keyWord, Queues.synchronizedQueue(EvictingQueue.create(queueLength)));
                        Queue<Node> nodes = cacheTable.get(keyWord);
                        // TODO check if nodes requires another synchronized block
                        if (!nodes.contains(node)) {
                            nodes.add(node);
                        }
                    }
                }
            }
            return true;

            //TODO handle exceptions properly
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * Search if the file cache has any information about node containing the given file
     *
     * @param fileName the file name to search. Can be collection of several key words
     *                 Ex : "Windows 8", "Microsoft office"
     *                 the key word will be extracted by separating with spaces
     * @return List of nodes containing file or null if no such nodes in cache tabel
     */
    public List<Node> search(String fileName) {
        List<Node> fileNodes = new ArrayList<>();
        fileName = fileName.toLowerCase();
        // TODO do we need mutual exclusion here
        for (String keyWord :
                fileName.trim().split(" +")) {

            Queue<Node> keyWordNodes = cacheTable.get(keyWord);

            if (fileNodes.isEmpty()) {
                fileNodes.addAll(keyWordNodes);
            } else {
                for (Node node :
                        fileNodes) {
                    if (!keyWordNodes.contains(node)) {
                        fileNodes.remove(node);
                    }
                }
            }

            if (fileNodes.isEmpty()) {
                return null;
            }
        }
        return fileNodes;
    }


    public int getIndexSize() {
        return indexSize;
    }


    public int getQueueLength() {
        return queueLength;
    }


    public ConcurrentMap<String, Queue<Node>> getCacheTable() {
        return cacheTable;
    }
}
