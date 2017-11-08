package lk.uomcse.fs.view;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.net.InetAddress;

/**
 * @author Dulanjaya
 * @since 11/8/2017
 */
public class ProgressCellRenderer extends DefaultTableCellRenderer {
    private final JProgressBar bar = new JProgressBar(0,100);

    public ProgressCellRenderer() {
        super();
        setOpaque(true);
        bar.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
//        bar.setStringPainted(true);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        Integer i = 0;
        try {
            i = (Integer) value;
        } catch (ClassCastException e) {
            // ignore
        }
        String text = "Completed";
        if (i < 0) {
            text = "Error";
        }
        else if (i <= 10) {
            bar.setValue(i * 10);
            bar.setString(Integer.toString(i));
            switch (i) {
                case 10:
                    bar.setForeground(Color.GREEN);
                    return bar;
                case 9:
                    bar.setForeground(Color.GREEN);
                    return bar;
                case 8:
                    bar.setForeground(Color.GREEN);
                    return bar;
                case 7:
                    bar.setForeground(Color.GREEN);
                    return bar;
                case 6:
                    bar.setForeground(Color.YELLOW);
                    return bar;
                case 5:
                    bar.setForeground(Color.YELLOW);
                    return bar;
                case 4:
                    bar.setForeground(Color.YELLOW);
                    return bar;
                case 3:
                    bar.setForeground(Color.YELLOW);
                    return bar;
                case 2:
                    bar.setForeground(Color.RED);
                    return bar;
                case 1:
                    bar.setForeground(Color.RED);
                    return bar;
                case 0:
                    bar.setForeground(Color.RED);
                    return bar;
            }


        }
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        return this;
    }
}
