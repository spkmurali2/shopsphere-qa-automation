package com.shopsphere.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads framework configuration values with prioritized lookup:
 * 1. JVM System Properties (e.g., -Dbrowser=chrome -Dheadless=true)
 * 2. Configuration file (config/config.properties)
 * 3. Safe fallback defaults
 */
public final class ConfigReader {

    private static final Properties properties = new Properties();
    private static final String CONFIG_FILE = "config/config.properties";

    static {
        loadProperties();
    }

    private ConfigReader() {
        // Prevent instantiation
    }

    private static void loadProperties() {
        try (InputStream inputStream = ConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (inputStream != null) {
                properties.load(inputStream);
            } else {
                System.out.println("[WARN] " + CONFIG_FILE + " not found on classpath. Falling back to built-in defaults.");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load configuration file: " + e.getMessage());
        }
    }

    public static String getProperty(String key, String defaultValue) {
        String systemVal = System.getProperty(key);
        if (systemVal != null && !systemVal.trim().isEmpty()) {
            return systemVal.trim();
        }
        String fileVal = properties.getProperty(key);
        if (fileVal != null && !fileVal.trim().isEmpty()) {
            return fileVal.trim();
        }
        return defaultValue;
    }

    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static String getBrowser() {
        return getProperty("browser", "chrome").toLowerCase();
    }

    public static boolean isHeadless() {
        String headless = getProperty("headless", "false");
        return Boolean.parseBoolean(headless);
    }

    public static String getBaseUrl() {
        return getProperty("baseUrl", "https://demo.nopcommerce.com/");
    }

    public static int getExplicitWaitTimeout() {
        String wait = getProperty("explicitWait", "15");
        try {
            return Integer.parseInt(wait);
        } catch (NumberFormatException e) {
            return 15;
        }
    }

    public static int getPageLoadTimeout() {
        String timeout = getProperty("pageLoadTimeout", "30");
        try {
            return Integer.parseInt(timeout);
        } catch (NumberFormatException e) {
            return 30;
        }
    }
}
