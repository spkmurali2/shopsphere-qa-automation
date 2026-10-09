package com.shopsphere.tests;

import com.shopsphere.pages.ContactUsPage;
import com.shopsphere.pages.HomePage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("ShopSphere Core Platform")
@Feature("Footer Links & Customer Services")
public class FooterAndCustomerServiceTest extends BaseTest {

    @Test(priority = 1, groups = {"regression", "negative"}, description = "SS-FOT-001: Contact Us form empty submission validates required fields")
    @Story("Contact Us Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies submitting empty contact enquiry validates Full Name, Email, and Enquiry inputs.")
    public void verifyContactUsFormSubmissionFailsWithEmptyFields() {
        HomePage homePage = openHomePage();
        ContactUsPage contactUsPage = homePage.clickContactUs();

        contactUsPage.submit();

        Assert.assertTrue(contactUsPage.getFullNameError().toLowerCase().contains("name") || !contactUsPage.getFullNameError().isEmpty(),
                "Full name validation error should be displayed.");
        Assert.assertTrue(contactUsPage.getEmailError().toLowerCase().contains("email") || !contactUsPage.getEmailError().isEmpty(),
                "Email validation error should be displayed.");
        Assert.assertTrue(contactUsPage.getEnquiryError().toLowerCase().contains("enquiry") || !contactUsPage.getEnquiryError().isEmpty(),
                "Enquiry validation error should be displayed.");
    }

    @Test(priority = 2, groups = {"regression"}, description = "SS-FOT-002: Sitemap page navigation")
    @Story("Footer Information Links")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies clicking Sitemap in footer navigates to /sitemap.")
    public void verifySitemapPageNavigation() {
        HomePage homePage = openHomePage();
        homePage.clickSitemap();

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("/sitemap"),
                "URL should contain '/sitemap'. Actual: " + driver.getCurrentUrl());
    }

    @Test(priority = 3, groups = {"regression"}, description = "SS-FOT-003: Shipping & Returns page navigation")
    @Story("Footer Information Links")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies clicking Shipping & returns in footer navigates to /shipping-returns.")
    public void verifyShippingAndReturnsPageNavigation() {
        HomePage homePage = openHomePage();
        homePage.clickShippingAndReturns();

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("/shipping-returns"),
                "URL should contain '/shipping-returns'. Actual: " + driver.getCurrentUrl());
    }

    @Test(priority = 4, groups = {"regression"}, description = "SS-FOT-004: Privacy Notice page navigation")
    @Story("Footer Information Links")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies clicking Privacy notice in footer navigates to /privacy-notice.")
    public void verifyPrivacyNoticePageNavigation() {
        HomePage homePage = openHomePage();
        homePage.clickPrivacyNotice();

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("/privacy-notice"),
                "URL should contain '/privacy-notice'. Actual: " + driver.getCurrentUrl());
    }

    @Test(priority = 5, groups = {"regression"}, description = "SS-FOT-005: Conditions of Use page navigation")
    @Story("Footer Information Links")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies clicking Conditions of use in footer navigates to /conditions-of-use.")
    public void verifyConditionsOfUsePageNavigation() {
        HomePage homePage = openHomePage();
        homePage.clickConditionsOfUse();

        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("/conditions-of-use"),
                "URL should contain '/conditions-of-use'. Actual: " + driver.getCurrentUrl());
    }

    @Test(priority = 6, groups = {"regression"}, description = "SS-FOT-006: Customer currency switcher toggles pricing display")
    @Story("Localization")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies switching currency between US Dollar and Euro updates currency symbol on catalog products.")
    public void verifyCurrencySwitcherTogglesProductPrices() {
        HomePage homePage = openHomePage();
        homePage.selectCurrency("Euro");

        String euroPrice = homePage.getFirstFeaturedProductPriceText();
        Assert.assertTrue(euroPrice.contains("€") || euroPrice.contains("Euro"),
                "Product price should reflect Euro symbol (€). Actual: " + euroPrice);

        // Switch back to US Dollar
        homePage.selectCurrency("US Dollar");
        String dollarPrice = homePage.getFirstFeaturedProductPriceText();
        Assert.assertTrue(dollarPrice.contains("$"),
                "Product price should reflect Dollar symbol ($). Actual: " + dollarPrice);
    }
}
