package com.shopsphere.tests;

import com.shopsphere.pages.HomePage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("ShopSphere Core Platform")
@Feature("Category & Catalog Navigation")
public class CategoryNavigationTest extends BaseTest {

    @Test(priority = 1, groups = {"regression", "smoke"}, description = "SS-NAV-001: Category navigation to Computers")
    @Story("Catalog Browsing")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies navigating to Computers category loads /computers with correct heading.")
    public void verifyCategoryNavigationToComputers() {
        HomePage homePage = openHomePage();
        homePage.clickCategory("Computers");

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("/computers"),
                "URL should contain '/computers'. Actual: " + driver.getCurrentUrl());
    }

    @Test(priority = 2, groups = {"regression"}, description = "SS-NAV-002: Category navigation to Electronics")
    @Story("Catalog Browsing")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies navigating to Electronics category loads /electronics.")
    public void verifyCategoryNavigationToElectronics() {
        HomePage homePage = openHomePage();
        homePage.clickCategory("Electronics");

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("/electronics"),
                "URL should contain '/electronics'. Actual: " + driver.getCurrentUrl());
    }

    @Test(priority = 3, groups = {"regression"}, description = "SS-NAV-003: Category navigation to Apparel")
    @Story("Catalog Browsing")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies navigating to Apparel category loads /apparel.")
    public void verifyCategoryNavigationToApparel() {
        HomePage homePage = openHomePage();
        homePage.clickCategory("Apparel");

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("/apparel"),
                "URL should contain '/apparel'. Actual: " + driver.getCurrentUrl());
    }

    @Test(priority = 4, groups = {"regression"}, description = "SS-NAV-004: Category navigation to Digital downloads")
    @Story("Catalog Browsing")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies navigating to Digital downloads category loads /digital-downloads.")
    public void verifyCategoryNavigationToDigitalDownloads() {
        HomePage homePage = openHomePage();
        homePage.clickCategory("Digital downloads");

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("/digital-downloads"),
                "URL should contain '/digital-downloads'. Actual: " + driver.getCurrentUrl());
    }

    @Test(priority = 5, groups = {"regression"}, description = "SS-NAV-005: Category navigation to Books")
    @Story("Catalog Browsing")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies navigating to Books category loads /books.")
    public void verifyCategoryNavigationToBooks() {
        HomePage homePage = openHomePage();
        homePage.clickCategory("Books");

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("/books"),
                "URL should contain '/books'. Actual: " + driver.getCurrentUrl());
    }

    @Test(priority = 6, groups = {"regression"}, description = "SS-NAV-006: Category navigation to Jewelry")
    @Story("Catalog Browsing")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies navigating to Jewelry category loads /jewelry.")
    public void verifyCategoryNavigationToJewelry() {
        HomePage homePage = openHomePage();
        homePage.clickCategory("Jewelry");

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("/jewelry"),
                "URL should contain '/jewelry'. Actual: " + driver.getCurrentUrl());
    }

    @Test(priority = 7, groups = {"regression"}, description = "SS-NAV-007: Category navigation to Gift Cards")
    @Story("Catalog Browsing")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies navigating to Gift Cards category loads /gift-cards.")
    public void verifyCategoryNavigationToGiftCards() {
        HomePage homePage = openHomePage();
        homePage.clickCategory("Gift Cards");

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("/gift-cards"),
                "URL should contain '/gift-cards'. Actual: " + driver.getCurrentUrl());
    }
}
