package lk.uomcse.fs.controller;

import lk.uomcse.fs.FalconFS;
import lk.uomcse.fs.model.Configuration;
import lk.uomcse.fs.utils.ListUtils;
import lk.uomcse.fs.view.ConfigView;
import lk.uomcse.fs.view.MainUI;

import javax.swing.*;
import java.io.IOException;

public class ConfigController {

    private final ConfigView view;

    private final Configuration model;

    /**
     * Cstr
     *
     * @param model Configuration model
     * @param view  View
     */
    public ConfigController(Configuration model, ConfigView view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);
        this.view.initialize();
    }

    /**
     * Updates view according to the current model
     */
    public void updateView() {
        this.view.setName(model.getName());
        this.view.setAddress(model.getAddress());
        this.view.setPort(model.getPort());
        this.view.setBootstrapServerAddress(model.getBootstrapServer().getAddress());
        this.view.setBootstrapServerPort(model.getBootstrapServer().getPort());
    }

    /**
     * Update model name
     *
     * @param name new name
     */
    public void updateName(String name) {
        model.setName(name);
    }

    /**
     * Update model address
     *
     * @param address new address
     */
    public void updateAddress(String address) {
        model.setAddress(address);
    }

    /**
     * Update model port
     *
     * @param port new port
     */
    public void updatePort(int port) {
        model.setPort(port);
    }

    /**
     * Updates bootstrap server address
     *
     * @param address new bootstrap server address
     */
    public void updateBootstrapServerAddress(String address) {
        model.setBootstrapServerAddress(address);
    }

    /**
     * Update  bootstrap server  port
     *
     * @param port new port
     */
    public void updateBootstrapServerPort(int port) {
        model.setBootstrapServerPort(port);
    }

    /**
     * Connects to the new instance
     */
    public void connect() {
        FalconFS fs = new FalconFS(this.model);
        MainUI ui = new MainUI();
        fs.getFilenames().addAll(ListUtils.randomSubList(model.getFiles(), 4, 2));
        MainController controller = new MainController(fs, ui);
        controller.updateView();
        view.close();
    }

    /**
     * Save configurations
     */
    public void save() {
        try {
            model.save();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
