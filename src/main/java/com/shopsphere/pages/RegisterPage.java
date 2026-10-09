package com.shopsphere.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object modeling customer registration portal (/register).
 */
public class RegisterPage extends BasePage {

    private final By genderMale = By.cssSelector("#gender-male");
    private final By genderFemale = By.cssSelector("#gender-female");
    private final By firstNameInput = By.cssSelector("#FirstName");
    private final By lastNameInput = By.cssSelector("#LastName");
    private final By emailInput = By.cssSelector("#Email");
    private final By passwordInput = By.cssSelector("#Password");
    private final By confirmPasswordInput = By.cssSelector("#ConfirmPassword");
    private final By registerButton = By.cssSelector("#register-button");

    private final By firstNameError = By.cssSelector("#FirstName-error");
    private final By lastNameError = By.cssSelector("#LastName-error");
    private final By emailError = By.cssSelector("#Email-error");
    private final By passwordError = By.cssSelector("#Password-error");
    private final By confirmPasswordError = By.cssSelector("#ConfirmPassword-error");
    private final By summaryError = By.cssSelector(".message-error");
    private final By registrationResult = By.cssSelector(".result, .page-body .result");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public RegisterPage selectGenderMale() {
        click(genderMale);
        return this;
    }

    public RegisterPage enterFirstName(String firstName) {
        type(firstNameInput, firstName);
        return this;
    }

    public RegisterPage enterLastName(String lastName) {
        type(lastNameInput, lastName);
        return this;
    }

    public RegisterPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    public RegisterPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public RegisterPage enterConfirmPassword(String confirmPassword) {
        type(confirmPasswordInput, confirmPassword);
        return this;
    }

    public RegisterPage clickRegisterButton() {
        click(registerButton);
        return this;
    }

    public boolean isFirstNameInputDisplayed() {
        return isDisplayed(firstNameInput);
    }

    public boolean isLastNameInputDisplayed() {
        return isDisplayed(lastNameInput);
    }

    public boolean isEmailInputDisplayed() {
        return isDisplayed(emailInput);
    }

    public boolean isPasswordInputDisplayed() {
        return isDisplayed(passwordInput);
    }

    public String getFirstNameError() {
        return isDisplayed(firstNameError) ? getText(firstNameError) : "";
    }

    public String getLastNameError() {
        return isDisplayed(lastNameError) ? getText(lastNameError) : "";
    }

    public String getEmailError() {
        return isDisplayed(emailError) ? getText(emailError) : "";
    }

    public String getPasswordError() {
        return isDisplayed(passwordError) ? getText(passwordError) : "";
    }

    public String getConfirmPasswordError() {
        return isDisplayed(confirmPasswordError) ? getText(confirmPasswordError) : "";
    }

    public String getSummaryErrorMessage() {
        if (isDisplayed(emailError)) {
            return getText(emailError);
        }
        return isDisplayed(summaryError) ? getText(summaryError) : "";
    }

    public String getRegistrationResultText() {
        return isDisplayed(registrationResult) ? getText(registrationResult) : "";
    }

    public boolean isRegistrationSuccessDisplayed() {
        return isDisplayed(registrationResult);
    }
}
