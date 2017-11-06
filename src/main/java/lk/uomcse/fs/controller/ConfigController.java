package lk.uomcse.fs.controller;

import lk.uomcse.fs.FalconFS;
import lk.uomcse.fs.model.Configurations;
import lk.uomcse.fs.utils.ListUtils;
import lk.uomcse.fs.view.ConfigView;

import javax.swing.*;
import java.io.IOException;

public class ConfigController {

    private final ConfigView view;

    private final Configurations model;

    public ConfigController(Configurations model, ConfigView view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);
        this.view.initialize();
    }

    public void connect() {
        FalconFS fs = new FalconFS(model);
        fs.getFilenames().addAll(ListUtils.randomSubList(model.getFiles(), 4, 2));
        fs.start();
    }

    public void updateView() {
        this.view.setName(model.getName());
        this.view.setAddress(model.getAddress());
        this.view.setPort(model.getPort());
        this.view.setBootstrapServerAddress(model.getBootstrapServer().getAddress());
        this.view.setBootstrapServerPort(model.getBootstrapServer().getPort());
    }

    public void updateName(String name) {
        model.setName(name);
    }

    public void updateAddress(String address) {
        System.out.println(address);
        model.setAddress(address);
    }

    public void updatePort(int port) {
        model.setPort(port);
    }

    public void updateBootstrapServerAddress(String address) {
        model.setBootstrapServerAddress(address);
    }

    public void updateBootstrapServerPort(int port) {
        model.setBootstrapServerPort(port);
    }

    public void save() {
        try {
            model.save();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
