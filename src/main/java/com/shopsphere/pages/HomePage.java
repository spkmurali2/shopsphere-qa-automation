package com.shopsphere.pages;

import com.shopsphere.config.ConfigReader;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page object representing the ShopSphere homepage and global navigation header.
 */
public class HomePage extends BasePage {

    // Header & Brand Locators
    private final By headerLogo = By.cssSelector(".header-logo a");
    private final By searchInput = By.id("small-searchterms");
    private final By searchButton = By.cssSelector("button.search-box-button");
    private final By loginLink = By.cssSelector("a.ico-login");
    private final By registerLink = By.cssSelector("a.ico-register");
    private final By cartLink = By.cssSelector("a.ico-cart");
    private final By cartQtyBadge = By.cssSelector("span.cart-qty");

    // Navigation & Notifications
    private final By topMenuBar = By.cssSelector(".top-menu, .header-menu");
    private final By topMenuCategories = By.cssSelector(".top-menu > li > a, .header-menu > ul > li > a, .header-menu a");
    private final By notificationBar = By.cssSelector("#bar-notification");
    private final By notificationContent = By.cssSelector("#bar-notification p.content, #bar-notification .content");
    private final By notificationClose = By.cssSelector("#bar-notification span.close");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigates to the configured application base URL and synchronizes on page availability.
     */
    public HomePage open() {
        driver.get(ConfigReader.getBaseUrl());
        waitForPageReady();
        wait.until(ExpectedConditions.or(
                ExpectedConditions.titleContains("nopCommerce"),
                ExpectedConditions.visibilityOfElementLocated(headerLogo)
        ));
        return this;
    }

    public boolean isLogoDisplayed() {
        return isDisplayed(headerLogo);
    }

    public boolean isTopMenuDisplayed() {
        return isDisplayed(topMenuBar);
    }

    public boolean isSearchInputDisplayed() {
        return isDisplayed(searchInput);
    }

    public boolean isCartLinkDisplayed() {
        return isDisplayed(cartLink);
    }

    /**
     * Performs a global header search and transitions to the SearchResultsPage.
     */
    public SearchResultsPage searchForProduct(String keyword) {
        type(searchInput, keyword);
        click(searchButton);
        waitForPageReady();
        return new SearchResultsPage(driver);
    }

    /**
     * Clicks search button without entering a keyword and captures validation alert text.
     */
    public String submitEmptySearchAndGetAlertText() {
        type(searchInput, "");
        click(searchButton);
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            String alertText = alert.getText();
            alert.accept();
            return alertText;
        } catch (Exception e) {
            return "";
        }
    }

    public LoginPage goToLogin() {
        click(loginLink);
        waitForPageReady();
        return new LoginPage(driver);
    }

    public void goToRegister() {
        click(registerLink);
        waitForPageReady();
    }

    public CartPage goToCart() {
        // If notification banner is active, wait briefly or close it to avoid click interception
        if (isDisplayed(notificationBar)) {
            closeNotificationBar();
        }
        click(cartLink);
        waitForPageReady();
        return new CartPage(driver);
    }

    public List<String> getTopMenuCategoryNames() {
        List<WebElement> items = driver.findElements(topMenuCategories);
        List<String> names = new ArrayList<>();
        for (WebElement item : items) {
            String text = item.getText().trim();
            if (!text.isEmpty()) {
                names.add(text);
            }
        }
        return names;
    }

    public void clickCategory(String categoryName) {
        By categoryLocator = By.xpath("//a[contains(normalize-space(), '" + categoryName + "')]");
        click(categoryLocator);
        waitForPageReady();
    }

    public int getCartItemCount() {
        try {
            String qtyText = getText(cartQtyBadge);
            // Parses format like "(2)" -> 2
            String cleaned = qtyText.replaceAll("[^0-9]", "");
            return cleaned.isEmpty() ? 0 : Integer.parseInt(cleaned);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isNotificationDisplayed() {
        return isDisplayed(notificationContent);
    }

    public String getNotificationMessage() {
        return getText(notificationContent);
    }

    public void closeNotificationBar() {
        try {
            if (isDisplayed(notificationClose)) {
                click(notificationClose);
                waitForElementToDisappear(notificationBar);
            }
        } catch (Exception ignored) {
        }
    }
}
