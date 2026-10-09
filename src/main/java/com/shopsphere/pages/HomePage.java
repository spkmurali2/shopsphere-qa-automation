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
    private final By logoutLink = By.cssSelector("a.ico-logout");
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

    public void clickLogin() {
        goToLogin();
    }

    public void goToRegister() {
        click(registerLink);
        waitForPageReady();
    }

    public void clickRegister() {
        goToRegister();
    }

    public boolean isLoggedIn() {
        return isDisplayed(logoutLink);
    }

    public HomePage logOutIfLoggedIn() {
        if (isLoggedIn()) {
            click(logoutLink);
            waitForPageReady();
        }
        return this;
    }

    public boolean isSearchButtonDisplayed() {
        return isDisplayed(searchButton);
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

    private final By currencyDropdown = By.id("customerCurrency");
    private final By wishlistLink = By.cssSelector("a.ico-wishlist");
    private final By wishlistQtyBadge = By.cssSelector("span.wishlist-qty");
    private final By sitemapLink = By.cssSelector("a[href*='sitemap']");
    private final By shippingReturnsLink = By.cssSelector("a[href*='shipping-returns']");
    private final By privacyNoticeLink = By.cssSelector("a[href*='privacy-notice']");
    private final By conditionsOfUseLink = By.cssSelector("a[href*='conditions-of-use']");
    private final By contactUsLink = By.cssSelector("a[href*='contactus']");
    private final By featuredProductPrice = By.cssSelector(".product-item .actual-price");

    public boolean isWishlistLinkDisplayed() {
        return isDisplayed(wishlistLink);
    }

    public WishlistPage clickWishlistLink() {
        click(wishlistLink);
        waitForPageReady();
        return new WishlistPage(driver);
    }

    public int getWishlistCount() {
        try {
            String text = getText(wishlistQtyBadge).replaceAll("[^0-9]", "");
            return text.isEmpty() ? 0 : Integer.parseInt(text);
        } catch (Exception e) {
            return 0;
        }
    }

    public boolean isCurrencySelectorDisplayed() {
        return isDisplayed(currencyDropdown);
    }

    public HomePage selectCurrency(String currencyVisibleText) {
        org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(driver.findElement(currencyDropdown));
        select.selectByVisibleText(currencyVisibleText);
        waitForPageReady();
        return this;
    }

    public String getFirstFeaturedProductPriceText() {
        return getText(featuredProductPrice);
    }

    public void clickSitemap() {
        click(sitemapLink);
        waitForPageReady();
    }

    public void clickShippingAndReturns() {
        click(shippingReturnsLink);
        waitForPageReady();
    }

    public void clickPrivacyNotice() {
        click(privacyNoticeLink);
        waitForPageReady();
    }

    public void clickConditionsOfUse() {
        click(conditionsOfUseLink);
        waitForPageReady();
    }

    public ContactUsPage clickContactUs() {
        click(contactUsLink);
        waitForPageReady();
        return new ContactUsPage(driver);
    }
}
