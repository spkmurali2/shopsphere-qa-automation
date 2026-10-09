package com.shopsphere.tests;

import com.shopsphere.pages.HomePage;
import com.shopsphere.pages.ProductPage;
import com.shopsphere.pages.SearchResultsPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("ShopSphere Core Platform")
@Feature("Product Details Page (PDP)")
public class ProductDetailsTest extends BaseTest {

    private ProductPage openMacBookProduct() {
        HomePage homePage = openHomePage();
        SearchResultsPage searchResultsPage = homePage.searchForProduct("Apple MacBook Pro");
        return searchResultsPage.openFirstProduct();
    }

    @Test(priority = 1, groups = {"regression", "smoke"}, description = "SS-PDP-001: Verify product title and price are displayed")
    @Story("Product Specifications")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies that the product details page renders the correct product title and price.")
    public void verifyProductTitleAndPriceAreDisplayed() {
        ProductPage productPage = openMacBookProduct();
        Assert.assertTrue(productPage.getProductTitle().toLowerCase().contains("macbook"),
                "Product title should contain 'MacBook'. Actual: " + productPage.getProductTitle());
        Assert.assertFalse(productPage.getProductPriceText().isEmpty(),
                "Product price should not be empty.");
    }

    @Test(priority = 2, groups = {"regression"}, description = "SS-PDP-002: Verify product breadcrumb trail is present")
    @Story("Breadcrumbs Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies that the breadcrumb trail reflects category hierarchy.")
    public void verifyProductBreadcrumbTrailNavigation() {
        ProductPage productPage = openMacBookProduct();
        String breadcrumb = productPage.getBreadcrumbText();
        Assert.assertFalse(breadcrumb.isEmpty(), "Breadcrumb trail should be visible.");
        Assert.assertTrue(breadcrumb.toLowerCase().contains("home"),
                "Breadcrumb should start with 'Home'. Actual: " + breadcrumb);
    }

    @Test(priority = 3, groups = {"regression"}, description = "SS-PDP-003: Verify quantity input default boundary is 1")
    @Story("Quantity Boundary")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies default quantity value on product detail page is 1.")
    public void verifyProductQuantityInputBoundaryDefaultValueIsOne() {
        ProductPage productPage = openMacBookProduct();
        Assert.assertEquals(productPage.getQuantity(), 1,
                "Default product quantity should be 1.");
    }

    @Test(priority = 4, groups = {"regression"}, description = "SS-PDP-004: Add product to wishlist displays confirmation notification")
    @Story("Wishlist Integration")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies clicking 'Add to wishlist' displays confirmation toast notification.")
    public void verifyAddProductToWishlistDisplaysNotification() {
        ProductPage productPage = openMacBookProduct();
        productPage.addToWishlist();

        Assert.assertTrue(productPage.isNotificationDisplayed(),
                "Wishlist confirmation notification banner should be visible.");
        Assert.assertTrue(productPage.getNotificationMessage().toLowerCase().contains("wishlist"),
                "Notification message should mention wishlist. Actual: " + productPage.getNotificationMessage());
    }

    @Test(priority = 5, groups = {"regression"}, description = "SS-PDP-005: Add product to compare list displays confirmation notification")
    @Story("Product Comparison")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies clicking 'Add to compare list' displays confirmation toast notification.")
    public void verifyAddProductToCompareListDisplaysNotification() {
        ProductPage productPage = openMacBookProduct();
        productPage.addToCompareList();

        Assert.assertTrue(productPage.isNotificationDisplayed(),
                "Compare list confirmation notification banner should be visible.");
        Assert.assertTrue(productPage.getNotificationMessage().toLowerCase().contains("compare"),
                "Notification message should mention comparison list. Actual: " + productPage.getNotificationMessage());
    }

    @Test(priority = 6, groups = {"regression"}, description = "SS-PDP-006: Verify product reviews link is available")
    @Story("Product Reviews")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies user can access the product reviews link from product details.")
    public void verifyProductReviewsLinkIsAvailable() {
        ProductPage productPage = openMacBookProduct();
        Assert.assertTrue(productPage.isReviewsLinkDisplayed(),
                "Product reviews link should be displayed on product details page.");
    }

    @Test(priority = 7, groups = {"regression"}, description = "SS-PDP-007: Verify email a friend button is available")
    @Story("Social Sharing")
    @Severity(SeverityLevel.MINOR)
    @Description("Verifies 'Email a friend' referral button is rendered on product page.")
    public void verifyEmailFriendButtonIsAvailable() {
        ProductPage productPage = openMacBookProduct();
        Assert.assertTrue(productPage.isEmailFriendButtonDisplayed(),
                "'Email a friend' button should be displayed on product details page.");
    }
}
