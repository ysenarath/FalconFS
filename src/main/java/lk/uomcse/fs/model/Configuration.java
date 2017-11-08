package lk.uomcse.fs.model;

import lk.uomcse.fs.entity.BootstrapServer;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Properties;

public class Configuration {

    private static final Logger LOGGER = Logger.getLogger(Configuration.class.getName());

    private String name;
    private String address;
    private int port;
    private BootstrapServer bootstrapServer;
    private String configPath;
    private List<String> files;
    private RequestHandler.SenderType senderType;

    public Configuration(String name, String address, int port, BootstrapServer bootstrapServer, String configPath, List<String> files) {
        this.name = name;
        this.address = address;
        this.port = port;
        this.bootstrapServer = bootstrapServer;
        this.configPath = configPath;
        this.files = files;
        this.senderType = RequestHandler.SenderType.UDP;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public BootstrapServer getBootstrapServer() {
        return bootstrapServer;
    }

    public String getConfigPath() {
        return configPath;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBootstrapServerPort(int port) {
        this.bootstrapServer.setPort(port);
    }

    public void setBootstrapServerAddress(String address) {
        this.bootstrapServer.setAddress(address);
    }

    public RequestHandler.SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(RequestHandler.SenderType senderType) {
        this.senderType = senderType;
    }

    public void save() throws IOException {
        Properties prop = new Properties();
        OutputStream output = null;
        if (configPath == null) {
            LOGGER.info("Unable to save configurations");
            throw new IOException("Unable to save configurations. Please try different config location.");
        }
        try {
            output = new FileOutputStream(configPath);
            // set the properties value
            prop.setProperty("bs.ip", getBootstrapServer().getAddress());
            prop.setProperty("bs.port", String.valueOf(getBootstrapServer().getPort()));
            prop.setProperty("fs.name", this.name);
            prop.setProperty("fs.ip", this.address);
            prop.setProperty("fs.port", String.valueOf(this.port));
            prop.setProperty("files", String.join(",", this.files));
            // save properties to project root folder
            prop.store(output, null);
        } catch (IOException io) {
            LOGGER.error("Unable to save config file due to IO Error.");
            throw new IOException("Unable to save config file due to an IO Error.");
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    // e.printStackTrace();
                    LOGGER.debug("Unable to close the stream due to an IO Error.");
                }
            }

        }
    }
}
