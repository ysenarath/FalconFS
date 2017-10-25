package lk.uomcse.fs.view;

import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.model.QueryService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * @author Dulanjaya
 * @since 10/24/2017
 */
public class FrameView {
    private ArrayList<Node> neighbors;
    private QueryService queryService;

    private JFrame frmMain;
    private JPanel pnlMain;

    private JPanel pnlNeighbors;
    private JTable tblNeighbors;
    private JScrollPane scrollPane;

    private JPanel pnlSearch;
    private JTextField txtSearch;
    private JButton btnSearch;
    private JLabel lblSearch;
    private JButton btnClear;
    private JPanel pnlResults;

    public FrameView(ArrayList<Node> neighbors, QueryService queryService) {
        this.neighbors = neighbors;
        this.queryService = queryService;
        initComponents();
        setMainPanel();
    }

    /**
     * Initialize UI components
     */
    private void initComponents() {
        frmMain = new JFrame("FalconFS");

        pnlMain = new JPanel();

        pnlSearch = new JPanel();
        pnlResults = new JPanel();
        txtSearch = new JTextField("");
        btnSearch = new JButton("Search");
        btnSearch.addActionListener(e -> System.out.println(queryService.search(txtSearch.getText(), 10)));
        btnClear = new JButton("Clear");
        btnClear.addActionListener(e -> txtSearch.setText(""));
        lblSearch = new JLabel("Search Query");


        pnlNeighbors = new JPanel();
        final NeighborTableModel tblmdlNeighbor = new NeighborTableModel(neighbors);
        tblNeighbors = new JTable(tblmdlNeighbor);
        scrollPane = new JScrollPane(tblNeighbors);
        new Thread(() -> {
            while (true) {
                tblmdlNeighbor.fireTableDataChanged();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
    }

    private void setMainPanel() {
        frmMain.setSize(1000, 600);
        GridBagConstraints gbc = new GridBagConstraints();

        pnlMain.setLayout(new GridBagLayout());
        frmMain.setContentPane(pnlMain);

        gbc.weightx = 1;
        gbc.weighty = 9;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipady = 100;
        gbc.gridx = 0;
        gbc.gridy = 1;
        setSearchPanel();
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
        pnlSearch.setBackground(Color.DARK_GRAY);

        GridBagLayout layout = new GridBagLayout();
        pnlSearch.setLayout(layout);

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

        pnlResults.setBackground(Color.cyan);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridwidth = 6;
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weighty = 20;
        pnlSearch.add(pnlResults, gbc);
    }

    private void setNeighborsPanel() {
        pnlNeighbors.setBackground(Color.GREEN);

        pnlNeighbors.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.BOTH;
        pnlNeighbors.add(scrollPane, gbc);
    }
}
