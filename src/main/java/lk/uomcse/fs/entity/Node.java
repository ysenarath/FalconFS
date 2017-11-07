package lk.uomcse.fs.entity;


public class Node {
    private String ip;

    private int port;

    /**
     * For Jakson support
     */
    public Node(){}


    /**
     * Node containing IP and port of an application instance
     *
     * @param ip   ip
     * @param port port
     */
    public Node(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * Gets IP
     *
     * @return ip address
     */
    public String getIp() {
        return ip;
    }

    /**
     * Sets ip
     *
     * @param ip an ip address
     */
    public void setIp(String ip) {
        this.ip = ip;
    }

    /**
     * Port of the node
     *
     * @return port
     */
    public int getPort() {
        return port;
    }


    /**
     * Sets port
     *
     * @param port a port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Convert node to a string
     *
     * @return string representations
     */
    @Override
    public String toString() {
        return String.format("Node{ip='%s', port=%s}", ip, port);
    }

    /**
     * Whether 2 objects are equal
     *
     * @param o object
     * @return whether objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (port != node.port) return false;
        return ip != null ? ip.equals(node.ip) : node.ip == null;
    }

    /**
     * Hashcode
     *
     * @return hashcode
     */
    @Override
    public int hashCode() {
        int result = ip != null ? ip.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }
}
