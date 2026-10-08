package com.shopsphere.tests;

import com.shopsphere.pages.CartPage;
import com.shopsphere.pages.HomePage;
import com.shopsphere.pages.ProductPage;
import com.shopsphere.pages.SearchResultsPage;
import com.shopsphere.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Functional test suite validating Shopping Cart lifecycle:
 * addition, duplicate accumulation, quantity updates, and removal.
 */
public class CartTest extends BaseTest {

    @Test(priority = 1, groups = {"regression", "smoke"}, description = "SS-CART-001 & SS-CART-002: Add product to cart and verify cart presence")
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

    @Test(priority = 2, groups = {"regression"}, description = "SS-CART-003: Add same product again and verify quantity accumulation")
    public void verifyAddDuplicateProductAccumulatesQuantity() {
        String keyword = TestDataReader.get("existingProduct", "Apple MacBook Pro");
        HomePage homePage = openHomePage();

        SearchResultsPage searchResultsPage = homePage.searchForProduct(keyword);
        ProductPage productPage = searchResultsPage.openFirstProduct();

        // Add first time
        productPage.addToCart();
        productPage.closeNotification();

        // Add second time
        productPage.addToCart();

        CartPage cartPage = productPage.goToCart();
        Assert.assertTrue(cartPage.hasItem("MacBook"), "MacBook should exist in cart.");
        Assert.assertTrue(cartPage.getItemQuantity("MacBook") >= 2,
                "Adding duplicate item should accumulate quantity to 2 or more. Actual: " + cartPage.getItemQuantity("MacBook"));
    }

    @Test(priority = 3, groups = {"regression"}, description = "SS-CART-004: Update quantity in cart and verify recalculation")
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

    @Test(priority = 4, groups = {"regression"}, description = "SS-CART-005: Remove product from cart renders empty state")
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
}
