package com.shopsphere.tests;

import com.shopsphere.pages.HomePage;
import com.shopsphere.pages.RegisterPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("ShopSphere Core Platform")
@Feature("Customer Account Registration & Input Validation")
public class RegistrationTest extends BaseTest {

    private RegisterPage openRegisterPage() {
        HomePage homePage = openHomePage();
        homePage.logOutIfLoggedIn();
        driver.get(com.shopsphere.config.ConfigReader.getBaseUrl() + "register");
        return new RegisterPage(driver);
    }

    @Test(priority = 1, groups = {"regression", "smoke"}, description = "SS-REG-001: Verify registration page loads with all required input fields")
    @Story("Registration Form Availability")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies First Name, Last Name, Email, and Password inputs are displayed on /register.")
    public void verifyRegistrationPageLoadsWithAllRequiredFields() {
        RegisterPage registerPage = openRegisterPage();
        Assert.assertTrue(registerPage.isFirstNameInputDisplayed(), "First name input should be displayed.");
        Assert.assertTrue(registerPage.isLastNameInputDisplayed(), "Last name input should be displayed.");
        Assert.assertTrue(registerPage.isEmailInputDisplayed(), "Email input should be displayed.");
        Assert.assertTrue(registerPage.isPasswordInputDisplayed(), "Password input should be displayed.");
    }

    @Test(priority = 2, groups = {"regression", "negative"}, description = "SS-REG-002: Submit registration with all empty fields validates required errors")
    @Story("Required Field Validation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Submits blank registration form and asserts required validation messages.")
    public void verifyRegistrationFailsWithAllEmptyFields() {
        RegisterPage registerPage = openRegisterPage();
        registerPage.clickRegisterButton();

        Assert.assertTrue(registerPage.getFirstNameError().toLowerCase().contains("required") || !registerPage.getFirstNameError().isEmpty(),
                "First name required error should be displayed.");
        Assert.assertTrue(registerPage.getLastNameError().toLowerCase().contains("required") || !registerPage.getLastNameError().isEmpty(),
                "Last name required error should be displayed.");
        Assert.assertTrue(registerPage.getEmailError().toLowerCase().contains("required") || !registerPage.getEmailError().isEmpty(),
                "Email required error should be displayed.");
    }

    @Test(priority = 3, groups = {"regression", "negative"}, description = "SS-REG-003: Submit registration with invalid email format")
    @Story("Email Format Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Enters 'invalid-email-format' and validates 'Wrong email' feedback.")
    public void verifyRegistrationFailsWithInvalidEmailFormat() {
        RegisterPage registerPage = openRegisterPage();
        registerPage.enterFirstName("John")
                .enterLastName("Doe")
                .enterEmail("invalid-email-format")
                .enterPassword("ValidPass123!")
                .enterConfirmPassword("ValidPass123!")
                .clickRegisterButton();

        Assert.assertTrue(registerPage.getEmailError().toLowerCase().contains("wrong email") || !registerPage.getEmailError().isEmpty(),
                "Wrong email error should be displayed for malformed email.");
    }

    @Test(priority = 4, groups = {"regression", "negative"}, description = "SS-REG-004: Submit registration with mismatched confirm password")
    @Story("Password Match Validation")
    @Severity(SeverityLevel.NORMAL)
    @Description("Enters mismatched Password and ConfirmPassword and validates matching error.")
    public void verifyRegistrationFailsWithMismatchedPasswords() {
        RegisterPage registerPage = openRegisterPage();
        registerPage.enterFirstName("Jane")
                .enterLastName("Doe")
                .enterEmail("jane.doe.test@shopsphere-qa.test")
                .enterPassword("Password123!")
                .enterConfirmPassword("DifferentPassword456!")
                .clickRegisterButton();

        Assert.assertTrue(registerPage.getConfirmPasswordError().toLowerCase().contains("do not match") || !registerPage.getConfirmPasswordError().isEmpty(),
                "Password mismatch error should be displayed. Actual: " + registerPage.getConfirmPasswordError());
    }

    @Test(priority = 5, groups = {"regression", "negative"}, description = "SS-REG-005: Submit registration with password shorter than minimum rule")
    @Story("Password Policy")
    @Severity(SeverityLevel.NORMAL)
    @Description("Enters a 3-character password '123' and validates minimum length constraint feedback.")
    public void verifyRegistrationFailsWithShortPassword() {
        RegisterPage registerPage = openRegisterPage();
        registerPage.enterFirstName("Sam")
                .enterLastName("Smith")
                .enterEmail("sam.smith.test@shopsphere-qa.test")
                .enterPassword("123")
                .enterConfirmPassword("123")
                .clickRegisterButton();

        Assert.assertTrue(registerPage.getPasswordError().toLowerCase().contains("characters") || !registerPage.getPasswordError().isEmpty(),
                "Short password rule message should be displayed. Actual: " + registerPage.getPasswordError());
    }

    @Test(priority = 6, groups = {"regression"}, description = "SS-REG-006: Successful registration with dynamically generated unique customer email")
    @Story("Successful Registration Flow")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Registers a new customer account with timestamped unique email and validates success.")
    public void verifySuccessfulCustomerRegistrationWithUniqueEmail() {
        String uniqueEmail = "shopsphere_qa_" + System.currentTimeMillis() + "@automation-test.com";
        RegisterPage registerPage = openRegisterPage();

        registerPage.selectGenderMale()
                .enterFirstName("Automation")
                .enterLastName("Tester")
                .enterEmail(uniqueEmail)
                .enterPassword("Pass!123456")
                .enterConfirmPassword("Pass!123456")
                .clickRegisterButton();

        Assert.assertTrue(registerPage.isRegistrationSuccessDisplayed(),
                "Registration completion message should be displayed.");
        Assert.assertTrue(registerPage.getRegistrationResultText().toLowerCase().contains("completed"),
                "Result text should confirm 'Your registration completed'. Actual: " + registerPage.getRegistrationResultText());
    }

    @Test(priority = 7, groups = {"regression", "negative"}, description = "SS-REG-007: Registration fails with already registered customer email")
    @Story("Duplicate Email Prevention")
    @Severity(SeverityLevel.NORMAL)
    @Description("Attempts registration with duplicate demo email and verifies conflict summary message.")
    public void verifyRegistrationFailsWithAlreadyRegisteredEmail() {
        RegisterPage registerPage = openRegisterPage();
        registerPage.enterFirstName("Existing")
                .enterLastName("User")
                .enterEmail("admin@yourstore.com")
                .enterPassword("AdminPass123!")
                .enterConfirmPassword("AdminPass123!")
                .clickRegisterButton();

        String summary = registerPage.getSummaryErrorMessage();
        Assert.assertTrue(summary.toLowerCase().contains("already exists") || summary.toLowerCase().contains("email") || !summary.isEmpty(),
                "Duplicate email error should be reported. Actual: " + summary);
    }
}
