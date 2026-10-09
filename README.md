# ShopSphere – E-commerce QA Automation

[![CI Automation & Release Quality Gate](https://github.com/spkmurali2/shopsphere-qa-automation/actions/workflows/qa-regression.yml/badge.svg)](https://github.com/spkmurali2/shopsphere-qa-automation/actions/workflows/qa-regression.yml)
[![Allure Report](https://img.shields.io/badge/Allure%20Report-Integrated-brightgreen.svg)](https://github.com/spkmurali2/shopsphere-qa-automation/actions)
[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Selenium WebDriver](https://img.shields.io/badge/Selenium-4.26.0-green.svg)](https://www.selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-7.10.2-blue.svg)](https://testng.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)](https://maven.apache.org/)

---

## 1. Project Overview

**ShopSphere** is an automated quality assurance framework developed in **Java 17**, **Selenium WebDriver 4**, **TestNG**, and **Maven**, integrated with **GitHub Actions CI**. 

The framework automates key functional journeys on a real-world e-commerce platform ([nopCommerce Public Demo Store](https://demo.nopcommerce.com/)). Rather than functioning as a synthetic demo or a collection of scripted browser actions, this project demonstrates how a professional Quality Assurance Engineer approaches real-world web automation: starting from a risk-based test strategy, structuring tests through the **Page Object Model (POM)** design pattern, handling modern asynchronous UI behaviour via **explicit waits**, capturing failure telemetry and screenshots, and establishing deterministic quality gates in continuous integration.

---

## 2. Why This Project Exists

Many QA portfolio projects suffer from two common anti-patterns:
1. **The "Toy Script" Pattern:** A linear `@Test` method filled with raw `driver.findElement(By.xpath(...))` calls, fragile `Thread.sleep(5000)` statements, and superficial assertions (e.g., verifying that a button was clicked rather than verifying the resulting system state).
2. **The "Over-Engineered Monolith" Pattern:** Premature abstraction layers involving complex database mocks, cucumber feature files with single-line implementations, and unnecessary dependencies that add maintenance friction without solving actual quality problems.

**ShopSphere was built to demonstrate realistic, industry-standard test engineering practices:**
* **Clear separation of concerns:** Test classes focus strictly on business intent and assertions; Page Objects encapsulate DOM structure, element locators, and UI interactions.
* **Resilient synchronization:** Zero arbitrary hardcoded sleeps. All asynchronous transitions (AJAX micro-cart notifications, dynamically rendered category menus, Cloudflare security challenges) are synchronized using targeted explicit waits.
* **Production-grade test design:** Positive, negative, and boundary scenarios reflecting how users genuinely interact with e-commerce storefronts.
* **Failure auditability:** Automatic, non-invasive screenshot capture on failure via custom TestNG listeners, preserving evidence for root cause analysis.
* **CI Quality Gating:** Headless regression runs integrated into GitHub Actions, serving as an automated gate that prevents regressions from merging to trunk.

---

## 3. Application Under Test

* **Platform:** [nopCommerce](https://demo.nopcommerce.com/) (ASP.NET Core open-source e-commerce solution).
* **Environment:** Public Shared Demo Store.
* **Characteristics & Realities:**
  * Shared global session state across concurrent anonymous users worldwide.
  * AJAX-driven micro-cart updates and notification bars (`div.bar-notification.success`).
  * Cloudflare Turnstile bot-mitigation checks on initial navigation.
  * Ephemeral product catalog where stock counts and product attributes can fluctuate.

Designing automated tests against a public shared instance forced the automation design to be resilient against network variance, asynchronous DOM updates, and shared state mutations.

---

## 4. QA Approach

Automation is one component of a holistic quality engineering strategy. Automated tests verify known invariants, while exploratory testing uncovers unexpected edge cases and usability risks.

```
+-------------------------------------------------------------+
|                 Charter-Based Exploratory Testing           |
|        (AJAX races, cart state collisions, edge inputs)     |
+-------------------------------------------------------------+
|                Automated Regression Suite (P0 + P1)         |
|      (Nightly scheduled runs & PR validation via Maven)     |
+-------------------------------------------------------------+
|                 Automated Smoke Suite (P0)                  |
|          (Fast health check on critical user paths)         |
+-------------------------------------------------------------+
```

Before writing any automation code, a formal **Test Strategy** and **Test Scenario Traceability Matrix** were drafted:
1. **Test Strategy Document:** Defined scope, test levels, risk assessment, entry/exit criteria, and defect lifecycle ([docs/test-strategy.md](docs/test-strategy.md)).
2. **Test Scenarios Matrix:** Mapped business requirements to deterministic test IDs with explicit preconditions and expected results ([docs/test-scenarios.md](docs/test-scenarios.md)).
3. **Exploratory Charters:** Documented ad-hoc exploratory testing charters targeting asynchronous cart updates and input boundaries ([docs/exploratory-testing.md](docs/exploratory-testing.md)).
4. **Release Governance:** Structured release readiness checklists and QA sign-off templates ([docs/release-checklist.md](docs/release-checklist.md), [docs/qa-signoff-template.md](docs/qa-signoff-template.md)).

---

## 5. Risk-Based Automation Approach

Test automation effort was prioritized by mapping **Business Impact** against **Failure Likelihood**:

| Risk Tier | Area | Rationale | Automation Decision |
| :--- | :--- | :--- | :--- |
| **Tier 1 (Critical)** | Core Catalog Discovery & Search | If users cannot locate products, conversion drops to zero. | Automated in P0 Smoke & Regression |
| **Tier 1 (Critical)** | Cart Management (Add, Quantity, Remove) | Direct path to revenue. Highly dynamic AJAX state makes it prone to UI race conditions. | Automated in P0 Smoke & Regression |
| **Tier 2 (High)** | Navigation & Auth Portals | Critical entry points for registered users and category discovery. | Automated in P1 Regression |
| **Tier 2 (High)** | Input Boundary & Error Validation | Prevents malformed inputs and ensures clear feedback. | Automated in P1 Regression |
| **Tier 3 (Medium)** | Checkout & Payment Gateway | Core revenue journey, but volatile in shared public demo. | **Intentionally manual** (see Section 7) |
| **Tier 4 (Low)** | Static Footer Links, Social Icons | Low business impact, static content. | Deferred to exploratory testing |

---

## 6. What Is Automated

The automated test suite contains **18 test cases** organized across 5 distinct test classes:

### Smoke Suite (`smoke`)
* `SmokeTest.verifyHomePageLoadsSuccessfully`: Validates HTTP status/title, logo presence, and initial page readiness.
* `SmokeTest.verifyMainNavigationMenuIsDisplayed`: Validates top-level navigation categories (Computers, Electronics, Apparel, etc.).
* `SmokeTest.verifySearchInputIsAvailable`: Validates search field interactivity and search trigger button.
* `SmokeTest.verifyShoppingCartLinkIsAccessible`: Validates header micro-cart link and current item badge display.

### Search Suite (`regression`)
* `SearchTest.verifySearchWithExistingProductReturnsRelevantResults`: Searches exact keyword (`Apple MacBook Pro`) and verifies product cards contain matching titles.
* `SearchTest.verifySearchWithPartialKeywordReturnsMatchingItems`: Validates search tokenization with substring (`Build your own`).
* `SearchTest.verifySearchForNonExistentProductDisplaysNoResultsMessage`: Validates no-results alert banner (`No products were found that matched your criteria.`).
* `SearchTest.verifyClickingSearchResultNavigatesToProductDetails`: Validates navigation from search results to individual SKU page and validates product title heading.
* `SearchTest.verifyEmptySearchDisplaysValidationAlert`: Validates native JavaScript alert dialog (`Please enter some search keyword`) when searching with blank input.

### Cart Suite (`regression`)
* `CartTest.verifyAddProductToCartSuccessfully`: Adds product from listing/detail, waits for success notification bar, navigates to cart, and verifies line item presence.
* `CartTest.verifyAddDuplicateProductAccumulatesQuantity`: Adds identical SKU twice and verifies line item quantity increments to 2.
* `CartTest.verifyUpdateCartItemQuantityRecalculatesValues`: Updates item quantity, clicks update button, and verifies quantity input reflects the updated value.
* `CartTest.verifyRemoveItemFromCartRendersEmptyState`: Removes line item and validates empty cart placeholder banner (`Your Shopping Cart is empty!`).

### Navigation Suite (`regression`)
* `NavigationTest.verifyCategoryNavigationToComputersPage`: Navigates via top category menu to Computers and verifies page heading and URL slug.
* `NavigationTest.verifyNavigationToLoginPage`: Navigates to `/login` via header link and validates page title and heading.
* `NavigationTest.verifyNavigationToRegisterPage`: Navigates to `/register` and validates user registration form availability.

### Authentication & Negative Suite (`regression`, `negative`)
* `AuthenticationNegativeTest.verifyLoginWithUnregisteredCredentialsShowsErrorMessage`: Submits non-existent credentials and validates summary error banner (`Login was unsuccessful. Please correct the errors and try again. No customer account found`).
* `AuthenticationNegativeTest.verifyLoginWithEmptyCredentialsShowsValidationMessage`: Submits empty login form and validates inline email field validation (`Please enter your email`).

---

## 7. What Is Intentionally Not Automated

### End-to-End Checkout & Payment Completion
* **Reasoning:** In an enterprise environment, payment gateways (Stripe, Adyen, PayPal) are tested against dedicated sandbox environments with deterministic test cards and isolated accounts. The public nopCommerce demo (`demo.nopcommerce.com`) is a shared community environment without a sandboxed payment backend. Attempting to automate mock orders against a shared public environment leads to:
  * Frequent database resets clearing customer data midway through test runs.
  * Flaky inventory exhaustion on shared product SKUs.
  * Polluting public demo databases with automated test noise.
* **Engineering Decision:** Checkout is thoroughly documented in our Test Strategy and manual test cases ([docs/test-scenarios.md](docs/test-scenarios.md)), but deliberately excluded from automated regression runs. This demonstrates understanding of test environment boundaries and risk trade-offs.

---

## 8. Framework Architecture

The framework is structured into modular layers adhering to clean architectural boundaries:

```
+-----------------------------------------------------------------------------------+
|                                Test Classes                                       |
|     (SmokeTest, SearchTest, CartTest, NavigationTest, AuthenticationNegativeTest) |
+-----------------------------------------------------------------------------------+
                                         │
                                         ▼
+-----------------------------------------------------------------------------------+
|                                 Page Objects                                      |
|            (HomePage, SearchResultsPage, ProductPage, CartPage, LoginPage)        |
+-----------------------------------------------------------------------------------+
                                         │
                                         ▼
+-----------------------------------------------------------------------------------+
|                                  Base Page                                        |
|         (Explicit waits, safe click, type, security challenge handler)            |
+-----------------------------------------------------------------------------------+
                                         │
                                         ▼
+-----------------------------------------------------------------------------------+
|                        Driver Factory & Config Management                         |
|           (ThreadLocal<WebDriver>, ChromeOptions, ConfigReader, TestDataReader)   |
+-----------------------------------------------------------------------------------+
```

### Architectural Highlights:
1. **ThreadLocal Driver Management:** `DriverFactory` wraps `WebDriver` inside a `ThreadLocal` container, ensuring zero state bleeding between threads and making the framework structurally ready for parallel execution when isolated test environments are available.
2. **Configuration Hierarchy:** `ConfigReader` resolves configuration via a 3-tier cascade:
   * System Properties (e.g. CLI flags `-Dbrowser=chrome -Dheadless=true`)
   * Configuration File (`src/test/resources/config/config.properties`)
   * Hardcoded Safe Defaults (`browser=chrome`, `headless=false`, `baseUrl=https://demo.nopcommerce.com/`)
3. **Resilient Browser Profiles:** `DriverFactory` configures Chrome with defensive switches (`--disable-gpu`, `--disable-dev-shm-usage`, `--no-sandbox`) and injects Chrome DevTools Protocol (CDP) scripts to prevent automation flag conflicts with Cloudflare Turnstile.
4. **Decoupled Test Listeners:** `TestListener` implements TestNG's `ITestListener` to capture timestamped PNG screenshots directly to `test-output/screenshots/` upon failure, wrapped in defensive try-catch blocks to prevent listener exceptions from masking underlying test failures.

---

## 9. Project Structure

```
shopsphere-qa-automation/
│
├── pom.xml                                    # Maven dependencies, compiler (Java 17), Surefire plugin
├── testng.xml                                 # Main regression suite configuration (sequential execution)
├── testng-smoke.xml                           # P0 Smoke suite configuration
├── README.md                                  # Framework documentation & interview guide
├── .gitignore                                 # Git ignore rules for target/, logs, IDE files
│
├── .github/
│   └── workflows/
│       └── qa-regression.yml                  # GitHub Actions CI workflow (Headless Chrome + Surefire)
│
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── shopsphere/
│   │               ├── config/
│   │               │   └── ConfigReader.java  # 3-tier configuration manager
│   │               ├── driver/
│   │               │   └── DriverFactory.java # ThreadLocal WebDriver lifecycle & Chrome options
│   │               ├── pages/
│   │               │   ├── BasePage.java      # Reusable explicit waits, JS fallback, alert handling
│   │               │   ├── HomePage.java      # Homepage interactions & navigation
│   │               │   ├── SearchResultsPage.java # Search results verification & item selection
│   │               │   ├── ProductPage.java   # SKU details, quantity, add-to-cart actions
│   │               │   ├── CartPage.java      # Cart table inspection, update quantity, removal
│   │               │   └── LoginPage.java     # Authentication portal actions & validation errors
│   │               └── utils/
│   │                   └── TestDataReader.java # Externalized test data loader
│   │
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── shopsphere/
│       │           ├── listeners/
│       │           │   └── TestListener.java  # Screenshot capture on test failure
│       │           └── tests/
│       │               ├── BaseTest.java      # Test lifecycle harness (@BeforeClass, @AfterClass)
│       │               ├── SmokeTest.java     # P0 health check tests
│       │               ├── SearchTest.java    # Product discovery & search validation
│       │               ├── CartTest.java      # Shopping cart functional scenarios
│       │               ├── NavigationTest.java # Header links & category navigation
│       │               └── AuthenticationNegativeTest.java # Login negative validation tests
│       │
│       └── resources/
│           └── config/
│               ├── config.properties          # Environment & browser configuration
│               └── testdata.properties        # Externalized test parameters & tokens
│
└── docs/
    ├── test-strategy.md                       # Comprehensive QA strategy & risk assessment
    ├── test-scenarios.md                      # Requirement-to-test traceability matrix
    ├── exploratory-testing.md                 # Charter-based exploratory testing notes & heuristics
    ├── defect-template.md                     # Professional defect reporting template
    ├── release-checklist.md                   # 10-point release readiness quality gate
    └── qa-signoff-template.md                 # Formal QA sign-off certificate
```

---

## 10. Why Page Object Model (POM) Is Used

In early automation scripts, test code is frequently intertwined with element locators:
```java
// Anti-pattern: Fragile, unmaintainable test script
@Test
public void testAddToCart() {
    driver.findElement(By.id("small-searchterms")).sendKeys("MacBook");
    driver.findElement(By.cssSelector("button.search-box-button")).click();
    driver.findElement(By.linkText("Apple MacBook Pro")).click();
    driver.findElement(By.id("add-to-cart-button-4")).click();
}
```

If the developers alter `small-searchterms` to `search-input`, every test referencing that ID breaks simultaneously.

### The Page Object Model Alternative in ShopSphere:
* **Encapsulation:** Page classes (`HomePage`, `CartPage`, etc.) encapsulate DOM locators as private `By` variables and expose clean, intent-revealing business methods.
* **Readable Tests:** Test methods read like executable user stories:
```java
@Test(description = "Verify user can add a product to the cart")
public void verifyAddProductToCartSuccessfully() {
    homePage.searchForProduct("Apple MacBook Pro");
    searchResultsPage.clickProductByName("Apple MacBook Pro");
    productPage.addProductToCart();
    Assert.assertTrue(productPage.isSuccessNotificationDisplayed(), 
        "Success notification bar was not displayed after adding product");
    
    productPage.navigateToShoppingCart();
    Assert.assertTrue(cartPage.isProductInCart("Apple MacBook Pro"), 
        "Expected product was not found in the shopping cart");
}
```
* **Single Source of Truth:** A locator change requires updating exactly one line in one Page Object.

---

## 11. Why Explicit Waits Are Used

### The Flaw of `Thread.sleep()`
Hardcoded sleeps halt thread execution blindly. If a network request finishes in 200ms, a `Thread.sleep(5000)` wastes 4.8 seconds. If a Cloudflare handshake takes 5.2 seconds, the test fails anyway.

### The Flaw of Implicit Waits
Implicit waits (`driver.manage().timeouts().implicitlyWait(...)`) apply globally to `findElement` calls, but they only poll for DOM presence. They cannot verify that an element is **clickable**, **visible**, or that a specific CSS animation or AJAX request has resolved. Mixing implicit and explicit waits can also cause unpredictable timeout multiplication according to the Selenium documentation.

### Explicit Waits in ShopSphere:
The framework relies entirely on `WebDriverWait` paired with `ExpectedConditions`:
* `ExpectedConditions.visibilityOfElementLocated(locator)`
* `ExpectedConditions.elementToBeClickable(locator)`
* `ExpectedConditions.invisibilityOfElementLocated(locator)` (used when waiting for the green notification bar to dismiss)
* `ExpectedConditions.alertIsPresent()` (used for browser JavaScript alerts)

Every action in `BasePage` (`click`, `type`, `getText`, `isDisplayed`) guarantees element readiness before interaction.

---

## 12. Test Data Approach

Test inputs are externalized in `src/test/resources/config/testdata.properties`:
```properties
product.valid.name=Apple MacBook Pro
product.partial.name=Build your own
product.invalid.name=XYZ-Product-Does-Not-Exist-999
product.update.quantity=3
auth.invalid.email=nonexistent_user_999@shopsphere-qa.test
auth.invalid.password=InvalidPass!2345
```

Accessed via `TestDataReader`:
```java
String product = TestDataReader.get("product.valid.name");
```

This prevents hardcoding test values across test methods, simplifies internationalization or multi-environment parameterization, and keeps test logic clean.

---

## 13. Failure Evidence, Reporting & Allure Integration

When an automated test runs locally or in CI, rich visual context and metrics are generated across multiple reporting tiers:
* **Interactive Allure HTML Reports:** Integrated via `allure-testng` and `allure-maven` plugin. Features `@Epic`, `@Feature`, `@Story`, `@Severity`, test step timelines, and embedded failure screenshots:
  ```bash
  # Generate and view interactive local Allure report in default browser:
  mvn allure:serve

  # Generate static standalone HTML report:
  mvn allure:report
  ```
* **Automated Failure Screenshot Capture:** `TestListener` intercepts `onTestFailure()`, captures a full-page PNG screenshot, and:
  1. Saves evidence to disk: `test-output/screenshots/[TestName]_[timestamp].png`
  2. Directly attaches the image into the Allure report via `@Attachment`.
* **Maven Surefire XML/HTML Reports:** Standard Surefire XML reports generated in `target/surefire-reports/`.
* **CI Artifact Archival:** The GitHub Actions workflow automatically uploads `allure-results/`, `test-output/screenshots/`, and `target/surefire-reports/` on every execution.

---

## 14. Smoke vs. Regression Execution

| Attribute | Smoke Suite (`testng-smoke.xml`) | Regression Suite (`testng.xml`) |
| :--- | :--- | :--- |
| **Purpose** | High-speed health check of critical journeys | Comprehensive 66-test functional & edge regression |
| **Test Count** | 6 critical tests | 66 tests across 9 suites |
| **Execution Time** | ~15 - 25 seconds | ~4 - 6 minutes |
| **When to Run** | Pre-merge PR checks, post-deployment smoke | Scheduled nightly, pre-release candidate sign-off |
| **Command** | `mvn test -DsuiteFile=testng-smoke.xml` | `mvn clean test` |

---

## 15. Local Execution

### Prerequisites:
* **Java Development Kit (JDK):** Version 17+
* **Apache Maven:** Version 3.8+
* **Google Chrome:** Installed on host machine

### Running Tests:
```bash
# Clone the repository
git clone https://github.com/your-username/shopsphere-qa-automation.git
cd shopsphere-qa-automation

# Execute full 66-test regression suite
mvn clean test

# Generate and launch Allure interactive report
mvn allure:serve

# Execute smoke suite only
mvn test -DsuiteFile=testng-smoke.xml

# Execute specific test class
mvn test -Dtest=RegistrationTest
mvn test -Dtest=CartTest
mvn test -Dtest=SearchTest

# Execute specific test method
mvn test -Dtest=SearchTest#verifySearchWithExistingProductReturnsRelevantResults
```

---

## 16. Headless Execution

Headless execution runs Chrome without rendering a visual GUI, which is essential for CI/CD runners, containerized environments, and headless Linux virtual machines.

```bash
# Execute full suite in headless Chrome
mvn clean test -Dheadless=true

# Execute specific test class in headless mode
mvn test -Dtest=SearchTest -Dheadless=true
```

`DriverFactory` automatically configures headless Chrome with `--headless=new`, a window size of `1920x1080`, and `--disable-gpu` to guarantee identical element rendering to standard desktop execution.

---

## 17. GitHub Actions CI

The automated regression suite is configured as a GitHub Actions workflow (`.github/workflows/qa-regression.yml`):

### Workflow Triggers:
* `push` to `main` branch
* `pull_request` against `main` branch
* `workflow_dispatch` (manual one-click trigger from GitHub Actions dashboard)

### Workflow Steps:
1. Check out code repository (`actions/checkout@v4`).
2. Set up JDK 17 with Maven caching (`actions/setup-java@v4`).
3. Set up headless Google Chrome environment.
4. Execute Maven test suite with `-Dheadless=true`.
5. Archive Surefire test reports (`actions/upload-artifact@v4`).
6. Archive failure screenshots if any tests failed (`if: failure()`).

---

## 18. Quality Gates

In this framework, test execution serves as a deterministic **Quality Gate**:
* If all tests pass (`BUILD SUCCESS`), the GitHub Actions job succeeds, permitting code review and merging.
* If any test fails (`BUILD FAILURE`), the step exits with code `1`, blocking PR merges.
* Full traceability is maintained between test results, defect logging templates ([docs/defect-template.md](docs/defect-template.md)), and the release checklist ([docs/release-checklist.md](docs/release-checklist.md)).

---

## 19. Known Limitations of Public Demo

Working against a public demo store introduces real-world limitations that QA engineers must navigate:
1. **Cloudflare Turnstile Managed Challenges:** The demo intermittently presents Cloudflare interstitials. The framework handles this via Chrome option tuning and automated iframe detection in `BasePage.handleSecurityChallenge()`.
2. **Shared Cart / Stock Counts:** Concurrent anonymous users may add or deplete demo inventory. Tests focus on persistent catalogue items (`Apple MacBook Pro`, `Build your own computer`).
3. **Session Reset Cycles:** The public demo database is periodically refreshed by nopCommerce maintainers, resetting cart sessions and order records. Tests are self-contained and do not assume persistent state across suite runs.

---

## 20. Future Improvements

* **Cross-Browser Matrix:** Expand `DriverFactory` to support Firefox (`geckodriver`) and Edge (`msedgedriver`) via CI matrix strategy.
* **Parallel Execution at Class Level:** Once dedicated staging containers (e.g., Dockerized nopCommerce instances) are integrated, enable parallel class execution in `testng.xml` (`parallel="classes" thread-count="3"`).
* **Dockerized Execution:** Create a `Dockerfile` and `docker-compose.yml` with Selenium Grid or Selenoid for fully isolated local container runs.
* **Allure Reporting Integration:** Integrate Allure TestNG adapter for interactive HTML dashboard reporting with embedded step-by-step screenshots and timeline charts.

---

## 💬 Interview Quick-Reference: Talking Points for Recruiters & Leads

When discussing this framework in a QA engineering interview, highlight:
* **"Why TestNG over JUnit?"** TestNG provides native test grouping (`groups = {"smoke", "regression"}`), comprehensive suite XML orchestration, and listener hooks (`ITestListener`) without requiring third-party extensions.
* **"How did you handle flaky tests?"** Flakiness was prevented by eliminating static sleeps, using explicit waits on AJAX transitions (such as waiting for the green micro-cart banner to disappear before navigating to `/cart`), and employing `ThreadLocal` for clean driver lifecycle isolation.
* **"Why didn't you automate checkout?"** Recognizing when *not* to automate is a key QA maturity indicator. Automating live payments on an uncontrolled public demo generates flaky false alarms and pollutes shared data. We documented checkout thoroughly in manual scenarios while keeping our CI gate 100% deterministic.
* **"How does your framework support CI/CD?"** The framework is completely CLI-configurable (`-Dheadless=true`), runs in GitHub Actions on every pull request, and uploads failure screenshots and Surefire reports as build artifacts for immediate debugging.
