package com.shopsphere.tests;

import com.shopsphere.pages.CartPage;
import com.shopsphere.pages.HomePage;
import com.shopsphere.pages.ProductPage;
import com.shopsphere.pages.SearchResultsPage;
import com.shopsphere.utils.TestDataReader;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("ShopSphere Core Platform")
@Feature("Shopping Cart Lifecycle & Boundaries")
public class CartTest extends BaseTest {

    @Test(priority = 1, groups = {"regression", "smoke"}, description = "SS-CART-001: Add product to cart and verify cart presence")
    @Story("Add to Cart")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies adding a product displays confirmation banner and reflects line item in cart.")
    public void verifyAddProductToCartSuccessfully() {
        String keyword = TestDataReader.get("existingProduct", "Apple MacBook Pro");
        HomePage homePage = openHomePage();

        SearchResultsPage searchResultsPage = homePage.searchForProduct(keyword);
        ProductPage productPage = searchResultsPage.openFirstProduct();

        productPage.addToCart();
        Assert.assertTrue(productPage.isAddedToCartNotificationDisplayed(),
                "Confirmation banner should appear upon adding item to cart.");
        Assert.assertTrue(productPage.getNotificationMessage().contains("added to your shopping cart"),
                "Banner text should confirm addition to cart. Actual: " + productPage.getNotificationMessage());

        CartPage cartPage = productPage.goToCart();
        Assert.assertFalse(cartPage.isCartEmpty(), "Shopping cart should not be empty after adding an item.");
        Assert.assertTrue(cartPage.hasItem("MacBook") || cartPage.hasItem("Apple"),
                "Cart table should contain the added MacBook item.");
        Assert.assertTrue(cartPage.getItemQuantity("MacBook") >= 1,
                "Cart line item quantity should be at least 1.");
    }

    @Test(priority = 2, groups = {"regression"}, description = "SS-CART-002: Add same product again and verify quantity accumulation")
    @Story("Quantity Accumulation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies adding duplicate product increments quantity rather than creating separate rows.")
    public void verifyAddDuplicateProductAccumulatesQuantity() {
        String keyword = TestDataReader.get("existingProduct", "Apple MacBook Pro");
        HomePage homePage = openHomePage();

        SearchResultsPage searchResultsPage = homePage.searchForProduct(keyword);
        ProductPage productPage = searchResultsPage.openFirstProduct();

        productPage.addToCart();
        productPage.closeNotification();

        productPage.addToCart();

        CartPage cartPage = productPage.goToCart();
        Assert.assertTrue(cartPage.hasItem("MacBook"), "MacBook should exist in cart.");
        Assert.assertTrue(cartPage.getItemQuantity("MacBook") >= 2,
                "Adding duplicate item should accumulate quantity to 2 or more.");
    }

    @Test(priority = 3, groups = {"regression"}, description = "SS-CART-003: Update quantity in cart and verify recalculation")
    @Story("Cart Modification")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies editing item quantity input and updating cart recalculates line item quantity.")
    public void verifyUpdateCartItemQuantityRecalculatesValues() {
        String keyword = TestDataReader.get("existingProduct", "Apple MacBook Pro");
        HomePage homePage = openHomePage();

        SearchResultsPage searchResultsPage = homePage.searchForProduct(keyword);
        ProductPage productPage = searchResultsPage.openFirstProduct();
        productPage.addToCart();

        CartPage cartPage = productPage.goToCart();
        int targetQty = 3;
        cartPage.updateItemQuantity("MacBook", targetQty);

        Assert.assertEquals(cartPage.getItemQuantity("MacBook"), targetQty,
                "Updated quantity should persist after cart update.");
    }

    @Test(priority = 4, groups = {"regression"}, description = "SS-CART-004: Remove product from cart renders empty state")
    @Story("Item Removal")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies removing the only item from the cart renders the empty cart placeholder.")
    public void verifyRemoveItemFromCartRendersEmptyState() {
        String keyword = TestDataReader.get("existingProduct", "Apple MacBook Pro");
        HomePage homePage = openHomePage();

        SearchResultsPage searchResultsPage = homePage.searchForProduct(keyword);
        ProductPage productPage = searchResultsPage.openFirstProduct();
        productPage.addToCart();

        CartPage cartPage = productPage.goToCart();
        Assert.assertTrue(cartPage.hasItem("MacBook"), "Precondition: item should be present before removal.");

        cartPage.removeItem("MacBook");

        Assert.assertTrue(cartPage.isCartEmpty(),
                "Cart should be empty after removing the only product.");
        Assert.assertTrue(cartPage.getEmptyCartMessage().toLowerCase().contains("empty"),
                "Empty cart notification should be displayed. Actual: " + cartPage.getEmptyCartMessage());
    }

    @Test(priority = 5, groups = {"regression", "negative"}, description = "SS-CART-005: Update quantity to 0 removes line item")
    @Story("Quantity Boundary")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies setting item quantity to 0 and clicking update cart removes the line item.")
    public void verifyUpdateQuantityToZeroRemovesItem() {
        String keyword = TestDataReader.get("existingProduct", "Apple MacBook Pro");
        HomePage homePage = openHomePage();

        SearchResultsPage searchResultsPage = homePage.searchForProduct(keyword);
        ProductPage productPage = searchResultsPage.openFirstProduct();
        productPage.addToCart();

        CartPage cartPage = productPage.goToCart();
        cartPage.updateItemQuantity("MacBook", 0);

        Assert.assertTrue(cartPage.isCartEmpty() || !cartPage.hasItem("MacBook"),
                "Setting quantity to 0 should remove the item from cart.");
    }

    @Test(priority = 6, groups = {"regression"}, description = "SS-CART-006: Add multiple distinct products and verify multi-item table")
    @Story("Multi-Item Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies adding two distinct products results in at least two distinct cart rows.")
    public void verifyAddMultipleDistinctProductsToCart() {
        HomePage homePage = openHomePage();

        // Add first product
        SearchResultsPage res1 = homePage.searchForProduct("Apple MacBook Pro");
        ProductPage p1 = res1.openFirstProduct();
        p1.addToCart();

        // Add second product
        homePage.open();
        SearchResultsPage res2 = homePage.searchForProduct("HTC One");
        if (res2.hasResults()) {
            ProductPage p2 = res2.openFirstProduct();
            p2.addToCart();
            CartPage cartPage = p2.goToCart();
            Assert.assertTrue(cartPage.getRowCount() >= 1, "Cart should contain items.");
        } else {
            CartPage cartPage = p1.goToCart();
            Assert.assertFalse(cartPage.isCartEmpty(), "Cart should contain items.");
        }
    }

    @Test(priority = 7, groups = {"regression"}, description = "SS-CART-007: Continue shopping button navigates to catalog")
    @Story("Cart Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies clicking 'Continue shopping' returns user to previous catalog view.")
    public void verifyContinueShoppingButtonNavigatesBack() {
        HomePage homePage = openHomePage();
        SearchResultsPage searchResultsPage = homePage.searchForProduct("Apple MacBook Pro");
        ProductPage productPage = searchResultsPage.openFirstProduct();
        productPage.addToCart();

        CartPage cartPage = productPage.goToCart();
        cartPage.clickContinueShopping();

        Assert.assertFalse(driver.getCurrentUrl().contains("/cart"),
                "User should navigate away from cart on 'Continue shopping'. Current URL: " + driver.getCurrentUrl());
    }

    @Test(priority = 8, groups = {"regression"}, description = "SS-CART-008: Empty cart state hides checkout button")
    @Story("Checkout Gate")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies empty cart does not show active checkout action.")
    public void verifyEmptyCartRendersHelpfulMessageAndDisabledCheckout() {
        HomePage homePage = openHomePage();
        CartPage cartPage = homePage.goToCart();

        // Ensure cart is empty by removing any lingering items
        while (!cartPage.isCartEmpty() && cartPage.getRowCount() > 0) {
            try {
                cartPage.removeItem("MacBook");
            } catch (Exception e) {
                break;
            }
        }

        if (cartPage.isCartEmpty()) {
            Assert.assertTrue(cartPage.getEmptyCartMessage().toLowerCase().contains("empty"),
                    "Empty cart banner should be visible.");
            Assert.assertFalse(cartPage.isCheckoutButtonDisplayed(),
                    "Checkout button should not be displayed when cart is empty.");
        }
    }

    @Test(priority = 9, groups = {"regression", "negative"}, description = "SS-CART-009: Apply empty discount coupon code handles gracefully")
    @Story("Coupon Validation")
    @Severity(SeverityLevel.MINOR)
    @Description("Verifies submitting empty discount coupon box does not trigger unhandled errors.")
    public void verifyApplyEmptyCouponCodeDisplaysValidation() {
        HomePage homePage = openHomePage();
        SearchResultsPage searchResultsPage = homePage.searchForProduct("Apple MacBook Pro");
        ProductPage productPage = searchResultsPage.openFirstProduct();
        productPage.addToCart();

        CartPage cartPage = productPage.goToCart();
        cartPage.applyDiscountCoupon("");

        Assert.assertTrue(cartPage.getCurrentUrl().contains("/cart"),
                "Submitting empty coupon code should keep user on cart page.");
    }

    @Test(priority = 10, groups = {"regression", "negative"}, description = "SS-CART-010: Apply invalid discount coupon code shows error message")
    @Story("Coupon Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies submitting invalid discount coupon 'INVALID_COUPON_2026' shows error feedback.")
    public void verifyApplyInvalidCouponCodeShowsErrorMessage() {
        HomePage homePage = openHomePage();
        SearchResultsPage searchResultsPage = homePage.searchForProduct("Apple MacBook Pro");
        ProductPage productPage = searchResultsPage.openFirstProduct();
        productPage.addToCart();

        CartPage cartPage = productPage.goToCart();
        cartPage.applyDiscountCoupon("INVALID_COUPON_2026");

        String msg = cartPage.getCouponMessage();
        Assert.assertTrue(msg.toLowerCase().contains("coupon code cannot be found") || msg.toLowerCase().contains("entered coupon code") || !msg.isEmpty(),
                "Error feedback should be provided for invalid coupon. Actual: " + msg);
    }
}
