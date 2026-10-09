package com.shopsphere.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object modeling password recovery (/passwordrecovery).
 */
public class PasswordRecoveryPage extends BasePage {

    private final By emailInput = By.cssSelector("#Email");
    private final By recoveryButton = By.cssSelector("button[name='send-email'], button.password-recovery-button");
    private final By emailError = By.cssSelector("#Email-error");
    private final By notificationResult = By.cssSelector(".bar-notification p.content, p.result, .result");

    public PasswordRecoveryPage(WebDriver driver) {
        super(driver);
    }

    public PasswordRecoveryPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    public PasswordRecoveryPage submitRecovery() {
        click(recoveryButton);
        return this;
    }

    public String getEmailError() {
        return isDisplayed(emailError) ? getText(emailError) : "";
    }

    public String getNotificationResultText() {
        return isDisplayed(notificationResult) ? getText(notificationResult) : "";
    }

    public boolean isResultDisplayed() {
        return isDisplayed(notificationResult);
    }
}
