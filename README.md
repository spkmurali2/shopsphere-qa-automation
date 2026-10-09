# ShopSphere – E-Commerce Test Automation

ShopSphere is a test automation project I built to practice automating core e-commerce user journeys using Java, Selenium WebDriver, TestNG and Maven.

I used the public [nopCommerce demo store](https://demo.nopcommerce.com/) as the target application. To keep tests reliable in CI and when working offline, I also included a lightweight embedded mock server that simulates the key pages and actions.

## Tools Used

- Java 17
- Selenium WebDriver (4.26.0)
- TestNG (7.10.2)
- Apache Maven
- Allure Reports (2.29.0)
- Git and GitHub
- GitHub Actions

## What I Tested

The test suite covers 66 automated tests across nine test classes:

- Homepage and header navigation (smoke checks, logo, currency selector, cart and wishlist links)
- Product search (exact match, partial match, case sensitivity, empty search alert, special characters, no-results messaging)
- Product details (title, price, breadcrumbs, default quantity, review link, wishlist button)
- Shopping cart (adding products, quantity updates, item removal, empty cart state, coupon code validation)
- Wishlist (adding items, shareable URL, item removal, transferring items to cart)
- User registration (field presence, required field errors, invalid email format, password mismatch, successful registration)
- Authentication and password recovery (valid and invalid logins, empty input validation, recovery form validation)
- Category navigation (Computers, Electronics, Apparel, Digital Downloads, Books, Jewelry, Gift Cards)
- Footer and customer service (Contact Us form validation, privacy notice, conditions of use, currency switcher)

I also documented test scenarios and manual exploratory notes in the `docs/` folder.

## Project Structure

```text
shopsphere-qa-automation/
├── .github/
│   └── workflows/
│       └── qa-regression.yml       # GitHub Actions workflow
├── docs/
│   ├── defect-template.md          # Bug report template
│   ├── exploratory-testing.md      # Manual exploratory testing notes
│   ├── release-checklist.md        # Pre-release checklist
│   ├── test-scenarios.md           # 66 test scenarios catalog
│   └── test-strategy.md            # Test strategy and technical approach
├── src/
│   ├── main/java/com/shopsphere/
│   │   ├── config/ConfigReader.java
│   │   ├── driver/DriverFactory.java
│   │   ├── pages/                  # Page Object classes (BasePage, HomePage, CartPage, etc.)
│   │   ├── server/EmbeddedTestServer.java # Embedded mock server for offline/CI runs
│   │   └── utils/TestDataReader.java
│   └── test/
│       ├── java/com/shopsphere/
│       │   ├── listeners/TestListener.java # Screenshot capture and Allure attachments
│       │   └── tests/              # TestNG test classes (SmokeTest, CartTest, etc.)
│       └── resources/config/       # config.properties and testdata.properties
├── pom.xml                         # Maven dependencies and build configuration
├── testng.xml                      # Full regression suite (66 tests)
└── testng-smoke.xml                # Smoke test suite (6 tests)
```

## How to Run the Tests

### Prerequisites

- Java 17 installed and configured (`java -version`)
- Maven 3.9+ installed and configured (`mvn -version`)
- Google Chrome browser installed

### Running Tests in Windows PowerShell

Run the default smoke test suite (6 tests against the live demo):

```powershell
mvn clean test
```

Run the smoke suite using the local embedded mock server:

```powershell
mvn clean test "-DmockServer=true"
```

Run the full regression suite (66 tests against the live demo):

```powershell
mvn clean test "-DsuiteFile=testng.xml"
```

Run the full regression suite against the local mock server:

```powershell
mvn clean test "-DsuiteFile=testng.xml" "-DmockServer=true"
```

Run tests in headless mode (no browser window):

```powershell
mvn clean test "-Dheadless=true"
```

Combined example (full regression, mock server, headless):

```powershell
mvn clean test "-DsuiteFile=testng.xml" "-DmockServer=true" "-Dheadless=true"
```

### Viewing Test Reports

Surefire generates HTML and text reports after execution:
- HTML overview: `target/surefire-reports/index.html`
- Emailable report: `target/surefire-reports/emailable-report.html`

Allure provides detailed execution dashboards:
- Serve and view interactively in your browser:
  ```powershell
  mvn allure:serve
  ```
- Generate a standalone HTML report under `target/site/allure-maven-plugin/`:
  ```powershell
  mvn allure:report
  ```

Failure screenshots are automatically saved to `test-output/screenshots/` and attached directly to the Allure report.

## GitHub Actions

The repository includes a GitHub Actions workflow (`.github/workflows/qa-regression.yml`) that runs on every push and pull request to `main`.

- It runs the smoke test suite in an Ubuntu container with Chrome and Xvfb.
- It uses the embedded mock server so the build is fast, deterministic, and not affected by external network drops or bot protection on the demo store.
- Test reports, Allure results, and any failure screenshots are uploaded as workflow artifacts.
- The full 66-test regression suite can also be triggered manually using GitHub Actions' `workflow_dispatch` option.

## Limitations and What I Want to Improve Next

### Limitations

- **Live Demo Changes:** The public nopCommerce demo is shared with other users, so its products, cart state, or availability can change unexpectedly.
- **Mock Server Scope:** The embedded mock server simulates the HTML routes needed for these tests, but a passing run on the mock server tests framework logic, not live site health.
- **Payment Processing:** End-to-end checkout and payment processing are not automated because the public demo does not offer a sandboxed payment gateway.

### What I Want to Improve Next

- Explore parallel test execution once test data isolation is expanded.
- Add more negative and edge-case scenarios from the exploratory testing notes.
- Add cross-browser runs in CI (Firefox and Edge).
- Improve test reporting dashboards with historical trend tracking.
