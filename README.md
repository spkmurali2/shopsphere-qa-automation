# ShopSphere – E-Commerce Automation Framework

[![CI Automation & Release Quality Gate](https://github.com/spkmurali2/shopsphere-qa-automation/actions/workflows/qa-regression.yml/badge.svg)](https://github.com/spkmurali2/shopsphere-qa-automation/actions/workflows/qa-regression.yml)
[![Allure Report](https://img.shields.io/badge/Allure%20Report-Integrated-brightgreen.svg)](https://github.com/spkmurali2/shopsphere-qa-automation/actions)
[![Java 17](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Selenium WebDriver](https://img.shields.io/badge/Selenium-4.26.0-green.svg)](https://www.selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-7.10.2-blue.svg)](https://testng.org/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-red.svg)](https://maven.apache.org/)

A test automation framework built using **Java 17**, **Selenium WebDriver 4**, **TestNG**, and **Maven** with **Allure Reports** and **GitHub Actions CI**. 

The framework tests core user journeys and edge cases on an e-commerce platform ([nopCommerce Demo Store](https://demo.nopcommerce.com/)), structured using the **Page Object Model (POM)** design pattern with explicit waits, automated screenshot capture on failure, and continuous integration quality gates.

---

## Tech Stack

| Technology | Purpose |
| :--- | :--- |
| **Java 17** | Core programming language |
| **Selenium WebDriver 4.26.0** | Browser automation |
| **TestNG 7.10.2** | Test runner, assertions, test groupings, and listener hooks |
| **Maven** | Build management and dependency resolution |
| **Allure 2.29.0** | Interactive visual test reporting with step timelines & screenshots |
| **GitHub Actions** | Automated CI pipeline with virtual display (`xvfb`) execution |

---

## Framework Architecture & Highlights

* **Page Object Model (POM):** Page locators and interactions are isolated into dedicated Page classes (`HomePage`, `CartPage`, `SearchResultsPage`, etc.). Test methods only contain user action calls and assertions.
* **Explicit Waits (No Hardcoded Sleeps):** Uses `WebDriverWait` and `ExpectedConditions` to wait dynamically for elements to become visible or clickable. Avoids flaky `Thread.sleep()` delays.
* **Thread-Safe Driver Factory:** Uses `ThreadLocal<WebDriver>` to manage driver sessions safely across threads.
* **Failure Evidence Capture:** Custom TestNG listener (`TestListener`) automatically takes a screenshot on test failure and attaches it directly into the Allure report and local `test-output/screenshots/` folder.
* **Hermetic CI Quality Gate:** Includes a lightweight local mock server fallback so GitHub Actions CI runs deterministically without third-party Cloudflare bot challenges or internet rate limiting.

---

## Test Coverage (66 Automated Scenarios)

The test suite covers **66 end-to-end and boundary test cases** organized across 9 functional modules:

1. **Smoke Tests (`SmokeTest` - 6 tests):** Homepage loading, top category menu availability, global search input, shopping cart link, wishlist badge, currency selector.
2. **Product Search (`SearchTest` - 10 tests):** Exact keyword search, partial keywords, case-insensitivity, leading/trailing whitespace trimming, special characters, non-existent products, 120-character input boundary.
3. **Shopping Cart (`CartTest` - 10 tests):** Adding single/multiple items, duplicate quantity accumulation, updating quantities, zero-quantity removal, discount coupon code validation, empty cart states.
4. **Product Details (`ProductDetailsTest` - 7 tests):** SKU title/price verification, breadcrumb navigation, default quantity, wishlist toast, compare list toast, product reviews, email a friend.
5. **Wishlist Management (`WishlistTest` - 5 tests):** Adding items to wishlist, wishlist item persistence, sharing wishlist URL, transferring wishlist items to cart, empty wishlist state.
6. **User Registration (`RegistrationTest` - 7 tests):** Required field validations, malformed email formatting, short password boundary, mismatched password confirmation, successful dynamic account registration, duplicate email rejection.
7. **Customer Authentication (`AuthenticationTest` - 8 tests):** Valid login, invalid credentials error, empty fields, malformed email, password recovery navigation, empty recovery email validation.
8. **Catalog & Category Navigation (`CategoryNavigationTest` - 7 tests):** Top-level navigation to Computers, Electronics, Apparel, Digital Downloads, Books, Jewelry, and Gift Cards.
9. **Footer & Customer Service (`FooterAndCustomerServiceTest` - 6 tests):** Contact Us form validation, Sitemap link, Shipping & Returns page, Privacy Notice, Conditions of Use, USD $\leftrightarrow$ Euro currency switcher.

---

## Project Structure

```
shopsphere-qa-automation/
├── src/
│   ├── main/java/com/shopsphere/
│   │   ├── config/ConfigReader.java            # Loads config from properties or CLI
│   │   ├── driver/DriverFactory.java           # WebDriver lifecycle & Chrome options
│   │   ├── pages/                              # Page Object Model classes
│   │   │   ├── BasePage.java                   # Shared explicit waits & browser actions
│   │   │   ├── HomePage.java
│   │   │   ├── SearchResultsPage.java
│   │   │   ├── ProductPage.java
│   │   │   ├── CartPage.java
│   │   │   ├── LoginPage.java
│   │   │   ├── RegisterPage.java
│   │   │   ├── WishlistPage.java
│   │   │   ├── ContactUsPage.java
│   │   │   └── PasswordRecoveryPage.java
│   │   └── server/EmbeddedTestServer.java      # Local test server for hermetic CI runs
│   └── test/
│       ├── java/com/shopsphere/
│       │   ├── listeners/TestListener.java     # Automated screenshot capture & Allure hooks
│       │   └── tests/                          # 9 Test classes (66 test cases)
│       │       ├── BaseTest.java
│       │       ├── SmokeTest.java
│       │       ├── SearchTest.java
│       │       ├── CartTest.java
│       │       ├── ProductDetailsTest.java
│       │       ├── WishlistTest.java
│       │       ├── RegistrationTest.java
│       │       ├── AuthenticationTest.java
│       │       ├── CategoryNavigationTest.java
│       │       └── FooterAndCustomerServiceTest.java
│       └── resources/config/
│           ├── config.properties               # Browser & wait timeout configurations
│           └── testdata.properties             # Externalized test keywords & data
├── .github/workflows/
│   └── qa-regression.yml                       # CI pipeline running on GitHub Actions
├── testng-smoke.xml                            # Smoke suite runner (6 critical tests)
├── testng.xml                                  # Full regression suite runner (66 tests)
└── pom.xml                                     # Maven dependencies and Surefire/Allure plugins
```

---

## How to Run Tests

### In Eclipse or IntelliJ:
* **Run Smoke Suite:** Right-click `testng-smoke.xml` $\rightarrow$ **Run As** $\rightarrow$ **TestNG Suite**.
* **Run Full Suite:** Right-click `testng.xml` $\rightarrow$ **Run As** $\rightarrow$ **TestNG Suite**.
* **Run Individual Test Class:** Right-click any test file (e.g. `SmokeTest.java` or `CartTest.java`) $\rightarrow$ **Run As** $\rightarrow$ **TestNG Test**.

### In Terminal / Command Line:
```bash
# Run default smoke suite (takes ~15-20 seconds)
mvn test

# Run full 66-test regression suite
mvn test -DsuiteFile=testng.xml

# Run in headless mode (no browser window opens)
mvn test -Dheadless=true

# Run a specific test class
mvn test -Dtest=CartTest
mvn test -Dtest=SearchTest
```

---

## Where Are the Allure Reports Stored?

Allure produces two things: **raw results** and **compiled HTML reports**:

### 1. Raw Execution Data (`allure-results/`):
* Generated automatically whenever tests run.
* **Storage Location:** Located directly in your project root at:
  ```
  shopsphere-qa-automation/allure-results/
  ```
* Contains JSON test execution metrics, step logs, and embedded screenshot files.

### 2. Compiled Interactive HTML Report:
* **Option A — Instant Browser View (Recommended):**
  Run this command in your project terminal:
  ```bash
  mvn allure:serve
  ```
  Allure compiles the report and immediately opens the interactive dashboard in your default browser (at `http://localhost:...`).

* **Option B — Generate Static HTML Folder:**
  Run this command:
  ```bash
  mvn allure:report
  ```
  The compiled standalone HTML report is saved in:
  ```
  shopsphere-qa-automation/target/site/allure-maven-plugin/index.html
  ```
  You can double-click `index.html` or open it in any browser to view detailed execution charts, `@Severity` breakdowns, and failure screenshots.

### 3. In GitHub Actions (CI):
* After every CI run, the Allure HTML report is automatically packaged and archived.
* Go to the **Actions** tab on GitHub $\rightarrow$ Click the latest workflow run $\rightarrow$ Scroll down to **Artifacts** $\rightarrow$ Download **`allure-html-report`**.

---

## CI/CD Pipeline (GitHub Actions)

On every Git `push` or `pull_request` to `main`:
1. GitHub Actions starts an `ubuntu-latest` runner with JDK 17 and Google Chrome.
2. A virtual display (`xvfb-run`) is initialized so Chrome runs smoothly without display or window-manager issues.
3. The smoke quality gate suite executes in under **1 minute**.
4. The Allure report and Surefire test summaries are generated and saved as artifacts.
