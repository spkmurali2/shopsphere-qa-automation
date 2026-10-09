package com.shopsphere.tests;

import com.shopsphere.pages.CartPage;
import com.shopsphere.pages.HomePage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Epic("ShopSphere Core Platform")
@Feature("Smoke & Critical Journey Health")
public class SmokeTest extends BaseTest {

    @Test(priority = 1, groups = {"smoke", "regression"}, description = "SS-SMK-001: Verify homepage loads with expected branding and title")
    @Story("Store Availability")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies that the nopCommerce store homepage loads successfully within thresholds and displays logo.")
    public void verifyHomePageLoadsSuccessfully() {
        HomePage homePage = openHomePage();
        Assert.assertTrue(homePage.getTitle().toLowerCase().contains("nopcommerce"),
                "Homepage title should contain 'nopCommerce'. Actual: " + homePage.getTitle());
        Assert.assertTrue(homePage.isLogoDisplayed(), "Header brand logo should be visible on homepage.");
    }

    @Test(priority = 2, groups = {"smoke", "regression"}, description = "SS-SMK-002: Verify primary navigation categories are present")
    @Story("Store Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validates that top navigation categories (Computers, Electronics, Apparel, etc.) are rendered.")
    public void verifyPrimaryNavigationCategoriesPresent() {
        HomePage homePage = openHomePage();
        Assert.assertTrue(homePage.isTopMenuDisplayed(), "Top navigation menu bar should be displayed.");

        List<String> categories = homePage.getTopMenuCategoryNames();
        Assert.assertFalse(categories.isEmpty(), "Category list should not be empty.");
        Assert.assertTrue(categories.stream().anyMatch(cat -> cat.equalsIgnoreCase("Computers")),
                "Primary category 'Computers' should be present in top menu.");
    }

    @Test(priority = 3, groups = {"smoke", "regression"}, description = "SS-SMK-003: Verify global search input element availability")
    @Story("Product Discovery")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Ensures the search input box and search trigger button are available for user interaction.")
    public void verifyGlobalSearchElementAvailability() {
        HomePage homePage = openHomePage();
        Assert.assertTrue(homePage.isSearchInputDisplayed(), "Global search input box must be displayed.");
        Assert.assertTrue(homePage.isSearchButtonDisplayed(), "Search submit button must be displayed.");
    }

    @Test(priority = 4, groups = {"smoke", "regression"}, description = "SS-NAV-003: Verify shopping cart header link navigation")
    @Story("Cart Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies clicking header shopping cart link navigates to the shopping cart page.")
    public void verifyCartHeaderNavigation() {
        HomePage homePage = openHomePage();
        CartPage cartPage = homePage.goToCart();
        Assert.assertTrue(cartPage.getCurrentUrl().contains("/cart"),
                "User should be navigated to cart URL. Actual: " + cartPage.getCurrentUrl());
    }

    @Test(priority = 5, groups = {"smoke", "regression"}, description = "SS-SMK-004: Verify wishlist header link availability")
    @Story("Wishlist Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies header wishlist link is visible and initial count is accessible.")
    public void verifyWishlistHeaderLinkAvailability() {
        HomePage homePage = openHomePage();
        Assert.assertTrue(homePage.isWishlistLinkDisplayed(), "Header wishlist link should be visible.");
        Assert.assertTrue(homePage.getWishlistCount() >= 0, "Wishlist counter should return non-negative number.");
    }

    @Test(priority = 6, groups = {"smoke", "regression"}, description = "SS-SMK-005: Verify customer currency dropdown selector availability")
    @Story("Store Localization")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies currency selector is rendered on homepage header.")
    public void verifyCustomerCurrencySelectorAvailability() {
        HomePage homePage = openHomePage();
        Assert.assertTrue(homePage.isCurrencySelectorDisplayed(), "Currency dropdown selector should be displayed.");
    }
}
