package com.shopsphere.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object for the Customer Login page (/login).
 */
public class LoginPage extends BasePage {

    private final By emailInput = By.id("Email");
    private final By passwordInput = By.id("Password");
    private final By loginButton = By.cssSelector("button.login-button");
    private final By returningCustomerTitle = By.xpath("//*[contains(text(), 'Returning Customer')] | //div[contains(@class, 'returning-wrapper')]");
    private final By validationSummaryError = By.cssSelector(".message-error.validation-summary-errors, .message-error");
    private final By emailFieldError = By.id("Email-error");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public boolean isReturningCustomerSectionDisplayed() {
        return isDisplayed(returningCustomerTitle);
    }

    public LoginPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(passwordInput, password);
        return this;
    }

    public void clickLogin() {
        click(loginButton);
        waitForPageReady();
    }

    public LoginPage loginExpectingFailure(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
        return this;
    }

    public boolean isErrorMessageDisplayed() {
        return isDisplayed(validationSummaryError);
    }

    public String getErrorMessageText() {
        return isDisplayed(validationSummaryError) ? getText(validationSummaryError) : "";
    }

    public boolean isEmailFieldErrorDisplayed() {
        return isDisplayed(emailFieldError);
    }

    public String getEmailFieldErrorText() {
        return isDisplayed(emailFieldError) ? getText(emailFieldError) : "";
    }
}
