package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.Node;

import java.util.*;

/**
 * Select the best nodes to request the file
 */
public class NodeSelector {

    private static final int MAX_NODES = 5;

    private static final int MIN_REQ_HEALTH = 50;

    private final List<Node> neighbours;

    private final CacheService cacheService;

    public NodeSelector(List<Node> neighbours, CacheService cacheService) {
        this.neighbours = neighbours;
        this.cacheService = cacheService;
    }

    /**
     * Select best nodes from the cached nodes and the neighbours
     *
     * @param fileName
     * @return
     */
    public List<Node> selectBestNodes(String fileName) {

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

}
