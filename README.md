# ShopSphere – E-Commerce Test Automation

ShopSphere is a test automation project I built to practise automating common e-commerce user journeys using Java, Selenium WebDriver, TestNG and Maven.

I used the [nopCommerce demo store](https://demo.nopcommerce.com/) as the application under test. The project focuses on testing product search, product details, shopping cart functionality, registration, login and navigation.

## Tools Used

- Java 17
- Selenium WebDriver
- TestNG
- Maven
- Allure Reports
- Git and GitHub
- GitHub Actions

## What I Tested

The project contains 66 automated test methods across nine test classes.

The main areas covered are:

- Homepage and navigation
- Product search, including invalid and empty searches
- Product details and product information
- Shopping cart, quantity updates and item removal
- Wishlist functionality
- User registration and validation
- Login and password recovery
- Category navigation
- Footer links and customer service pages

I also documented exploratory testing scenarios and prioritised test cases based on their importance and risk.

## Framework Structure

I used the Page Object Model (POM) to separate page elements and actions from test cases. This makes the tests easier to read and maintain.

The framework also includes reusable browser setup, explicit waits, test data stored in properties files, and screenshots when tests fail.

Allure is used to present test execution results and failure evidence in a report.

## Running the Tests

Make sure Java 17 and Maven are installed.

Run the default smoke test suite:

```bash
mvn clean test
```

Run the full regression suite:

```bash
mvn clean test -DsuiteFile=testng.xml
```

Run the tests in headless mode:

```bash
mvn clean test -Dheadless=true
```

Generate and view the Allure report:

```bash
mvn allure:serve
```

## GitHub Actions

GitHub Actions runs the smoke tests when changes are pushed to the repository or a pull request is opened. Test reports and failure screenshots are saved as workflow artifacts.

The workflow acts as a quality gate: if a test fails, the workflow fails.

## Test Environment

For local execution, the framework is configured to use the public nopCommerce demo store.

For CI execution, it uses a lightweight local test server. This avoids problems caused by bot protection or availability issues on the shared public demo.

Because the CI environment uses a mock server, a successful CI run does not necessarily mean that the live nopCommerce website passed the same tests.

## Limitations

The public demo is shared with other users, so its content and behaviour may change.

The current project focuses on UI testing. Payment processing and successful end-to-end order completion are not included in the current scope.

## Future Improvements

- Expand test coverage as needed
- Improve test data and failure analysis
- Add more scenarios based on exploratory testing
- Explore parallel execution when test data and environment isolation are in place
