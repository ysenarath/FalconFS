package lk.uomcse.fs.view;

import lk.uomcse.fs.model.entity.Neighbour;
import lk.uomcse.fs.model.entity.Node;
import lk.uomcse.fs.model.service.QueryService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * User Interface Starts from here
 *
 * @author Dulanjaya
 * @since Phase 1
 */
public class FrameView {
    // Models
    private ArrayList<Neighbour> neighbors;
    private QueryService queryService;
    private ArrayList<String> filenames;
    private Node me;

    // UI Components
    // Main UI
    private JFrame frmMain;
    private JPanel pnlMain;

    // Neighbors
    private JPanel pnlNeighbors;
    private JTable tblNeighbors;
    private JScrollPane scrollNeighbor;
    private JLabel lblNeighbors;

    // Search Panel
    private JPanel pnlSearch;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JLabel lblSearch;
    private JButton btnClear;

    // Results Panel
    private JPanel pnlResults;
    private JScrollPane scrollResults;

    // FileSystem Panel
    private JPanel pnlFileNames;
    private JLabel lblFilenames;
    private JTable tblFilenames;
    private JButton btnAddFilename;
    private JScrollPane scrollFilename;


    /**
     * Initializes the FrameView
     *
     * @param me
     * @param neighbors
     * @param queryService
     * @param filenames
     */
    public FrameView(Node me, ArrayList<Neighbour> neighbors, QueryService queryService, ArrayList<String> filenames) {
        this.me = me;
        this.neighbors = neighbors;
        this.queryService = queryService;
        this.filenames = filenames;
        initComponents();
        setMainPanel();
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        frmMain = new JFrame("FalconFS - " + me.getIp() + ":" + Integer.toString(me.getPort()));
        pnlMain = new JPanel();

        pnlSearch = new JPanel();
        pnlResults = new JPanel();
        txtSearch = new JTextField("");
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> queryService.search(txtSearch.getText()));
        btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> txtSearch.setText(""));
        lblSearch = new JLabel("Search Query");


        pnlNeighbors = new JPanel();
        lblNeighbors = new JLabel("Neighbor Nodes:");
        final NeighborTableModel tblmdlNeighbor = new NeighborTableModel(neighbors);
        tblNeighbors = new JTable(tblmdlNeighbor);
        scrollNeighbor = new JScrollPane(tblNeighbors);

        final ResultTableModel tblmdlResult = new ResultTableModel(queryService);
        JTable tblResults = new JTable(tblmdlResult);
        scrollResults = new JScrollPane(tblResults);

        new Thread(() -> {
            while (true) {
                tblmdlNeighbor.fireTableDataChanged();
                tblmdlResult.fireTableDataChanged();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();

        pnlFileNames = new JPanel();
        lblFilenames = new JLabel("Filenames: ");
        btnAddFilename = new JButton("Add");
        final FilenameModel filenameModel = new FilenameModel(filenames);
        tblFilenames = new JTable(filenameModel);
        scrollFilename = new JScrollPane(tblFilenames);

        btnAddFilename.addActionListener(e -> {
            String filename = JOptionPane.showInputDialog("Enter the filename");
            if (!filenames.contains(filename)) {
                filenames.add(filename.trim().toLowerCase());
                filenameModel.fireTableDataChanged();
            }

        });
    }

    private void setMainPanel() {
        frmMain.setSize(1000, 600);
        GridBagConstraints gbc = new GridBagConstraints();

        pnlMain.setLayout(new GridLayout(1, 1));
        frmMain.setContentPane(pnlMain);

        gbc.anchor = GridBagConstraints.NORTH;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 1;
        setSearchPanel();
        setResultPanel();
        pnlMain.add(pnlSearch, gbc);

        setNeighborsPanel();
        gbc.weightx = 1;
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnlMain.add(pnlNeighbors, gbc);

        frmMain.setPreferredSize(frmMain.getSize());
        frmMain.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frmMain.setLocationRelativeTo(null);
        frmMain.setResizable(false);
        frmMain.setVisible(true);
    }

    private void setSearchPanel() {
        pnlSearch.setLayout(new GridBagLayout());
        pnlSearch.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(20, 20, 0, 20);

        lblSearch.setFont(new Font("Serif", Font.PLAIN, 20));
        gbc.weighty = 1;
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        pnlSearch.add(lblSearch, gbc);

        txtSearch.setFont(new Font("Serif", Font.PLAIN, 20));
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.gridx = 1;
        gbc.gridy = 0;
        pnlSearch.add(txtSearch, gbc);

        gbc.insets = new Insets(0, 20, 0, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        gbc.gridx = 4;
        gbc.gridy = 1;
        pnlSearch.add(btnSearch, gbc);

        gbc.gridx = 5;
        gbc.gridy = 1;
        pnlSearch.add(btnClear, gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 6;
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weighty = 20;
        pnlSearch.add(pnlResults, gbc);

        setFilenamePanel();
        gbc.gridy = 3;
        gbc.gridx = 0;
        gbc.weighty = 5;
        pnlSearch.add(pnlFileNames, gbc);
    }

    private void setNeighborsPanel() {
//        pnlNeighbors.setBackground(Color.GREEN);
        pnlNeighbors.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        pnlNeighbors.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        pnlNeighbors.add(lblNeighbors, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.BOTH;
        pnlNeighbors.add(scrollNeighbor, gbc);
    }

    private void setResultPanel() {
        pnlResults.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        JLabel lblResults = new JLabel("Search Results");
        pnlResults.add(lblResults, gbc);

        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 30;
        gbc.fill = GridBagConstraints.BOTH;
        pnlResults.add(scrollResults, gbc);
    }

    private void setFilenamePanel() {
        pnlFileNames.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTHEAST;
//        gbc.insets = new Insets(0,5,5,5);
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.fill = GridBagConstraints.BOTH;
        pnlFileNames.add(lblFilenames, gbc);

        gbc.anchor = GridBagConstraints.NORTHEAST;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 18;
        gbc.weighty = 10;
        gbc.gridx = 0;
        gbc.gridy = 1;
        pnlFileNames.add(scrollFilename, gbc);

        gbc.gridx = 1;
        gbc.weightx = 2;
        pnlFileNames.add(btnAddFilename, gbc);


    }
}
