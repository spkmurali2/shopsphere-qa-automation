package com.shopsphere.driver;

import com.shopsphere.config.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;
import java.util.Map;

/**
 * Thread-safe WebDriver factory managing browser initialization, configuration,
 * and clean teardown via ThreadLocal storage.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    private DriverFactory() {
        // Prevent instantiation
    }

    /**
     * Initializes the WebDriver instance based on configuration and system properties.
     */
    public static WebDriver initDriver() {
        String browser = ConfigReader.getBrowser();
        boolean headless = ConfigReader.isHeadless();

        WebDriver driver;

        switch (browser) {
            case "firefox":
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                if (headless) {
                    firefoxOptions.addArguments("-headless");
                }
                driver = new FirefoxDriver(firefoxOptions);
                break;

            case "edge":
                EdgeOptions edgeOptions = new EdgeOptions();
                if (headless) {
                    edgeOptions.addArguments("--headless=new");
                }
                driver = new EdgeDriver(edgeOptions);
                break;

            case "chrome":
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                if (headless) {
                    chromeOptions.addArguments("--headless=new");
                    chromeOptions.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36");
                }
                // Sensible options for stability and realistic browsing behavior
                chromeOptions.addArguments("--window-size=1920,1080");
                chromeOptions.addArguments("--disable-gpu");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                chromeOptions.setExperimentalOption("useAutomationExtension", false);
                driver = new ChromeDriver(chromeOptions);
                break;
        }

        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(ConfigReader.getPageLoadTimeout()));
        try {
            if (!headless) {
                driver.manage().window().maximize();
            }
        } catch (Exception ignored) {
            // Window size is already defined via --window-size=1920,1080 for headless/xvfb
        }

        if (driver instanceof ChromiumDriver) {
            try {
                ((ChromiumDriver) driver).executeCdpCommand(
                        "Page.addScriptToEvaluateOnNewDocument",
                        Map.of("source", "Object.defineProperty(navigator, 'webdriver', {get: () => undefined});")
                );
            } catch (Exception ignored) {
            }
        }

        driverThreadLocal.set(driver);
        return driver;
    }

    /**
     * Retrieves the current thread's WebDriver instance.
     */
    public static WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    /**
     * Quits the WebDriver session and cleans up the ThreadLocal reference.
     */
    public static void quitDriver() {
        WebDriver driver = driverThreadLocal.get();
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                System.err.println("[WARN] Exception while quitting WebDriver: " + e.getMessage());
            } finally {
                driverThreadLocal.remove();
            }
        }
    }
}
