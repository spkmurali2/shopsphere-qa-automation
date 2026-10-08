package com.shopsphere.pages;

import com.shopsphere.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * BasePage encapsulates core WebDriver interactions, explicit wait wrappers,
 * and DOM synchronization. Domain-specific pages extend this base class.
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WebDriverWait wait;
    protected final int timeoutSeconds;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.timeoutSeconds = ConfigReader.getExplicitWaitTimeout();
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(this.timeoutSeconds));
    }

    public BasePage(WebDriver driver, int customTimeoutSeconds) {
        this.driver = driver;
        this.timeoutSeconds = customTimeoutSeconds;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(customTimeoutSeconds));
    }

    /**
     * Waits for element visibility and clicks it. Falls back to JavaScript click if intercepted.
     */
    protected void click(By locator) {
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            element.click();
        } catch (Exception e) {
            // Fallback for elements occasionally obscured by dynamic toasts/floating headers
            WebElement element = driver.findElement(locator);
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    /**
     * Clears input field and types text after ensuring visibility.
     */
    protected void type(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        if (text != null && !text.isEmpty()) {
            element.sendKeys(text);
        }
    }

    /**
     * Retrieves visible text from an element after explicit wait.
     */
    protected String getText(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim();
    }

    /**
     * Checks if element is currently displayed without throwing TimeoutException.
     */
    protected boolean isDisplayed(By locator) {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Waits for element to be visible in DOM and view.
     */
    protected WebElement waitForElement(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits for element to disappear / become invisible.
     */
    protected boolean waitForElementToDisappear(By locator) {
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Handles transient Cloudflare/Turnstile security verification interstitials if present.
     */
    public void handleSecurityChallenge() {
        long startTime = System.currentTimeMillis();
        boolean clicked = false;
        while (System.currentTimeMillis() - startTime < 45000) {
            String title = driver.getTitle();
            if (title != null && (title.contains("nopCommerce") || (!title.contains("Just a moment") && !title.isEmpty()))) {
                break;
            }
            try {
                if (!clicked) {
                    List<WebElement> iframes = driver.findElements(By.tagName("iframe"));
                    for (WebElement iframe : iframes) {
                        try {
                            if (iframe.isDisplayed()) {
                                new Actions(driver).moveToElement(iframe, 28, 28).click().perform();
                                clicked = true;
                                break;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                }
                Thread.sleep(1500);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Waits for document.readyState to be complete and handles any transient security interstitials.
     */
    public void waitForPageReady() {
        handleSecurityChallenge();
        wait.until(webDriver -> ((JavascriptExecutor) webDriver)
                .executeScript("return document.readyState").equals("complete"));
    }

    /**
     * Returns list of matching elements visible on page.
     */
    protected List<WebElement> getElements(By locator) {
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(locator));
        return driver.findElements(locator);
    }

    /**
     * Scrolls the viewport until the target element is centered into view.
     */
    protected void scrollToElement(By locator) {
        WebElement element = driver.findElement(locator);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public String getTitle() {
        return driver.getTitle();
    }
}
