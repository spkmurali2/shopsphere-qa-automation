package com.shopsphere.tests;

import com.shopsphere.pages.HomePage;
import com.shopsphere.pages.ProductPage;
import com.shopsphere.pages.SearchResultsPage;
import com.shopsphere.utils.TestDataReader;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Functional and negative test suite for product search and catalog discovery.
 */
public class SearchTest extends BaseTest {

    @Test(groups = {"regression", "smoke"}, description = "SS-SRCH-001: Search for an existing product returns relevant results")
    public void verifySearchForExistingProductReturnsRelevantResults() {
        String keyword = TestDataReader.get("existingProduct", "Apple MacBook Pro");
        HomePage homePage = openHomePage();

        SearchResultsPage searchResultsPage = homePage.searchForProduct(keyword);

        Assert.assertTrue(searchResultsPage.getResultsCount() > 0,
                "Search for '" + keyword + "' should return at least one product.");
        Assert.assertTrue(searchResultsPage.hasProductTitleContaining("MacBook") || searchResultsPage.hasProductTitleContaining("Apple"),
                "Search results should contain relevant product titles matching the query.");
    }

    @Test(groups = {"regression"}, description = "SS-SRCH-002: Search using partial product text returns matching products")
    public void verifyPartialKeywordSearchReturnsMatches() {
        String partialKeyword = TestDataReader.get("partialSearchKeyword", "Build your own");
        HomePage homePage = openHomePage();

        SearchResultsPage searchResultsPage = homePage.searchForProduct(partialKeyword);

        Assert.assertTrue(searchResultsPage.getResultsCount() > 0,
                "Partial search for '" + partialKeyword + "' should yield product matches.");
        Assert.assertTrue(searchResultsPage.hasProductTitleContaining("Build your own"),
                "Results should include products matching partial query.");
    }

    @Test(groups = {"regression"}, description = "SS-SRCH-004: Navigate to product detail page from search results")
    public void verifyNavigationFromSearchResultsToProductDetail() {
        String keyword = TestDataReader.get("existingProduct", "Apple MacBook Pro");
        HomePage homePage = openHomePage();

        SearchResultsPage searchResultsPage = homePage.searchForProduct(keyword);
        Assert.assertTrue(searchResultsPage.getResultsCount() > 0, "Precondition: search results must be present.");

        ProductPage productPage = searchResultsPage.openFirstProduct();

        Assert.assertTrue(productPage.getCurrentUrl().contains("/apple-macbook-pro") || productPage.getProductTitle().toLowerCase().contains("macbook"),
                "Navigating to product should open detailed Product page. Actual URL: " + productPage.getCurrentUrl());
    }

    @Test(groups = {"regression", "negative"}, description = "SS-SRCH-003: Search for non-existent product shows no-results feedback")
    public void verifySearchForNonExistentProductShowsNoResultsMessage() {
        String nonExistentKeyword = TestDataReader.get("unknownProduct", "XYZ-Product-Does-Not-Exist");
        HomePage homePage = openHomePage();

        SearchResultsPage searchResultsPage = homePage.searchForProduct(nonExistentKeyword);

        Assert.assertEquals(searchResultsPage.getResultsCount(), 0,
                "No products should be returned for non-existent SKU.");
        Assert.assertTrue(searchResultsPage.isNoResultsMessageDisplayed(),
                "No results banner should be visible for non-matching queries.");
        Assert.assertTrue(searchResultsPage.getNoResultsMessage().contains("No products were found"),
                "Expected standard no-results message. Actual: " + searchResultsPage.getNoResultsMessage());
    }

    @Test(groups = {"regression", "negative"}, description = "SS-SRCH-005: Empty keyword search displays validation alert")
    public void verifyEmptySearchSubmissionHandledGracefully() {
        HomePage homePage = openHomePage();

        String alertText = homePage.submitEmptySearchAndGetAlertText();

        Assert.assertTrue(alertText.toLowerCase().contains("search keyword"),
                "Expected validation alert prompting for search keyword. Actual: " + alertText);
    }
}
