package lk.uomcse.fs.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lk.uomcse.fs.controller.MainController;
import lk.uomcse.fs.model.FalconFS;
import lk.uomcse.fs.model.Statistics;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

/**
 * @author Dulanjaya
 * @since 11/2/2017
 */
public class MainUI {
    private final JFrame frame = new JFrame("FalconFS");
    private JPanel mainPanel;
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
    private JPanel pnlStatistics;
    private JButton updateButton;
    private JLabel lblRecieved;
    private JLabel lblResolved;
    private JLabel lblForwarded;
    private JCheckBox Cache;
    private MainController controller;

    public MainUI() {
    }


    public void setController(MainController controller) {
        this.controller = controller;
    }

    public void initialize() {
        this.initializeFrame();
        this.setupComponents();
    }

    private void initializeFrame() {
        frame.setContentPane(this.mainPanel);
        frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.addWindowListener(new WindowAdapter() {
            /**
             * Invoked when a window has been closed.
             *
             * @param e
             */
            @Override
            public void windowClosing(WindowEvent e) {
                final JDialog shutdownMessage = new JDialog(frame, Dialog.ModalityType.APPLICATION_MODAL);
                shutdownMessage.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                shutdownMessage.setLayout(new GridLayout());
                shutdownMessage.setTitle("FalconFS");
                Label lblShutdown = new Label("FalconFS is shutting down...");
                lblShutdown.setFont(new Font("Helvatica", Font.PLAIN, 20));
                shutdownMessage.add(lblShutdown);
                shutdownMessage.pack();
                shutdownMessage.setLocationRelativeTo(frame);
                new Thread(() -> {
                    MainUI.this.controller.stop();
                    shutdownMessage.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
                    shutdownMessage.dispose();
                }).start();
                shutdownMessage.setVisible(true);

            }
        });
    }

    private void setupComponents() {
        this.setupThreadComponents();
        this.setupSearchComponents();
        this.setupFileComponents();
        this.setupConsoleComponent();
        this.setupSelfNodeInfo();
        this.setupStatistics();
        Cache.addActionListener(e -> controller.getQueryService().setUseCache(Cache.isSelected()) );
    }

    private void setupStatistics() {
        updateButton.addActionListener(e -> {
            Statistics stat = controller.getQueryService().getStatistics();
            lblForwarded.setText(String.valueOf(stat.getForwarded()));
            lblRecieved.setText(String.valueOf(stat.getReceived()));
            lblResolved.setText(String.valueOf(stat.getResolved()));
        });
    }

    private void setupThreadComponents() {
        final ResultTableModel resultTableModel = new ResultTableModel(controller.getQueryService());
        tblResults.setModel(resultTableModel);

        final NeighborTableModel neighborTableModel = new NeighborTableModel(controller.getNeighbours());
        tblNeighbors.setModel(neighborTableModel);

        // center align values
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        tblNeighbors.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblNeighbors.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tblNeighbors.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        tblResults.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        tblResults.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        tblResults.getColumnModel().getColumn(3).setCellRenderer(rightRenderer);

        // adding progress bar
        tblNeighbors.getColumnModel().getColumn(3).setCellRenderer(new ProgressCellRenderer());

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
        List<String> filenames = controller.getFilenames();
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
                filenames.add(newFilename.toLowerCase());
                filenameModel.fireTableDataChanged();
            }
        });

    }

    private void setupSearchComponents() {
        this.txtSearch.addActionListener(e -> {
            if (txtSearch.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Search string is empty!", "FalconFS", JOptionPane.ERROR_MESSAGE);
            } else {
                controller.getQueryService().search(this.txtSearch.getText());
            }
        });
        this.btnSearch.addActionListener(e -> {
            if (txtSearch.getText().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Search string is empty!", "FalconFS", JOptionPane.ERROR_MESSAGE);
            } else {
                controller.getQueryService().search(this.txtSearch.getText());
            }

        });

        this.btnClear.addActionListener(e -> {
            this.txtSearch.setText("");
            this.controller.getQueryService().clear();
        });
    }

    private void setupConsoleComponent() {
        txtConsole.setBackground(Color.BLACK);
        txtConsole.setForeground(Color.GREEN);
        txtConsole.setEditable(false);
        Logger.getRootLogger().addAppender(new AppenderSkeleton() {
            @Override
            protected void append(LoggingEvent loggingEvent) {
                if (loggingEvent.getLevel() == Level.INFO) {
                    txtConsole.insert(loggingEvent.getMessage() + "\n", 0);
                    txtConsole.repaint();
                }
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
        this.lblName.setText("Name: " + controller.getName());
        this.lblIP.setText("IP: " + controller.getSelf().getIp());
        this.lblPort.setText("Port: " + Integer.toString(controller.getSelf().getPort()));
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(2, 4, new Insets(5, 5, 5, 5), -1, -1));
        pnlLeft = new JPanel();
        pnlLeft.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(pnlLeft, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlSearch = new JPanel();
        pnlSearch.setLayout(new GridLayoutManager(2, 4, new Insets(10, 10, 10, 10), -1, -1));
        pnlLeft.add(pnlSearch, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlSearch.setBorder(BorderFactory.createTitledBorder("Search for a File"));
        lblSearch = new JLabel();
        lblSearch.setText("Search");
        pnlSearch.add(lblSearch, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtSearch = new JTextField();
        pnlSearch.add(txtSearch, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        btnSearch = new JButton();
        btnSearch.setText("Search");
        pnlSearch.add(btnSearch, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        pnlSearch.add(spacer1, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, new Dimension(50, -1), null, 0, false));
        btnClear = new JButton();
        btnClear.setText("Clear");
        pnlSearch.add(btnClear, new GridConstraints(1, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        pnlResults = new JPanel();
        pnlResults.setLayout(new GridLayoutManager(1, 1, new Insets(10, 10, 10, 10), -1, -1));
        pnlLeft.add(pnlResults, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlResults.setBorder(BorderFactory.createTitledBorder("Search Results"));
        final JScrollPane scrollPane1 = new JScrollPane();
        pnlResults.add(scrollPane1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tblResults = new JTable();
        scrollPane1.setViewportView(tblResults);
        pnlFiles = new JPanel();
        pnlFiles.setLayout(new GridLayoutManager(2, 2, new Insets(10, 10, 10, 10), -1, -1));
        pnlLeft.add(pnlFiles, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlFiles.setBorder(BorderFactory.createTitledBorder("Files"));
        final JScrollPane scrollPane2 = new JScrollPane();
        pnlFiles.add(scrollPane2, new GridConstraints(0, 0, 1, 2, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(-1, 150), null, 0, false));
        tblFiles = new JTable();
        scrollPane2.setViewportView(tblFiles);
        btnAddFile = new JButton();
        btnAddFile.setText("Add Files");
        pnlFiles.add(btnAddFile, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(70, -1), null, 0, false));
        final Spacer spacer2 = new Spacer();
        pnlFiles.add(spacer2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer3 = new Spacer();
        pnlLeft.add(spacer3, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pnlRight = new JPanel();
        pnlRight.setLayout(new GridLayoutManager(3, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(pnlRight, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlNeighbors = new JPanel();
        pnlNeighbors.setLayout(new GridLayoutManager(2, 1, new Insets(10, 10, 10, 10), -1, -1));
        pnlRight.add(pnlNeighbors, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlNeighbors.setBorder(BorderFactory.createTitledBorder("Neighbors:"));
        final JScrollPane scrollPane3 = new JScrollPane();
        pnlNeighbors.add(scrollPane3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        tblNeighbors = new JTable();
        scrollPane3.setViewportView(tblNeighbors);
        pnlConsole = new JPanel();
        pnlConsole.setLayout(new GridLayoutManager(1, 2, new Insets(10, 10, 10, 10), -1, -1));
        pnlRight.add(pnlConsole, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JScrollPane scrollPane4 = new JScrollPane();
        pnlConsole.add(scrollPane4, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        txtConsole = new JTextArea();
        txtConsole.setText("");
        scrollPane4.setViewportView(txtConsole);
        final Spacer spacer4 = new Spacer();
        pnlConsole.add(spacer4, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        pnlSelfNode = new JPanel();
        pnlSelfNode.setLayout(new GridLayoutManager(3, 2, new Insets(10, 10, 10, 0), -1, -1));
        pnlRight.add(pnlSelfNode, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlSelfNode.setBorder(BorderFactory.createTitledBorder("Self-Node"));
        lblName = new JLabel();
        lblName.setText("Name: ");
        pnlSelfNode.add(lblName, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer5 = new Spacer();
        pnlSelfNode.add(spacer5, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        lblIP = new JLabel();
        lblIP.setText("IP: ");
        pnlSelfNode.add(lblIP, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        lblPort = new JLabel();
        lblPort.setText("Label");
        pnlSelfNode.add(lblPort, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer6 = new Spacer();
        pnlRight.add(spacer6, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        final Spacer spacer7 = new Spacer();
        mainPanel.add(spacer7, new GridConstraints(0, 2, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
