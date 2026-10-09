package com.shopsphere.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page object representing the Shopping Cart page (/cart).
 */
public class CartPage extends BasePage {

    private final By cartTable = By.cssSelector("table.cart");
    private final By cartRows = By.cssSelector("table.cart tbody tr");
    private final By emptyCartMessage = By.cssSelector(".no-data, .order-summary-content .no-data");
    private final By updateCartButton = By.cssSelector("button#updatecart.update-cart-button, button.update-cart-button");
    private final By orderSubtotal = By.cssSelector("tr.order-subtotal td.cart-total-right span.value-summary, .order-subtotal .value-summary");
    private final By termsOfService = By.id("termsofservice");
    private final By checkoutButton = By.id("checkout");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public boolean isCartEmpty() {
        return isDisplayed(emptyCartMessage);
    }

    public String getEmptyCartMessage() {
        return getText(emptyCartMessage);
    }

    public int getCartItemRowCount() {
        try {
            return driver.findElements(cartRows).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<String> getItemNames() {
        List<WebElement> rows = driver.findElements(cartRows);
        List<String> names = new ArrayList<>();
        for (WebElement row : rows) {
            try {
                WebElement nameEl = row.findElement(By.cssSelector(".product-name a, .product a"));
                names.add(nameEl.getText().trim());
            } catch (Exception ignored) {
            }
        }
        return names;
    }

    public boolean hasItem(String productName) {
        String lowerTarget = productName.toLowerCase();
        for (String name : getItemNames()) {
            if (name.toLowerCase().contains(lowerTarget)) {
                return true;
            }
        }
        return false;
    }

    private WebElement findRowForProduct(String productName) {
        List<WebElement> rows = driver.findElements(cartRows);
        String lowerTarget = productName.toLowerCase();
        for (WebElement row : rows) {
            try {
                WebElement nameEl = row.findElement(By.cssSelector(".product-name a, .product a"));
                if (nameEl.getText().trim().toLowerCase().contains(lowerTarget)) {
                    return row;
                }
            } catch (Exception ignored) {
            }
        }
        throw new RuntimeException("Could not find cart row for product: " + productName);
    }

    public int getItemQuantity(String productName) {
        WebElement row = findRowForProduct(productName);
        WebElement qtyInput = row.findElement(By.cssSelector("input.qty-input"));
        return Integer.parseInt(qtyInput.getAttribute("value").trim());
    }

    public CartPage updateItemQuantity(String productName, int newQuantity) {
        WebElement row = findRowForProduct(productName);
        WebElement qtyInput = row.findElement(By.cssSelector("input.qty-input"));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].value = arguments[1]; arguments[0].setAttribute('value', arguments[1]); arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
                qtyInput, String.valueOf(newQuantity));
        click(updateCartButton);
        waitForPageReady();
        return this;
    }

    public CartPage removeItem(String productName) {
        WebElement row = findRowForProduct(productName);
        WebElement removeBtn = row.findElement(By.cssSelector("button.remove-btn, .remove-btn"));
        removeBtn.click();
        waitForPageReady();
        return this;
    }

    public String getItemSubtotal(String productName) {
        WebElement row = findRowForProduct(productName);
        WebElement subtotalEl = row.findElement(By.cssSelector(".product-subtotal, .subtotal"));
        return subtotalEl.getText().trim();
    }

    public String getOrderSubtotal() {
        try {
            return getText(orderSubtotal);
        } catch (Exception e) {
            return "";
        }
    }

    private final By discountCodeInput = By.cssSelector("#discountcouponcode");
    private final By applyDiscountButton = By.cssSelector("#applydiscountcouponcode");
    private final By couponMessage = By.cssSelector(".message-failure, .coupon-box .message, .message-error");
    private final By continueShoppingButton = By.cssSelector("button[name='continueshopping'], button.continue-shopping-button");

    public int getRowCount() {
        return driver.findElements(cartRows).size();
    }

    public CartPage applyDiscountCoupon(String code) {
        type(discountCodeInput, code);
        click(applyDiscountButton);
        waitForPageReady();
        return this;
    }

    public String getCouponMessage() {
        return isDisplayed(couponMessage) ? getText(couponMessage) : "";
    }

    public boolean isCheckoutButtonDisplayed() {
        return isDisplayed(checkoutButton);
    }

    public void clickContinueShopping() {
        click(continueShoppingButton);
        waitForPageReady();
    }
}
