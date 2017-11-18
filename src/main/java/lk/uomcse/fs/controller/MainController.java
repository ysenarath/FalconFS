package lk.uomcse.fs.controller;

import lk.uomcse.fs.model.FalconFS;
import lk.uomcse.fs.model.entity.Neighbour;
import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.service.QueryService;
import lk.uomcse.fs.utils.exceptions.BootstrapException;
import lk.uomcse.fs.view.MainUI;

import javax.swing.*;
import java.util.List;

public class MainController {
    private final MainUI view;
    private final FalconFS model;
    private boolean autoSearch;

    public MainController(FalconFS model, MainUI view) {
        this.model = model;
        this.view = view;
        this.view.setController(this);
        autoSearch = false;
    }

    /**
     * Starts the FalconFS services
     *
     * @throws BootstrapException when there is a problem in bootstrapping
     */
    public void start() throws BootstrapException {
        this.model.start();
        this.view.initialize();
    }

    public QueryService getQueryService() {
        return model.getQueryService();
    }

    public List<Neighbour> getNeighbours() {
        return model.getNeighbours();
    }

    public List<String> getFilenames() {
        return model.getFilenames();
    }

    public Node getSelf() {
        return model.getSelf();
    }

    public void stop() {
        try {
            model.stop();
        } catch (BootstrapException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Bootstrap Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }

    public String getName() {
        return model.getName();
    }

    public void setAutoSearch(boolean value) {
        autoSearch = value;
    }

    public boolean getAutoSearch() {
        return autoSearch;
    }
}
