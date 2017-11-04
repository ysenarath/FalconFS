package lk.uomcse.fs.utils;

import javax.swing.*;

public class FrameUtils {
    /**
     * Sets look and feel of this application
     *
     * @param className class name of look and feel
     */
    public static void setLookAndFeel(String className) {
        try {
            if (className.equals("Darcula")) {
                Class<?> clazz = Class.forName("com.bulenkov.darcula.DarculaLaf");
                LookAndFeel laf = (LookAndFeel) clazz.newInstance();
                UIManager.setLookAndFeel(laf);
            } else {
                UIManager.setLookAndFeel(className);
            }
        } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e1) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException | IllegalAccessException e2) {
                // TODO: Error log and Error message
                return;
            }
        }
    }
}
