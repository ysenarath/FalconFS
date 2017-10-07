package lk.uomcse.fs.models;

import lk.uomcse.fs.udp.UDPClient;

public class QueryClient extends UDPClient {
    /**
     * Creates the client for a server at host:port
     *
     * @param host name/ip of the server
     * @param port port of the server
     */
    public QueryClient(String host, int port) {
        super(host, port);
    }
}
