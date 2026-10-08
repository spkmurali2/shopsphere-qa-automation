package com.shopsphere.tests;

import com.shopsphere.pages.HomePage;
import com.shopsphere.pages.LoginPage;
import com.shopsphere.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Negative test suite validating authentication guardrails and error messaging.
 */
public class AuthenticationNegativeTest extends BaseTest {

    @Test(groups = {"regression", "negative"}, description = "SS-AUTH-001: Unregistered credentials show login error summary")
    public void verifyLoginFailsWithInvalidCredentials() {
        String invalidEmail = TestDataReader.get("invalidEmail", "unregistered.qa.testuser@example.com");
        String invalidPassword = TestDataReader.get("invalidPassword", "WrongPassword999!");

        HomePage homePage = openHomePage();
        LoginPage loginPage = homePage.goToLogin();

        loginPage.loginExpectingFailure(invalidEmail, invalidPassword);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Error banner should be visible after submitting invalid credentials.");
        Assert.assertTrue(loginPage.getErrorMessageText().contains("unsuccessful") ||
                        loginPage.getErrorMessageText().contains("No customer account found"),
                "Error message should state unsuccessful login. Actual: " + loginPage.getErrorMessageText());
    }

    @Test(groups = {"regression", "negative"}, description = "SS-AUTH-002: Submitting empty login triggers field validation")
    public void verifyLoginFailsWithEmptyInputs() {
        HomePage homePage = openHomePage();
        LoginPage loginPage = homePage.goToLogin();

        loginPage.clickLogin();

        // In nopCommerce, client-side email error "Please enter your email" is shown under the Email field
        Assert.assertTrue(loginPage.isEmailFieldErrorDisplayed(),
                "Validation message should be displayed for missing email.");
        Assert.assertTrue(loginPage.getEmailFieldErrorText().toLowerCase().contains("enter your email"),
                "Validation text should prompt for email. Actual: " + loginPage.getEmailFieldErrorText());
    }
}
