package lk.uomcse.fs;

import lk.uomcse.fs.controller.ConfigController;
import lk.uomcse.fs.entity.BootstrapServer;
import lk.uomcse.fs.model.Configurations;
import lk.uomcse.fs.utils.FrameUtils;
import lk.uomcse.fs.view.ConfigView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class Main {
    /**
     * Main Method
     *
     * @param args No args yet
     */
    public static void main(String[] args) throws FileNotFoundException {
        FrameUtils.setLookAndFeel("Darcula");
        Properties props = new Properties();
        String configPath = null;
        InputStream inputStream = FalconFS.class.getClassLoader().getResourceAsStream("config.properties");
        if (inputStream == null)
            try {
                configPath = "./config.properties";
                if (args.length >= 1) {
                    configPath = args[0];
                    System.out.println(String.format("Taking '%s' as path to configuration.", configPath));
                    // TODO: Show ok/default message box with above message
                }
                inputStream = new FileInputStream(configPath);
            } catch (FileNotFoundException ex) {
                inputStream = null;
            }
        if (inputStream != null) {
            try {
                props.load(inputStream);
            } catch (IOException e) {
                System.err.println("Property file 'config.properties' could not be loaded");
                // TODO: Show error message box with above message
                return;
            }
        } else {
            System.err.println("Please provide path to configurations after the name of application.");
            // TODO: Show error message box with above message
            return;
        }
        BootstrapServer bc = new BootstrapServer(props.getProperty("bs.ip"), Integer.parseInt(props.getProperty("bs.port")));
        String filesStr = props.getProperty("files");
        List<String> files = Arrays.asList(filesStr.trim().toLowerCase().split(","));
        Configurations configurations = new Configurations(props.getProperty("fs.name"), props.getProperty("fs.ip"), Integer.parseInt(props.getProperty("fs.port")), bc, configPath, files);
        ConfigView configView = new ConfigView();
        ConfigController configController = new ConfigController(configurations, configView);
        configController.updateView();
    }
}
