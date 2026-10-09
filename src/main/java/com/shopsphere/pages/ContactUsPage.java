package com.shopsphere.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page object modeling customer service contact form (/contactus).
 */
public class ContactUsPage extends BasePage {

    private final By fullNameInput = By.cssSelector("#FullName");
    private final By emailInput = By.cssSelector("#Email");
    private final By enquiryInput = By.cssSelector("#Enquiry");
    private final By submitButton = By.cssSelector("button[name='send-email'], button.contact-us-button");

    private final By fullNameError = By.cssSelector("#FullName-error");
    private final By emailError = By.cssSelector("#Email-error");
    private final By enquiryError = By.cssSelector("#Enquiry-error");
    private final By resultMessage = By.cssSelector(".result");

    public ContactUsPage(WebDriver driver) {
        super(driver);
    }

    public ContactUsPage enterFullName(String name) {
        type(fullNameInput, name);
        return this;
    }

    public ContactUsPage enterEmail(String email) {
        type(emailInput, email);
        return this;
    }

    public ContactUsPage enterEnquiry(String enquiry) {
        type(enquiryInput, enquiry);
        return this;
    }

    public ContactUsPage submit() {
        click(submitButton);
        return this;
    }

    public String getFullNameError() {
        return isDisplayed(fullNameError) ? getText(fullNameError) : "";
    }

    public String getEmailError() {
        return isDisplayed(emailError) ? getText(emailError) : "";
    }

    public String getEnquiryError() {
        return isDisplayed(enquiryError) ? getText(enquiryError) : "";
    }

    public String getResultMessage() {
        return isDisplayed(resultMessage) ? getText(resultMessage) : "";
    }
}
