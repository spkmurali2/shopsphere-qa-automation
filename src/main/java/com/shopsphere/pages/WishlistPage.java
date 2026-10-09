package com.shopsphere.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page object representing the customer Wishlist page (/wishlist).
 */
public class WishlistPage extends BasePage {

    private final By wishlistTable = By.cssSelector("table.cart");
    private final By wishlistItems = By.cssSelector("table.cart tbody tr");
    private final By emptyWishlistMessage = By.cssSelector(".no-data, .wishlist-content .no-data");
    private final By removeButtons = By.cssSelector("button.remove-btn, input[name='removefromcart']");
    private final By addToCartCheckboxes = By.cssSelector("input[name='addtocart']");
    private final By addToCartButton = By.cssSelector("button[name='addtocartbutton'], button.wishlist-add-to-cart-button");
    private final By shareLink = By.cssSelector("a.share-link, .share-info a");

    public WishlistPage(WebDriver driver) {
        super(driver);
    }

    public boolean isWishlistEmpty() {
        return isDisplayed(emptyWishlistMessage);
    }

    public String getEmptyWishlistMessage() {
        return getText(emptyWishlistMessage);
    }

    public boolean hasItem(String productName) {
        if (!isDisplayed(wishlistTable)) {
            return false;
        }
        List<WebElement> rows = getElements(wishlistItems);
        for (WebElement row : rows) {
            if (row.getText().toLowerCase().contains(productName.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public WishlistPage removeItem(String productName) {
        List<WebElement> rows = getElements(wishlistItems);
        for (WebElement row : rows) {
            if (row.getText().toLowerCase().contains(productName.toLowerCase())) {
                WebElement removeBtn = row.findElement(By.cssSelector("button.remove-btn, input[name='removefromcart']"));
                removeBtn.click();
                waitForPageReady();
                break;
            }
        }
        return this;
    }

    public WishlistPage selectAddToCartForItem(String productName) {
        List<WebElement> rows = getElements(wishlistItems);
        for (WebElement row : rows) {
            if (row.getText().toLowerCase().contains(productName.toLowerCase())) {
                WebElement chk = row.findElement(By.cssSelector("input[name='addtocart']"));
                if (!chk.isSelected()) {
                    chk.click();
                }
                break;
            }
        }
        return this;
    }

    public CartPage clickAddToCartButton() {
        click(addToCartButton);
        waitForPageReady();
        return new CartPage(driver);
    }

    public boolean isShareLinkDisplayed() {
        return isDisplayed(shareLink);
    }

    public String getShareLinkUrl() {
        return driver.findElement(shareLink).getAttribute("href");
    }
}
