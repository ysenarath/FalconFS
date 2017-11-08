package lk.uomcse.fs.view;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import lk.uomcse.fs.controller.ConfigController;
import lk.uomcse.fs.utils.FrameUtils;
import lk.uomcse.fs.utils.IPUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DefaultFormatter;
import java.awt.*;

public class ConfigView {
    private final JFrame frame = new JFrame("Setup Configuration");

    private JTextField bsAddressText;
    private JButton saveButton;
    private JButton connectButton;
    private JTextField selfNameText;
    private JComboBox selfAddressCombo;
    private JSpinner selfPortSpinner;
    private JSpinner bsPortSpinner;
    private JPanel mainPanel;
    private JComboBox modeCombo;
    private JSpinner bootstrapPortSpinner;
    private ConfigController controller;

    public ConfigView() {
        controller = null;
    }

    public void initialize() {
        initFrame();
        initAddressCombo();
        initModeCombo();
        initListeners();
    }

    /**
     * Initialize form
     */
    private void initFrame() {
        frame.setContentPane(this.mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(new Dimension(450, 350));
        frame.setVisible(true);
        FrameUtils.centreWindow(frame);
    }

    private void initAddressCombo() {
        // Init self address combo boxes
        selfAddressCombo.removeAllItems();
        IPUtils.getPublicIpAddress().forEach(selfAddressCombo::addItem);
        // Init port spinner to default values
        for (JComponent comp : new JComponent[]{selfPortSpinner.getEditor(), bsPortSpinner.getEditor()}) {
            JFormattedTextField field = (JFormattedTextField) comp.getComponent(0);
            DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
            formatter.setCommitsOnValidEdit(true);
        }
    }

    /**
     * Initializes mode
     */
    private void initModeCombo() {
        modeCombo.removeAllItems();
        modeCombo.addItem("UDP");
        modeCombo.addItem("Rest");
        modeCombo.setSelectedIndex(0);
        bootstrapPortSpinner.setEnabled(false);
        bootstrapPortSpinner.setValue(controller.getPort());
    }

    /**
     * Setup binding
     */
    private void initListeners() {
        bsAddressText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                controller.updateBootstrapServerAddress(bsAddressText.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                controller.updateBootstrapServerAddress(bsAddressText.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                controller.updateBootstrapServerAddress(bsAddressText.getText());
            }
        });
        selfNameText.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                controller.updateName(selfNameText.getText());
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                controller.updateName(selfNameText.getText());
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                controller.updateName(selfNameText.getText());
            }
        });
        selfAddressCombo.addActionListener(e -> controller.updateAddress((String) selfAddressCombo.getSelectedItem()));
        selfPortSpinner.addChangeListener(e -> {
            controller.updatePort((Integer) selfPortSpinner.getValue());
            bootstrapPortSpinner.setValue(selfPortSpinner.getValue());
        });
        bsPortSpinner.addChangeListener(e -> controller.updateBootstrapServerPort((Integer) bsPortSpinner.getValue()));
        connectButton.addActionListener(e -> controller.connect());
        saveButton.addActionListener(e -> controller.save());
        modeCombo.addActionListener(e -> {
            String mode = (String) modeCombo.getSelectedItem();
            controller.updateSenderType(mode);
            if (mode != null && mode.toLowerCase().equals("rest")) {
                bootstrapPortSpinner.setEnabled(true);
            } else {
                bootstrapPortSpinner.setEnabled(false);
            }
        });
        bootstrapPortSpinner.addChangeListener(e -> controller.updateBootstrapPort((Integer) bootstrapPortSpinner.getValue()));
    }

    public void setController(ConfigController controller) {
        this.controller = controller;
    }

    public void setName(String name) {
        selfNameText.setText(name);
    }

    public void setAddress(String address) {
        int sel = 0;
        for (int i = 0; i < selfAddressCombo.getItemCount(); i++) {
            if (selfAddressCombo.getItemAt(i).toString().equals(address))
                sel = i;
        }
        selfAddressCombo.setSelectedIndex(sel);
    }

    public void setPort(int port) {
        selfPortSpinner.setValue(port);
    }

    public void setBootstrapPort(int port) {
        bootstrapPortSpinner.setValue(port);
    }

    public void setBootstrapServerPort(int port) {
        bsPortSpinner.setValue(port);
    }

    public void setBootstrapServerAddress(String value) {
        bsAddressText.setText(value);
    }

    public void setVisible(boolean visible) {
        this.frame.setVisible(visible);
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
        mainPanel.setLayout(new GridLayoutManager(3, 4, new Insets(5, 5, 5, 5), -1, -1));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1, new GridConstraints(0, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel1.setBorder(BorderFactory.createTitledBorder("Bootstrap Server"));
        final JLabel label1 = new JLabel();
        label1.setText("Address");
        panel1.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setText("Port");
        panel1.add(label2, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        bsAddressText = new JTextField();
        panel1.add(bsAddressText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        bsPortSpinner = new JSpinner();
        panel1.add(bsPortSpinner, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel2, new GridConstraints(1, 0, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder("Self Node"));
        final JLabel label3 = new JLabel();
        label3.setText("Name");
        panel2.add(label3, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label4 = new JLabel();
        label4.setText("Address");
        panel2.add(label4, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selfNameText = new JTextField();
        panel2.add(selfNameText, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JLabel label5 = new JLabel();
        label5.setText("Port");
        panel2.add(label5, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selfAddressCombo = new JComboBox();
        panel2.add(selfAddressCombo, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        selfPortSpinner = new JSpinner();
        panel2.add(selfPortSpinner, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JLabel label6 = new JLabel();
        label6.setText("Mode");
        panel2.add(label6, new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        modeCombo = new JComboBox();
        panel2.add(modeCombo, new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveButton = new JButton();
        saveButton.setText("Save");
        mainPanel.add(saveButton, new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        connectButton = new JButton();
        connectButton.setText("Connect");
        mainPanel.add(connectButton, new GridConstraints(2, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(0, 3, 2, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
