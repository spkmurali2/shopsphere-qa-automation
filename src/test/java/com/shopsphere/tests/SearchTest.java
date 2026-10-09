package com.shopsphere.tests;

import com.shopsphere.pages.HomePage;
import com.shopsphere.pages.ProductPage;
import com.shopsphere.pages.SearchResultsPage;
import com.shopsphere.utils.TestDataReader;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

@Epic("ShopSphere Core Platform")
@Feature("Product Search & Discovery")
public class SearchTest extends BaseTest {

    @Test(priority = 1, groups = {"regression", "smoke"}, description = "SS-SRCH-001: Exact product keyword search returns matching items")
    @Story("Search Accuracy")
    @Severity(SeverityLevel.BLOCKER)
    @Description("Verifies exact search term 'Apple MacBook Pro' returns relevant product cards.")
    public void verifySearchForExistingProductReturnsRelevantResults() {
        String keyword = TestDataReader.get("existingProduct", "Apple MacBook Pro");
        HomePage homePage = openHomePage();

        SearchResultsPage resultsPage = homePage.searchForProduct(keyword);
        Assert.assertTrue(resultsPage.hasResults(), "Search should return product results for: " + keyword);

        List<String> titles = resultsPage.getProductTitles();
        boolean matchFound = titles.stream().anyMatch(title -> title.toLowerCase().contains("macbook"));
        Assert.assertTrue(matchFound, "Results list should contain MacBook item. Found: " + titles);
    }

    @Test(priority = 2, groups = {"regression"}, description = "SS-SRCH-002: Partial keyword token search returns matches")
    @Story("Search Tokenization")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies searching with a partial token 'Build your own' returns matching products.")
    public void verifyPartialKeywordSearchReturnsMatches() {
        String keyword = TestDataReader.get("partialKeyword", "Build your own");
        HomePage homePage = openHomePage();

        SearchResultsPage resultsPage = homePage.searchForProduct(keyword);
        Assert.assertTrue(resultsPage.hasResults(), "Partial search should return results for: " + keyword);

        List<String> titles = resultsPage.getProductTitles();
        boolean matchFound = titles.stream().anyMatch(title -> title.toLowerCase().contains("build"));
        Assert.assertTrue(matchFound, "Results should contain product matching partial token. Found: " + titles);
    }

    @Test(priority = 3, groups = {"regression", "negative"}, description = "SS-SRCH-003: Non-existent product search displays no results message")
    @Story("Negative Search")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies searching for an unknown SKU renders the official no-results notification.")
    public void verifySearchForNonExistentProductShowsNoResultsMessage() {
        String keyword = TestDataReader.get("unknownProduct", "XYZ-Product-Does-Not-Exist");
        HomePage homePage = openHomePage();

        SearchResultsPage resultsPage = homePage.searchForProduct(keyword);
        Assert.assertTrue(resultsPage.isNoResultsMessageDisplayed(),
                "No-results message should be displayed for non-existent product query.");
        Assert.assertTrue(resultsPage.getNoResultsMessageText().toLowerCase().contains("no products were found"),
                "Banner text should confirm no products found. Actual: " + resultsPage.getNoResultsMessageText());
    }

    @Test(priority = 4, groups = {"regression"}, description = "SS-SRCH-004: Navigate to product detail from search result card")
    @Story("Search Result Navigation")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Verifies clicking a product card from search results opens the Product Details Page.")
    public void verifyNavigationFromSearchResultsToProductDetail() {
        String keyword = TestDataReader.get("existingProduct", "Apple MacBook Pro");
        HomePage homePage = openHomePage();

        SearchResultsPage resultsPage = homePage.searchForProduct(keyword);
        ProductPage productPage = resultsPage.openFirstProduct();

        Assert.assertTrue(productPage.getProductTitle().toLowerCase().contains("macbook"),
                "Product detail page should show the selected item. Title: " + productPage.getProductTitle());
    }

    @Test(priority = 5, groups = {"regression", "negative"}, description = "SS-SRCH-005: Empty keyword search triggers native alert dialog")
    @Story("Input Boundary")
    @Severity(SeverityLevel.MINOR)
    @Description("Verifies submitting an empty search query prompts the user via JavaScript alert dialog.")
    public void verifyEmptySearchSubmissionHandledGracefully() {
        HomePage homePage = openHomePage();
        String alertText = homePage.submitEmptySearchAndGetAlertText();

        Assert.assertFalse(alertText.isEmpty(), "Browser alert dialog should be triggered for empty search.");
        Assert.assertTrue(alertText.toLowerCase().contains("please enter some search keyword"),
                "Alert dialog text should ask for keyword. Actual: " + alertText);
    }

    @Test(priority = 6, groups = {"regression"}, description = "SS-SRCH-006: Search is case-insensitive")
    @Story("Search Normalization")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies searching in all lowercase ('apple macbook pro') returns the same results.")
    public void verifySearchIsCaseInsensitive() {
        HomePage homePage = openHomePage();
        SearchResultsPage resultsPage = homePage.searchForProduct("apple macbook pro");

        Assert.assertTrue(resultsPage.hasResults(), "Case-insensitive query should return results.");
        Assert.assertTrue(resultsPage.getProductTitles().stream().anyMatch(t -> t.toLowerCase().contains("macbook")),
                "Results should include MacBook for lowercase search.");
    }

    @Test(priority = 7, groups = {"regression"}, description = "SS-SRCH-007: Search with leading and trailing whitespaces is trimmed")
    @Story("Search Sanitization")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies query with whitespace padding '   Apple MacBook Pro   ' resolves accurately.")
    public void verifySearchWithLeadingAndTrailingWhitespacesTrimsCorrectly() {
        HomePage homePage = openHomePage();
        SearchResultsPage resultsPage = homePage.searchForProduct("   Apple MacBook Pro   ");

        Assert.assertTrue(resultsPage.hasResults(), "Whitespace-padded query should be trimmed and return results.");
    }

    @Test(priority = 8, groups = {"regression", "negative"}, description = "SS-SRCH-008: Search with special characters returns graceful no-results")
    @Story("Special Character Handling")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies query with special characters '!@#$%^&*' does not crash and renders no-results.")
    public void verifySearchWithSpecialCharactersReturnsGracefulNoResults() {
        HomePage homePage = openHomePage();
        SearchResultsPage resultsPage = homePage.searchForProduct("!@#$%^&*()");

        Assert.assertTrue(resultsPage.isNoResultsMessageDisplayed(),
                "Special characters search should display no-results banner without application errors.");
    }

    @Test(priority = 9, groups = {"regression"}, description = "SS-SRCH-009: Search by brand keyword returns catalog items")
    @Story("Brand Search")
    @Severity(SeverityLevel.NORMAL)
    @Description("Verifies brand query 'Apple' yields matching catalogue items.")
    public void verifySearchByBrandKeyword() {
        HomePage homePage = openHomePage();
        SearchResultsPage resultsPage = homePage.searchForProduct("Apple");

        Assert.assertTrue(resultsPage.hasResults(), "Brand query 'Apple' should return matching items.");
    }

    @Test(priority = 10, groups = {"regression", "negative"}, description = "SS-SRCH-010: Search with extremely long query string boundary")
    @Story("Input Boundary")
    @Severity(SeverityLevel.MINOR)
    @Description("Verifies 120-character boundary query string is handled safely without crashing.")
    public void verifySearchWithExtremelyLongQueryString() {
        String longQuery = "a".repeat(120);
        HomePage homePage = openHomePage();
        SearchResultsPage resultsPage = homePage.searchForProduct(longQuery);

        Assert.assertTrue(resultsPage.isNoResultsMessageDisplayed(),
                "Boundary query should safely render no-results message.");
    }
}
