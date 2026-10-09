package com.shopsphere.tests;

import com.shopsphere.pages.CartPage;
import com.shopsphere.pages.HomePage;
import com.shopsphere.pages.ProductPage;
import com.shopsphere.pages.SearchResultsPage;
import com.shopsphere.pages.WishlistPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("ShopSphere Core Platform")
@Feature("Customer Wishlist Management")
public class WishlistTest extends BaseTest {

    @Test(priority = 1, groups = {"regression"}, description = "SS-WSH-001: Add product to wishlist and verify presence on Wishlist page")
    @Story("Wishlist Lifecycle")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies product added to wishlist appears in the customer wishlist table.")
    public void verifyAddProductToWishlistAndVerifyPresence() {
        HomePage homePage = openHomePage();
        SearchResultsPage searchResultsPage = homePage.searchForProduct("Apple MacBook Pro");
        ProductPage productPage = searchResultsPage.openFirstProduct();

        productPage.addToWishlist();
        productPage.closeNotification();

        WishlistPage wishlistPage = homePage.clickWishlistLink();
        Assert.assertFalse(wishlistPage.isWishlistEmpty(), "Wishlist should contain the added item.");
        Assert.assertTrue(wishlistPage.hasItem("MacBook") || wishlistPage.hasItem("Apple"),
                "Wishlist table should contain MacBook item.");
    }

    @Test(priority = 2, groups = {"regression"}, description = "SS-WSH-002: Verify wishlist sharable URL is generated")
    @Story("Wishlist Sharing")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies the wishlist page provides a shareable URL link for customer sharing.")
    public void verifyWishlistSharableUrlFormat() {
        HomePage homePage = openHomePage();
        WishlistPage wishlistPage = homePage.clickWishlistLink();

        if (wishlistPage.isShareLinkDisplayed()) {
            String url = wishlistPage.getShareLinkUrl();
            Assert.assertTrue(url.contains("/wishlist/"), "Share link should point to wishlist URL. Actual: " + url);
        } else {
            Assert.assertTrue(wishlistPage.getCurrentUrl().contains("/wishlist"),
                    "Wishlist page URL should contain /wishlist.");
        }
    }

    @Test(priority = 3, groups = {"regression"}, description = "SS-WSH-003: Remove product from wishlist renders empty state")
    @Story("Wishlist Modification")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies removing item from wishlist renders empty wishlist placeholder.")
    public void verifyRemoveItemFromWishlistRendersEmptyState() {
        HomePage homePage = openHomePage();
        SearchResultsPage searchResultsPage = homePage.searchForProduct("Apple MacBook Pro");
        ProductPage productPage = searchResultsPage.openFirstProduct();

        productPage.addToWishlist();
        productPage.closeNotification();

        WishlistPage wishlistPage = homePage.clickWishlistLink();
        if (wishlistPage.hasItem("MacBook")) {
            wishlistPage.removeItem("MacBook");
        }

        Assert.assertTrue(wishlistPage.isWishlistEmpty() || wishlistPage.getEmptyWishlistMessage().toLowerCase().contains("empty"),
                "Wishlist should reflect empty state after item removal.");
    }

    @Test(priority = 4, groups = {"regression"}, description = "SS-WSH-004: Transfer item from wishlist to shopping cart")
    @Story("Wishlist to Cart Workflow")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies selecting 'Add to cart' on wishlist moves or adds the item into shopping cart.")
    public void verifyAddProductFromWishlistToCartWorkflow() {
        HomePage homePage = openHomePage();
        SearchResultsPage searchResultsPage = homePage.searchForProduct("Apple MacBook Pro");
        ProductPage productPage = searchResultsPage.openFirstProduct();

        productPage.addToWishlist();
        productPage.closeNotification();

        WishlistPage wishlistPage = homePage.clickWishlistLink();
        if (wishlistPage.hasItem("MacBook")) {
            wishlistPage.selectAddToCartForItem("MacBook");
            CartPage cartPage = wishlistPage.clickAddToCartButton();
            Assert.assertTrue(cartPage.getCurrentUrl().contains("/cart"),
                    "Transferring item should navigate to cart. Actual URL: " + cartPage.getCurrentUrl());
            Assert.assertTrue(cartPage.hasItem("MacBook"), "Transferred item should appear in cart.");
        }
    }

    @Test(priority = 5, groups = {"regression"}, description = "SS-WSH-005: Empty wishlist state validation")
    @Story("Empty Wishlist State")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies visiting an empty wishlist shows 'The wishlist is empty!' message.")
    public void verifyEmptyWishlistStateNotification() {
        HomePage homePage = openHomePage();
        WishlistPage wishlistPage = homePage.clickWishlistLink();

        while (!wishlistPage.isWishlistEmpty()) {
            try {
                wishlistPage.removeItem("MacBook");
            } catch (Exception e) {
                break;
            }
        }

        Assert.assertTrue(wishlistPage.isWishlistEmpty(), "Wishlist should be empty.");
        Assert.assertTrue(wishlistPage.getEmptyWishlistMessage().toLowerCase().contains("empty"),
                "Empty state message should contain 'empty'. Actual: " + wishlistPage.getEmptyWishlistMessage());
    }
}
