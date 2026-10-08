package com.shopsphere.tests;

import com.shopsphere.pages.CartPage;
import com.shopsphere.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Smoke test suite verifying critical platform entry points and health.
 * Serves as the primary pre-merge quality gate.
 */
public class SmokeTest extends BaseTest {

    @Test(groups = {"smoke"}, description = "SS-SMK-001: Verify homepage loads with expected branding and title")
    public void verifyHomePageLoadsSuccessfully() {
        HomePage homePage = openHomePage();

        Assert.assertTrue(homePage.getTitle().toLowerCase().contains("nopcommerce"),
                "Page title did not contain expected 'nopcommerce' text. Actual: " + homePage.getTitle());
        Assert.assertTrue(homePage.isLogoDisplayed(),
                "Store logo should be visible on the homepage header.");
    }

    @Test(groups = {"smoke"}, description = "SS-SMK-002: Verify major category navigation is available")
    public void verifyPrimaryNavigationCategoriesPresent() {
        HomePage homePage = openHomePage();

        Assert.assertTrue(homePage.isTopMenuDisplayed(),
                "Top navigation menu bar should be displayed.");

        List<String> categories = homePage.getTopMenuCategoryNames();
        Assert.assertFalse(categories.isEmpty(), "Category menu should contain items.");
        Assert.assertTrue(categories.stream().anyMatch(c -> c.equalsIgnoreCase("Computers")),
                "Expected 'Computers' category in header menu. Available: " + categories);
    }

    @Test(groups = {"smoke"}, description = "SS-SMK-003: Verify global search input is interactable")
    public void verifyGlobalSearchElementAvailability() {
        HomePage homePage = openHomePage();

        Assert.assertTrue(homePage.isSearchInputDisplayed(),
                "Search input should be visible and ready on the header.");
    }

    @Test(groups = {"smoke"}, description = "SS-SMK-004: Verify cart navigation from header")
    public void verifyCartHeaderNavigation() {
        HomePage homePage = openHomePage();

        Assert.assertTrue(homePage.isCartLinkDisplayed(),
                "Cart link should be visible in header.");

        CartPage cartPage = homePage.goToCart();
        Assert.assertTrue(cartPage.getCurrentUrl().contains("/cart"),
                "Navigating via header cart link should lead to /cart URL. Actual: " + cartPage.getCurrentUrl());
    }
}
