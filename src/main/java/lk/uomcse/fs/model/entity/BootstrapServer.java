package lk.uomcse.fs.model.entity;

import org.apache.log4j.Logger;

public class BootstrapServer {
    private final static Logger LOGGER = Logger.getLogger(BootstrapServer.class.getName());

    private String address;

    private int port;

    public BootstrapServer(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
