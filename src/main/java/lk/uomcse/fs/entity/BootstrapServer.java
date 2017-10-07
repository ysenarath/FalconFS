package lk.uomcse.fs.entity;

import org.apache.log4j.Logger;

public class BootstrapServer {
    private final static Logger LOGGER = Logger.getLogger(BootstrapServer.class.getName());

    private final String host;

    private final int port;

    public BootstrapServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
