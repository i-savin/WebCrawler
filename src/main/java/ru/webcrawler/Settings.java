package ru.webcrawler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author ilasavin
 * @since 31.08.15
 */
public class Settings {
    private final static Logger logger = LoggerFactory.getLogger(Settings.class);

    private final static String CONFIG_FILE = "config.properties";
    private Properties properties;

    private Settings() {
        loadPropertiesFile(CONFIG_FILE);
    }

    public void loadPropertiesFile(String fileName) {
        this.properties = new Properties();
        try {
            logger.info("Using {} to config webcrawler", fileName);
            properties.load(getClass().getClassLoader().getResourceAsStream(fileName));
        } catch (Exception e) {
            logger.error("Could not load properties file: ", e);
            System.exit(-1);
        }
    }

    public static Settings getSettingsInstance() {
        return SettingsInstance.settings;
    }

    public String get(String key) {
        return this.properties.getProperty((key));
    }

    private static class SettingsInstance {
        private static Settings settings = new Settings();
    }
}


