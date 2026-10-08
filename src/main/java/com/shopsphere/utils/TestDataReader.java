package com.shopsphere.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility to load externalized test data properties for functional and negative tests.
 */
public final class TestDataReader {

    private static final Properties testData = new Properties();
    private static final String TEST_DATA_FILE = "config/testdata.properties";

    static {
        loadTestData();
    }

    private TestDataReader() {
        // Prevent instantiation
    }

    private static void loadTestData() {
        try (InputStream stream = TestDataReader.class.getClassLoader().getResourceAsStream(TEST_DATA_FILE)) {
            if (stream != null) {
                testData.load(stream);
            } else {
                System.out.println("[WARN] Test data file " + TEST_DATA_FILE + " not found on classpath.");
            }
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to load test data: " + e.getMessage());
        }
    }

    public static String get(String key) {
        return testData.getProperty(key);
    }

    public static String get(String key, String defaultValue) {
        return testData.getProperty(key, defaultValue);
    }
}
