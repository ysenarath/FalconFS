package lk.uomcse.fs.controller;

import lk.uomcse.fs.FalconFS;
import lk.uomcse.fs.entity.Neighbour;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.model.QueryService;
import lk.uomcse.fs.utils.exceptions.BootstrapException;
import lk.uomcse.fs.view.MainUI;

import javax.swing.*;
import java.util.List;

public class MainController {
    private final MainUI view;
    private final FalconFS model;

    public MainController(FalconFS model, MainUI view) {
        this.model = model;
        this.view = view;
        try {
            this.model.start();
            this.view.setController(this);
            this.view.initialize();
        } catch (BootstrapException e) {
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
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

    public void updateView() {
    }

    public void stop() {
        model.stop();
    }
}
