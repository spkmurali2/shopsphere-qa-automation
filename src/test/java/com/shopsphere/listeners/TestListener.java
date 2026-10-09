package com.shopsphere.listeners;

import com.shopsphere.driver.DriverFactory;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TestNG listener providing automated failure evidence collection (screenshots),
 * structured test lifecycle logging, and execution diagnostics.
 */
public class TestListener implements ITestListener {

    private static final String SCREENSHOT_DIR = "test-output/screenshots";

    @Override
    public void onStart(ITestContext context) {
        System.out.println("==================================================");
        System.out.println("[SUITE START] " + context.getName());
        System.out.println("==================================================");
    }

    @Override
    public void onFinish(ITestContext context) {
        System.out.println("==================================================");
        System.out.println("[SUITE FINISH] " + context.getName() +
                " | Passed: " + context.getPassedTests().size() +
                " | Failed: " + context.getFailedTests().size() +
                " | Skipped: " + context.getSkippedTests().size());
        System.out.println("==================================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println("[TEST STARTED] " + getTestIdentifier(result));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        System.out.println("[TEST PASSED] " + getTestIdentifier(result) +
                " (" + (result.getEndMillis() - result.getStartMillis()) + " ms)");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        System.out.println("[TEST SKIPPED] " + getTestIdentifier(result));
    }

    @Override
    public void onTestFailure(ITestResult result) {
        System.err.println("[TEST FAILED] " + getTestIdentifier(result));
        if (result.getThrowable() != null) {
            System.err.println("Cause: " + result.getThrowable().getMessage());
        }

        captureFailureScreenshot(result);
    }

    private void captureFailureScreenshot(ITestResult result) {
        WebDriver driver = DriverFactory.getDriver();
        if (driver == null) {
            System.err.println("[WARN] Cannot capture screenshot: WebDriver instance is null.");
            return;
        }

        try {
            TakesScreenshot ts = (TakesScreenshot) driver;
            File srcFile = ts.getScreenshotAs(OutputType.FILE);

            Path dirPath = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String sanitizedTestName = result.getMethod().getMethodName().replaceAll("[^a-zA-Z0-9_-]", "_");
            String fileName = String.format("%s_%s.png", sanitizedTestName, timestamp);
            Path targetPath = dirPath.resolve(fileName);

            Files.copy(srcFile.toPath(), targetPath);
            System.out.println("[FAILURE EVIDENCE] Screenshot captured at: " + targetPath.toAbsolutePath());
            attachScreenshotToAllure(sanitizedTestName, driver);
        } catch (IOException e) {
            System.err.println("[ERROR] Failed to save screenshot file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("[WARN] Unexpected error capturing screenshot: " + e.getMessage());
        }
    }

    @io.qameta.allure.Attachment(value = "Failure Screenshot - {0}", type = "image/png")
    private byte[] attachScreenshotToAllure(String testName, WebDriver driver) {
        try {
            return ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private String getTestIdentifier(ITestResult result) {
        return result.getTestClass().getRealClass().getSimpleName() + "." + result.getMethod().getMethodName();
    }
}
