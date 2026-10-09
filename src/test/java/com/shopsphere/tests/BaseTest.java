package com.shopsphere.tests;

import com.shopsphere.driver.DriverFactory;
import com.shopsphere.listeners.TestListener;
import com.shopsphere.pages.HomePage;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

/**
 * Base test class managing WebDriver lifecycle and test setup.
 */
@Listeners(TestListener.class)
public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeSuite(alwaysRun = true)
    public void suiteSetUp() {
        boolean mockEnabled = Boolean.parseBoolean(System.getProperty("mockServer", "false"));
        if (mockEnabled) {
            int port = com.shopsphere.server.EmbeddedTestServer.startDynamic();
            System.setProperty("baseUrl", "http://127.0.0.1:" + port + "/");
        }
        DriverFactory.initDriver();
    }

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        driver = DriverFactory.getDriver();
        if (driver == null) {
            driver = DriverFactory.initDriver();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void tearDown() {
        DriverFactory.quitDriver();
        com.shopsphere.server.EmbeddedTestServer.stop();
    }

    protected WebDriver getDriver() {
        return driver;
    }

    protected HomePage openHomePage() {
        HomePage homePage = new HomePage(driver);
        return homePage.open();
    }
}
