package lk.uomcse.fs;

import lk.uomcse.fs.controller.ConfigController;
import lk.uomcse.fs.entity.BootstrapServer;
import lk.uomcse.fs.model.Configuration;
import lk.uomcse.fs.model.FalconFS;
import lk.uomcse.fs.utils.FrameUtils;
import lk.uomcse.fs.view.ConfigView;

import java.io.*;
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
                try {
                    if (configPath != null)
                        new FileOutputStream(configPath, false).close();
                } catch (IOException ignore) {
                    // Ignore configs
                }
            }
        }
        BootstrapServer bc = new BootstrapServer(props.getProperty("bs.ip"), Integer.parseInt(props.getProperty("bs.port")));
        String filesStr = props.getProperty("files");
        List<String> files = Arrays.asList(filesStr.trim().toLowerCase().split(","));
        // Handing application control to UI
        Configuration configuration = new Configuration(props.getProperty("fs.name"), props.getProperty("fs.ip"), Integer.parseInt(props.getProperty("fs.port")), bc, configPath, files);
        ConfigView configView = new ConfigView();
        ConfigController configController = new ConfigController(configuration, configView);
        configController.updateView();
    }
}
