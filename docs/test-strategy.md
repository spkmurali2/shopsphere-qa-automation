# Test Strategy: ShopSphere QA Automation

## 1. Overview
This document explains the testing strategy for the ShopSphere automation project. The goal is to verify core e-commerce user journeys on the [nopCommerce demo store](https://demo.nopcommerce.com/) using an automated regression suite and targeted manual exploration.

---

## 2. Scope of Testing

### In-Scope
- **Homepage and Header:** Navigation menus, header search, logo, and links to cart and wishlist.
- **Product Search:** Exact keyword match, partial match, case sensitivity, leading/trailing spaces, empty query alerts, special characters, and no-results banners.
- **Product Details:** Product title, price display, breadcrumbs, default quantity, customer reviews link, and add-to-wishlist button.
- **Shopping Cart:** Adding items from product pages, updating quantities, removing items, empty cart state, and coupon code validation.
- **Wishlist:** Adding items, shareable URL generation, item removal, and moving wishlist items to the cart.
- **Registration and Authentication:** Required field validation, malformed email checks, password mismatch, successful registration with unique email, and password recovery form validation.
- **Category Navigation & Footer:** Navigating top-level categories (Computers, Electronics, Apparel, etc.) and footer informational pages (Contact Us, Privacy Notice, Conditions of Use).

### Out of Scope
- **Payment Processing & Order Completion:** The public demo does not offer a sandboxed payment gateway, so real credit card payments and order completion are not automated.
- **Performance & Load Testing:** Testing server load and latency thresholds is outside the scope of this functional UI test framework.
- **Third-Party Integrations:** External OAuth logins and newsletter services are not tested.

---

## 3. Test Levels

### Smoke Tests (P0)
- **File:** `testng-smoke.xml` (6 tests)
- **Goal:** Fast sanity check to confirm core pages and navigation are accessible.
- **Cadence:** Run locally as a quick check and in GitHub Actions on every push or pull request.

### Regression Tests (P1 & P2)
- **File:** `testng.xml` (66 tests)
- **Goal:** Comprehensive functional coverage across search, product pages, cart, wishlist, authentication, and categories.
- **Cadence:** Run before merges or release candidates.

### Negative & Boundary Tests
- **Goal:** Verify that invalid or unusual inputs produce clear error messages rather than unhandled server errors.
- **Examples:** Empty search submissions, non-existent products, passwords under 6 characters, invalid email formats, and setting cart quantity to 0.

---

## 4. Test Prioritization

Testing efforts are prioritized by user journey importance:

1. **P0 (Critical):** Core shopping funnel — Homepage access, basic search, view product, add to cart. If these fail, the core shopping experience is blocked.
2. **P1 (High):** Cart updates, item removal, user registration, login validation, and category navigation.
3. **P2 (Medium):** Password recovery, wishlist management, search edge cases, footer links, and Contact Us form validation.
4. **P3 (Low):** Minor edge cases, such as very long search queries (120+ characters) or empty coupon submissions.

---

## 5. Framework Architecture & Technical Design

The automation framework is built with Java, Selenium WebDriver, TestNG, and Maven:

- **Page Object Model (POM):** Page elements and actions are encapsulated in classes under `com.shopsphere.pages`, keeping test logic in `com.shopsphere.tests` readable and easier to maintain.
- **Explicit Waits:** `BasePage` uses `WebDriverWait` with `ExpectedConditions` (visibility, clickability) instead of hard-coded sleeps (`Thread.sleep`). A JavaScript click fallback handles cases where transient notification banners overlay buttons.
- **Browser Lifecycle:** `DriverFactory` manages the WebDriver instance using `ThreadLocal<WebDriver>`. In `BaseTest`, the driver session is initialized for the test suite and cleanly closed in `@AfterSuite`.
- **Sequential Execution:** Tests are configured to run sequentially (`parallel="none"` in `testng.xml`) to prevent state conflicts when interacting with the cart and session.
- **External Configuration:** Settings are managed by `ConfigReader` with command-line overrides taking precedence (e.g. `-Dheadless=true`), followed by `config/config.properties`.
- **External Test Data:** Product names, expected titles, and search terms are kept in `src/test/resources/config/testdata.properties` and read via `TestDataReader`.
- **Failure Evidence:** `TestListener` implements TestNG's `ITestListener`. When a test fails, it captures a screenshot to `test-output/screenshots/` and attaches it to the Allure report.

---

## 6. Test Environments

### Live nopCommerce Demo Store
- URL: `https://demo.nopcommerce.com/`
- Used for local manual exploration and local test execution.
- Because it is a public shared demo, data can occasionally change or be reset.

### Embedded Mock Server
- A lightweight HTTP server (`EmbeddedTestServer`) built into the project using Java's built-in `HttpServer`.
- Enabled via `-DmockServer=true`.
- Simulates the required HTML routes and forms locally without external network calls or bot protection issues.
- Used in GitHub Actions CI to ensure fast, deterministic pipeline runs.
- **Note:** Passing tests on the mock server verify the framework logic and test assertions, but do not prove that the live demo site passed.

---

## 7. Defect & Release Workflow

- When an unexpected issue is found during exploratory or automated testing, it is logged using the format in [defect-template.md](defect-template.md).
- Before merging or tagging changes, the pre-flight checks in [release-checklist.md](release-checklist.md) are verified.
