package com.shopsphere.tests;

import com.shopsphere.pages.HomePage;
import com.shopsphere.pages.LoginPage;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Functional test suite validating major application navigation links and portal routes.
 */
public class NavigationTest extends BaseTest {

    @Test(groups = {"regression"}, description = "SS-NAV-001: Verify header login link navigates to Login page")
    public void verifyLoginNavigationLoadsLoginPage() {
        HomePage homePage = openHomePage();

        LoginPage loginPage = homePage.goToLogin();

        Assert.assertTrue(loginPage.getCurrentUrl().contains("/login"),
                "URL should contain '/login'. Actual: " + loginPage.getCurrentUrl());
        Assert.assertTrue(loginPage.isReturningCustomerSectionDisplayed(),
                "Returning customer section should be visible on the Login portal.");
    }

    @Test(groups = {"regression"}, description = "SS-NAV-002: Verify header register link navigates to Registration page")
    public void verifyRegistrationLinkNavigation() {
        HomePage homePage = openHomePage();

        homePage.goToRegister();

        Assert.assertTrue(homePage.getCurrentUrl().contains("/register"),
                "URL should route to '/register'. Actual: " + homePage.getCurrentUrl());
        Assert.assertTrue(homePage.getTitle().toLowerCase().contains("register"),
                "Page title should reference 'register'. Actual: " + homePage.getTitle());
    }

    @Test(groups = {"regression"}, description = "SS-NAV-004: Verify top menu category navigation (Computers)")
    public void verifyCategoryNavigationToComputers() {
        HomePage homePage = openHomePage();

        homePage.clickCategory("Computers");

        Assert.assertTrue(homePage.getCurrentUrl().contains("/computers"),
                "URL should route to '/computers'. Actual: " + homePage.getCurrentUrl());
        Assert.assertTrue(homePage.getTitle().toLowerCase().contains("computers"),
                "Page title should reflect 'Computers'. Actual: " + homePage.getTitle());
    }
}
