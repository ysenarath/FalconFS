package lk.uomcse.fs.view;

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
    private ConfigController controller;

    public ConfigView() {
        controller = null;
    }

    public void initialize() {
        initFrame();
        initComponents();
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

    private void initComponents() {
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
        selfPortSpinner.addChangeListener(e -> controller.updatePort((Integer) selfPortSpinner.getValue()));
        bsPortSpinner.addChangeListener(e -> controller.updateBootstrapServerPort((Integer) bsPortSpinner.getValue()));
        connectButton.addActionListener(e -> controller.connect());
        saveButton.addActionListener(e -> controller.save());
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

    public void setBootstrapServerPort(int port) {
        bsPortSpinner.setValue(port);
    }

    public void setBootstrapServerAddress(String value) {
        bsAddressText.setText(value);
    }

    public void close() {
        frame.setVisible(false);
    }
}
