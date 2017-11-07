package lk.uomcse.fs.view;

import lk.uomcse.fs.entity.Neighbour;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.model.QueryService;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

/**
 * @author Dulanjaya
 * @since 11/2/2017
 */
public class MainUI {
    private final JFrame frame = new JFrame("FalconFS");
    private JPanel pnlMain;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JPanel pnlLeft;
    private JPanel pnlRight;
    private JLabel lblSearch;
    private JPanel pnlSearch;
    private JButton btnClear;
    private JPanel pnlResults;
    private JTable tblResults;
    private JButton btnAddFile;
    private JTable tblFiles;
    private JPanel pnlFiles;
    private JTable tblNeighbors;
    private JPanel pnlNeighbors;
    private JPanel pnlConsole;
    private JTextArea txtConsole;
    private JPanel pnlSelfNode;
    private JLabel lblName;
    private JLabel lblIP;
    private JLabel lblPort;

    // Models
    private java.util.List<Neighbour> neighbors;
    private QueryService queryService;
    private java.util.List<String> filenames;
    private Node me;

    public MainUI(Node me, java.util.List<Neighbour> neighbors, QueryService queryService, java.util.List<String> filenames) {
        this.me = me;
        this.neighbors = neighbors;
        this.queryService = queryService;
        this.filenames = filenames;

        this.initializeFrame();
        this.setupComponents();
    }

    private void initializeFrame() {
        frame.setContentPane(this.pnlMain);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void setupComponents() {
        this.setupThreadComponents();
        this.setupSearchComponents();
        this.setupFileComponents();
        this.setupConsoleComponent();
        this.setupSelfNodeInfo();
    }

    private void setupThreadComponents() {
        final ResultTableModel resultTableModel = new ResultTableModel(queryService);
        tblResults.setModel(resultTableModel);

        final NeighborTableModel neighborTableModel = new NeighborTableModel(neighbors);
        tblNeighbors.setModel(neighborTableModel);

        new Thread(() -> {
            boolean isActive = true;
            while (isActive) {
                resultTableModel.fireTableDataChanged();
                neighborTableModel.fireTableDataChanged();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    private void setupFileComponents() {
        final FilenameModel filenameModel = new FilenameModel(filenames);
        tblFiles.setModel(filenameModel);

        this.btnAddFile.addActionListener(e -> {
            String newFilename = JOptionPane.showInputDialog(frame, "Enter the filename", "FalconFS", JOptionPane.INFORMATION_MESSAGE);
            if (null == newFilename) {
                return;
            }
            if (newFilename.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Filename is empty!", "FalconFS", JOptionPane.ERROR_MESSAGE);
            } else if (filenames.contains(newFilename)) {
                JOptionPane.showMessageDialog(frame, "Filename is already added!", "FalconFS", JOptionPane.ERROR_MESSAGE);
            } else {
                filenames.add(newFilename);
                filenameModel.fireTableDataChanged();
            }
        });

    }

    private void setupSearchComponents() {
        this.btnSearch.addActionListener(e -> {
            if (txtSearch.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Search string is empty!", "FalconFS", JOptionPane.ERROR_MESSAGE);
            } else {
                this.queryService.search(this.txtSearch.getText());
            }

        });

        this.btnClear.addActionListener(e -> {
            this.txtSearch.setText("");
        });
    }

    private void setupConsoleComponent() {
        txtConsole.setBackground(Color.BLACK);
        txtConsole.setForeground(Color.GREEN);
        Logger.getRootLogger().addAppender(new AppenderSkeleton() {
            @Override
            protected void append(LoggingEvent loggingEvent) {
                txtConsole.insert(loggingEvent.getMessage() + "\n", 0);
                txtConsole.repaint();
            }

            @Override
            public void close() {

            }

            @Override
            public boolean requiresLayout() {
                return false;
            }
        });
    }

    private void setupSelfNodeInfo() {
        this.lblName.setText("Name: " + me.toString());
        this.lblIP.setText("IP: " + me.getIp());
        this.lblPort.setText("Port: " + Integer.toString(me.getPort()));
    }
}
