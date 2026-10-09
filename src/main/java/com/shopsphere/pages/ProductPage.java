package com.shopsphere.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * Page object for the individual Product Details page.
 */
public class ProductPage extends BasePage {

    private final By productNameHeading = By.cssSelector(".product-name h1");
    private final By productPrice = By.cssSelector("div.product-price span, [id^='price-value-']");
    private final By quantityInput = By.cssSelector("input[id^='product_enteredQuantity_'], input.qty-input");
    private final By addToCartButton = By.cssSelector("button[id^='add-to-cart-button-']");
    private final By notificationBar = By.cssSelector("#bar-notification");
    private final By notificationContent = By.cssSelector("#bar-notification p.content, #bar-notification .content");
    private final By notificationCartLink = By.cssSelector("#bar-notification a[href*='cart']");
    private final By notificationClose = By.cssSelector("#bar-notification span.close");
    private final By headerCartLink = By.cssSelector("a.ico-cart");

    public ProductPage(WebDriver driver) {
        super(driver);
    }

    public String getProductTitle() {
        return getText(productNameHeading);
    }

    public String getProductPriceText() {
        return getText(productPrice);
    }

    /**
     * Updates the item quantity input before clicking add-to-cart.
     */
    public ProductPage setQuantity(int quantity) {
        WebElement qtyField = waitForElement(quantityInput);
        qtyField.clear();
        qtyField.sendKeys(String.valueOf(quantity));
        return this;
    }

    public int getQuantity() {
        try {
            String value = waitForElement(quantityInput).getAttribute("value");
            return Integer.parseInt(value.trim());
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Clicks "Add to cart" and waits for notification banner.
     */
    public ProductPage addToCart() {
        closeNotification();
        click(addToCartButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#bar-notification.success, .bar-notification.success, #bar-notification .content")));
        return this;
    }

    public boolean isAddedToCartNotificationDisplayed() {
        return isDisplayed(notificationContent);
    }

    public String getNotificationMessage() {
        return getText(notificationContent);
    }

    public void closeNotification() {
        try {
            if (isDisplayed(notificationClose)) {
                click(notificationClose);
                waitForElementToDisappear(notificationBar);
            }
        } catch (Exception ignored) {
        }
    }

    public CartPage goToCart() {
        // Synchronize on header cart counter updating from AJAX before navigation
        try {
            wait.until(d -> {
                String cartText = d.findElement(headerCartLink).getText();
                return cartText.matches(".*\\([1-9][0-9]*\\).*");
            });
        } catch (Exception ignored) {
        }
        closeNotification();
        click(headerCartLink);
        waitForPageReady();
        return new CartPage(driver);
    }

    private final By addToWishlistButton = By.cssSelector("button[id^='add-to-wishlist-button-'], button.add-to-wishlist-button");
    private final By addToCompareButton = By.cssSelector("button.add-to-compare-list-button");
    private final By breadcrumbTrail = By.cssSelector(".breadcrumb");
    private final By productReviewsLink = By.cssSelector("a[href*='productreviews'], .product-review-links a");
    private final By emailFriendButton = By.cssSelector("button.email-a-friend-button");

    public ProductPage addToWishlist() {
        closeNotification();
        click(addToWishlistButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(notificationContent));
        return this;
    }

    public ProductPage addToCompareList() {
        closeNotification();
        click(addToCompareButton);
        wait.until(ExpectedConditions.visibilityOfElementLocated(notificationContent));
        return this;
    }

    public boolean isNotificationDisplayed() {
        return isDisplayed(notificationContent);
    }

    public String getBreadcrumbText() {
        return isDisplayed(breadcrumbTrail) ? getText(breadcrumbTrail) : "";
    }

    public boolean isReviewsLinkDisplayed() {
        return isDisplayed(productReviewsLink);
    }

    public boolean isEmailFriendButtonDisplayed() {
        return isDisplayed(emailFriendButton);
    }
}
