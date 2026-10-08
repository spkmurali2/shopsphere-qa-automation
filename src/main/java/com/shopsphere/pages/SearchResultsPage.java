package com.shopsphere.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;

/**
 * Page object for the search results page (/search).
 */
public class SearchResultsPage extends BasePage {

    private final By searchKeywordInput = By.cssSelector("input.search-text, #q");
    private final By searchPageButton = By.cssSelector("button.search-button");
    private final By productCards = By.cssSelector(".item-box .product-item, .product-item");
    private final By productTitles = By.cssSelector(".product-title a");
    private final By noResultsBanner = By.cssSelector(".no-result, .search-results .warning");

    public SearchResultsPage(WebDriver driver) {
        super(driver);
    }

    public int getResultsCount() {
        try {
            return driver.findElements(productCards).size();
        } catch (Exception e) {
            return 0;
        }
    }

    public List<String> getProductTitles() {
        List<WebElement> titleElements = driver.findElements(productTitles);
        List<String> titles = new ArrayList<>();
        for (WebElement el : titleElements) {
            String title = el.getText().trim();
            if (!title.isEmpty()) {
                titles.add(title);
            }
        }
        return titles;
    }

    public boolean hasProductTitleContaining(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        for (String title : getProductTitles()) {
            if (title.toLowerCase().contains(lowerKeyword)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Clicks on the product title link matching the specified text and returns the ProductPage.
     */
    public ProductPage openProductByTitle(String targetTitle) {
        By productLink = By.xpath("//h2[@class='product-title']/a[contains(normalize-space(), '" + targetTitle + "')]");
        click(productLink);
        waitForPageReady();
        return new ProductPage(driver);
    }

    /**
     * Clicks on the first visible product result card.
     */
    public ProductPage openFirstProduct() {
        click(productTitles);
        waitForPageReady();
        return new ProductPage(driver);
    }

    public boolean isNoResultsMessageDisplayed() {
        return isDisplayed(noResultsBanner);
    }

    public String getNoResultsMessage() {
        return isDisplayed(noResultsBanner) ? getText(noResultsBanner) : "";
    }

    public String getSearchInputKeyword() {
        try {
            return waitForElement(searchKeywordInput).getAttribute("value");
        } catch (Exception e) {
            return "";
        }
    }
}
