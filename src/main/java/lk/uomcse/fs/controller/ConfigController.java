package lk.uomcse.fs.controller;

import lk.uomcse.fs.model.Configuration;
import lk.uomcse.fs.model.Protocol;
import lk.uomcse.fs.utils.exceptions.BootstrapException;
import lk.uomcse.fs.utils.exceptions.InitializationException;
import lk.uomcse.fs.view.ConfigView;

import javax.swing.*;
import java.io.IOException;

public class ConfigController {

    private final ConfigView view;

    private final Configuration configs;

    /**
     * Constructor
     *
     * @param configs Configuration configs
     * @param view    View
     */
    public ConfigController(Configuration configs, ConfigView view) {
        this.configs = configs;
        this.view = view;
        this.view.setController(this);
        this.view.initialize();
    }

    /**
     * Updates view according to the current configs
     */
    public void updateView() {
        this.view.setName(configs.getName());
        this.view.setAddress(configs.getAddress());
        this.view.setPort(configs.getPort());
        this.view.setBootstrapServerAddress(configs.getBootstrapServer().getAddress());
        this.view.setBootstrapServerPort(configs.getBootstrapServer().getPort());
    }

    /**
     * Update configs name
     *
     * @param name new name
     */
    public void updateName(String name) {
        configs.setName(name);
    }

    /**
     * Update configs address
     *
     * @param address new address
     */
    public void updateAddress(String address) {
        configs.setAddress(address);
    }

    /**
     * Update configs port
     *
     * @param port new port
     */
    public void updatePort(int port) {
        configs.setPort(port);
    }

    /**
     * Updates bootstrap server address
     *
     * @param address new bootstrap server address
     */
    public void updateBootstrapServerAddress(String address) {
        configs.setBootstrapServerAddress(address);
    }

    /**
     * Update  bootstrap server  port
     *
     * @param port new port
     */
    public void updateBootstrapServerPort(int port) {
        configs.setBootstrapServerPort(port);
    }


    /**
     * Updates sender type
     *
     * @param type update sender type to REST if this value is zero
     */
    public void updateSenderType(String type) {
        if (type.toLowerCase().equals("rest"))
            configs.setProtocol(Protocol.REST);
        else
            configs.setProtocol(Protocol.UDP);
    }

    /**
     * Connects to the new instance
     */
    public void connect() {
        if (configs.getProtocol().equals(Protocol.REST)) {
            if (configs.getBootstrapPort() == configs.getPort()) {
                String msg = "Invalid port combination. Please use different ports for primary and bootstrapping ports.";
                JOptionPane.showMessageDialog(view.getFrame(), msg, "Configuration Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        try {
            configs.connect();
            view.setVisible(false);
        } catch (BootstrapException e) {
            JOptionPane.showMessageDialog(view.getFrame(), e.getMessage(), "Bootstrap Error", JOptionPane.ERROR_MESSAGE);
        } catch (InitializationException e) {
            JOptionPane.showMessageDialog(view.getFrame(), e.getMessage(), "Initialization Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Save configurations
     */
    public void save() {
        try {
            configs.save();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "IO Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public int getPort() {
        return configs.getPort();
    }

    public void updateBootstrapPort(Integer value) {
        configs.setBootstrapPort(value);
    }
}
