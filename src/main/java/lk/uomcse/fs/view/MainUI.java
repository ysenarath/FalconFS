package lk.uomcse.fs.view;

import lk.uomcse.fs.controller.MainController;
import lk.uomcse.fs.entity.Neighbour;
import lk.uomcse.fs.entity.Node;
import lk.uomcse.fs.model.QueryService;
import lk.uomcse.fs.utils.FrameUtils;
import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

import javax.swing.*;
import java.awt.*;
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
        FrameUtils.centreWindow(frame);
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
    }

    private void setupThreadComponents() {
        final ResultTableModel resultTableModel = new ResultTableModel(controller.getQueryService());
        tblResults.setModel(resultTableModel);

        final NeighborTableModel neighborTableModel = new NeighborTableModel(controller.getNeighbours());
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
                filenames.add(newFilename);
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
}
