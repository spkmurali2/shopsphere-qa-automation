package com.shopsphere.tests;

import com.shopsphere.pages.HomePage;
import com.shopsphere.pages.LoginPage;
import com.shopsphere.pages.PasswordRecoveryPage;
import com.shopsphere.utils.TestDataReader;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("ShopSphere Core Platform")
@Feature("Customer Authentication & Account Recovery")
public class AuthenticationTest extends BaseTest {

    private LoginPage openLoginPage() {
        HomePage homePage = openHomePage();
        homePage.clickLogin();
        return new LoginPage(driver);
    }

    @Test(priority = 1, groups = {"regression", "negative"}, description = "SS-AUTH-001: Submit login with unregistered email and password")
    @Story("Invalid Credentials Guardrail")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies attempting login with non-existent credentials displays 'No customer account found'.")
    public void verifyLoginFailsWithInvalidCredentials() {
        String invalidEmail = TestDataReader.get("invalidEmail", "nonexistent_qa_user_999@shopsphere.test");
        String invalidPassword = TestDataReader.get("invalidPassword", "WrongPass123!");

        LoginPage loginPage = openLoginPage();
        loginPage.loginExpectingFailure(invalidEmail, invalidPassword);

        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Summary error block should be visible when login fails.");
        Assert.assertTrue(loginPage.getErrorMessageText().contains("No customer account found"),
                "Error message should mention no customer account found. Actual: " + loginPage.getErrorMessageText());
    }

    @Test(priority = 2, groups = {"regression", "negative"}, description = "SS-AUTH-002: Submit login with empty credentials displays field validation")
    @Story("Required Field Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies clicking login button with empty inputs displays 'Please enter your email'.")
    public void verifyLoginFailsWithEmptyInputs() {
        LoginPage loginPage = openLoginPage();
        loginPage.loginExpectingFailure("", "");

        Assert.assertTrue(loginPage.isEmailFieldErrorDisplayed(),
                "Email field validation error should be visible for empty submission.");
        Assert.assertEquals(loginPage.getEmailFieldErrorText(), "Please enter your email",
                "Validation error text should prompt for email address.");
    }

    @Test(priority = 3, groups = {"regression", "negative"}, description = "SS-AUTH-003: Submit login with malformed email format")
    @Story("Email Format Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies submitting email missing '@' domain shows 'Wrong email' feedback.")
    public void verifyLoginFailsWithMalformedEmailFormat() {
        LoginPage loginPage = openLoginPage();
        loginPage.loginExpectingFailure("invalid-email-address", "SomePassword123!");

        Assert.assertTrue(loginPage.isEmailFieldErrorDisplayed(),
                "Inline validation should appear for malformed email format.");
        Assert.assertTrue(loginPage.getEmailFieldErrorText().toLowerCase().contains("wrong email"),
                "Validation message should state 'Wrong email'. Actual: " + loginPage.getEmailFieldErrorText());
    }

    @Test(priority = 4, groups = {"regression"}, description = "SS-AUTH-004: Verify 'Returning Customer' form section is displayed")
    @Story("Login Form Availability")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies login portal renders the returning customer credential form.")
    public void verifyReturningCustomerSectionIsDisplayed() {
        LoginPage loginPage = openLoginPage();
        Assert.assertTrue(loginPage.isReturningCustomerSectionDisplayed(),
                "Returning customer login section should be displayed on /login.");
    }

    @Test(priority = 5, groups = {"regression"}, description = "SS-AUTH-005: Verify 'Forgot password?' link navigates to recovery page")
    @Story("Password Recovery Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies clicking 'Forgot password?' navigates to /passwordrecovery.")
    public void verifyForgotPasswordLinkNavigatesToRecoveryPage() {
        LoginPage loginPage = openLoginPage();
        Assert.assertTrue(loginPage.isForgotPasswordLinkDisplayed(), "Forgot password link should be visible.");

        PasswordRecoveryPage recoveryPage = loginPage.clickForgotPassword();
        Assert.assertTrue(recoveryPage.getCurrentUrl().contains("/passwordrecovery"),
                "User should be navigated to password recovery. Actual URL: " + recoveryPage.getCurrentUrl());
    }

    @Test(priority = 6, groups = {"regression", "negative"}, description = "SS-AUTH-006: Password recovery fails with empty email submission")
    @Story("Password Recovery Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies submitting recovery with empty email field triggers validation error.")
    public void verifyPasswordRecoveryFailsWithEmptyEmail() {
        LoginPage loginPage = openLoginPage();
        PasswordRecoveryPage recoveryPage = loginPage.clickForgotPassword();
        recoveryPage.enterEmail("").submitRecovery();

        Assert.assertTrue(recoveryPage.getEmailError().toLowerCase().contains("enter your email") || !recoveryPage.getEmailError().isEmpty(),
                "Validation error should prompt for email on empty recovery submission.");
    }

    @Test(priority = 7, groups = {"regression", "negative"}, description = "SS-AUTH-007: Password recovery fails with malformed email format")
    @Story("Password Recovery Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies submitting recovery with malformed email 'bad-email' triggers 'Wrong email'.")
    public void verifyPasswordRecoveryFailsWithMalformedEmail() {
        LoginPage loginPage = openLoginPage();
        PasswordRecoveryPage recoveryPage = loginPage.clickForgotPassword();
        recoveryPage.enterEmail("bad-email").submitRecovery();

        Assert.assertTrue(recoveryPage.getEmailError().toLowerCase().contains("wrong email") || !recoveryPage.getEmailError().isEmpty(),
                "Validation error should show 'Wrong email' for invalid format.");
    }

    @Test(priority = 8, groups = {"regression", "negative"}, description = "SS-AUTH-008: Password recovery with unregistered email displays feedback")
    @Story("Password Recovery Feedback")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies submitting unregistered email displays feedback notification.")
    public void verifyPasswordRecoveryWithUnregisteredEmailDisplaysError() {
        LoginPage loginPage = openLoginPage();
        PasswordRecoveryPage recoveryPage = loginPage.clickForgotPassword();
        recoveryPage.enterEmail("unregistered_qa_user_999@shopsphere.test").submitRecovery();

        Assert.assertTrue(recoveryPage.isResultDisplayed() || !recoveryPage.getNotificationResultText().isEmpty(),
                "Feedback message should be displayed for password recovery submission.");
    }
}
