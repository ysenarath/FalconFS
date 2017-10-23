package lk.uomcse.fs.entity;

import java.util.Comparator;
import java.util.Random;

public class Node implements Comparator<Node>, Comparable<Node> {
    private String ip;

    private int port;

    //health of the node. should be in between 0 and 100
    private Integer health;

    /**
     * BeatCount of a node.
     */
    private int beatCount;

    public Node(String ip, int port) {
        this.ip = ip;
        this.port = port;
        Random rand = new Random();

        this.health = rand.nextInt(100) + 1;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        if (health > 100) {
            this.health = 100;
        } else if (health < 0) {
            this.health = 0;
        } else {
            this.health = health;
        }
    }

    /**
     * Returns the beat count and clears {@code beatCount}
     *
     * @return {@code beatCount}
     */
    public synchronized int getBeatCount() {
        int tem = beatCount;
        beatCount = 0;
        return tem;
    }

    /**
     * Increments the {@code beatCount} by 1
     */
    public synchronized void setBeatCount() {
        beatCount ++;
    }

    @Override
    public String toString() {
        return String.format("Node{ip='%s', port=%s}", ip, port);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (port != node.port) return false;
        return ip != null ? ip.equals(node.ip) : node.ip == null;
    }

    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }

    @Override
    public int compare(Node o1, Node o2) {
        return o1.health - o2.health;
    }

    @Override
    public int compareTo(Node o) {
        return (o.health).compareTo(this.health);
    }
}
