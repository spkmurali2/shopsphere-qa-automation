# Test Strategy: ShopSphere E-Commerce QA Automation

## 1. Document Overview
* **Project Name:** ShopSphere – E-commerce QA Automation Framework
* **Target Application:** nopCommerce Public Demo Store (`https://demo.nopcommerce.com/`)
* **Author:** QA Automation Team
* **Target Audience:** Engineering Leads, QA Engineers, Release Managers, Product Owners

---

## 2. Objective & Mission
The objective of this test strategy is to define a structured, risk-based quality assurance approach for the ShopSphere e-commerce web platform. This document outlines the testing levels, scope, test environment considerations, automation principles, defect workflows, and quality gates required to deliver high confidence in release readiness.

---

## 3. Scope of Testing

### 3.1 In-Scope (Automated & Manual)
* **Homepage & Core Discovery:** Site availability, global navigation, category menus, currency selectors, and header utilities.
* **Product Search & Filtering:** Exact keyword match, partial keyword match, case-insensitivity, no-results messaging, and search navigation.
* **Product Details & Selection:** Product specifications display, quantity configuration, dynamic pricing display, and stock-status validation.
* **Shopping Cart Operations:** Add to cart from listing/detail, quantity increments/decrements, item removal, empty cart state, subtotal/total calculations, and cart counter synchronization.
* **User Authentication & Navigation:** Navigation to login/registration portals, client-side input validation, and invalid login credential feedback.
* **Negative & Boundary Scenarios:** Search queries with special characters/non-existent SKUs, zero or negative quantities, and unauthenticated protected action handling.

### 3.2 Out of Scope (Intentionally Excluded)
* **Payment Gateway & Order Placement:** End-to-end payment submission and final order placement are excluded from automated test suites because the public shared demo environment (`demo.nopcommerce.com`) lacks sandboxed credit card validation, resets periodically, and shares cart/inventory states across public users.
* **Third-Party Integrations:** External tracking beacons, social media login OAuth providers, and newsletter third-party sync.
* **Performance / Load Testing:** Stress and spike testing are deferred to a dedicated performance test stage using specialized tooling (e.g., JMeter, k6) on an isolated staging cluster.
* **Security & Penetration Testing:** Automated DAST/SAST penetration scanning is handled independently in the security pipeline.

---

## 4. Test Levels & Methodology

```
+-------------------------------------------------------+
|                 Exploratory Testing                   |
|        (Ad-hoc, edge cases, UX friction, races)       |
+-------------------------------------------------------+
|             Automated Regression Suite                |
|      (Scheduled nightly / PR gates, full coverage)    |
+-------------------------------------------------------+
|                Automated Smoke Suite                  |
|          (P0 Critical paths, pre-merge gates)         |
+-------------------------------------------------------+
```

### 4.1 Smoke Testing (P0 Critical)
* **Purpose:** Rapid health check of core revenue-generating user journeys.
* **Cadence:** Executed on every pull request, deployment to staging, or pipeline trigger.
* **Target Execution Time:** Under 2 minutes.
* **Pass Criteria:** 100% pass rate. Any smoke failure immediately blocks the release pipeline.

### 4.2 Functional & Regression Testing (P1/P2)
* **Purpose:** Verify that recent modifications, bug fixes, or dependency updates have not degraded existing catalog or checkout features.
* **Cadence:** Executed nightly and prior to release candidates.
* **Scope:** Search accuracy, shopping cart manipulations, input validation, and multi-step user workflows.

### 4.3 Negative Testing
* **Purpose:** Ensure graceful error handling, informative user feedback, and defensive system behavior when presented with unexpected or malicious inputs.
* **Examples:** Searching for gibberish strings, navigating with manipulated parameters, submitting malformed login credentials.

### 4.4 Exploratory Testing
* **Purpose:** Uncover unscripted anomalies, edge-case interaction races, visual alignment bugs, and subtle usability frictions that scripted checks cannot anticipate.
* **Method:** Time-boxed charter sessions focusing on volatile e-commerce behaviors (e.g., rapid button tapping, session timeout mid-cart modification).

---

## 5. Risk-Based Testing Prioritization
Resources and execution windows are finite. Testing efforts are prioritized using a Failure Mode and Effects Analysis (FMEA) approach assessing **Impact** (business revenue/customer trust) and **Likelihood** (code volatility/complexity).

| Priority Tier | Risk Profile | Functional Areas | Test Approach |
| :--- | :--- | :--- | :--- |
| **P0 - Critical** | Severe revenue loss; complete user journey block | Catalog load, Search discovery, Add to Cart, Cart Persistence | Fully automated smoke tests; zero-tolerance quality gate. |
| **P1 - High** | Significant friction in customer conversion | Quantity changes, Item removal, Login validation, Search edge cases | Automated regression test coverage + exploratory testing. |
| **P2 - Medium** | Degraded secondary feature; workaround exists | Category navigation, sorting options, footer links | Automated regression test coverage where cost-effective. |
| **P3 - Low** | Minor cosmetic defect; negligible business impact | Static text formatting, minor alignment, tooltip styling | Targeted manual/exploratory verification. |

### Why Critical Customer Journeys Receive Higher Priority
In an e-commerce platform, the user journey flows sequentially through a conversion funnel:
`Homepage -> Search / Discovery -> Product Detail -> Add to Cart -> Cart Review -> Checkout`.
A break at the discovery or cart stage halts 100% of downstream revenue transactions. Therefore, automation assets are concentrated heavily on the discovery-to-cart funnel to provide immediate shift-left regression detection.

---

## 6. Automation Architecture & Design Principles

* **Page Object Model (POM):** Strict separation between UI locators/actions (encapsulated in page classes) and test assertions (maintained in test classes).
* **Robust Explicit Synchronization:** Zero dependency on arbitrary `Thread.sleep()`. All element states are governed by dynamic `WebDriverWait` polling for visibility, clickability, and staleness.
* **Stateless Test Isolation:** Every test method initializes and tears down its own browser instance via a clean `DriverFactory`, preventing cross-test pollution.
* **ThreadLocal Driver Management:** ThreadLocal storage ensures thread-safe browser sessions, allowing seamless parallelization when dedicated environments become available.
* **Environment Agnostic Configuration:** Browser type, base URL, and headless flags are driven externally by configuration files and Maven CLI overrides (`-Dheadless=true`).
* **Automated Evidence Collection:** Custom TestNG listeners capture screenshots on test failure and log detailed execution context.

---

## 7. Test Environment & Data Strategy

### 7.1 Environment
* **Target:** Public nopCommerce demo store (`https://demo.nopcommerce.com/`).
* **Characteristics:** Public multi-tenant environment subject to shared caching, automated content resets, and Cloudflare perimeter protection.
* **Handling Strategy:** Robust selectors avoiding volatile session-specific attributes; defensive explicit waits capable of absorbing network latency fluctuations.

### 7.2 Test Data Management
* Test datasets (product names, search terms, quantities) are externalized in `src/test/resources/config/testdata.properties`.
* Static test data relies on standard catalog items guaranteed to persist in baseline catalog templates (e.g., "Apple MacBook Pro", "Build your own computer").
* Dynamic data (e.g., randomized search terms or email prefixes) is generated programmatically to avoid collisions.

---

## 8. Defect Management Workflow

When a failure is detected:
1. **Triage & Reproducibility:** Verify whether the failure is a product bug, automation issue (flaky locator), or environment outage.
2. **Evidence Collection:** TestNG automatically stores timestamped full-page screenshots in `test-output/screenshots/`.
3. **Defect Logging:** File Jira/GitHub issue using the standard template in [defect-template.md](file:///c:/Users/Priya%20Murali/Downloads/TestAutomation/docs/defect-template.md).
4. **Lifecycle:** `New -> Triaged -> In Progress -> Fixed -> Retested -> Closed`.

---

## 9. Entry & Exit Criteria

### 9.1 Entry Criteria for Testing
* Test environment is reachable and returns HTTP 200 on base URL.
* Stable build deployed with no blocking environmental degradation.
* Test suite configuration files and test data properties are verified.

### 9.2 Exit Criteria for Release Candidate
* **Smoke Tests:** 100% passing on the target build.
* **Automated Regression Suite:** 100% pass rate on P0/P1 scenarios (or known test failures formally triaged and approved with workarounds).
* **Defect Metrics:** 0 open Critical (P0) or High (P1) defects.
* **Evidence:** Complete Surefire test reports and failure artifacts archived in CI.
* **QA Sign-off:** Completed and reviewed sign-off document.

---

## 10. Release Decision Approach
Release readiness is not determined by gut feeling or raw test counts; it is governed by verifiable quality gates:
1. Continuous Integration: Headless GitHub Actions workflow execution completed cleanly.
2. Risk Assessment: Any accepted edge-case defects documented with mitigation paths.
3. Sign-off: Formal consensus between QA, Development, and Product leads based on the [release-checklist.md](file:///c:/Users/Priya%20Murali/Downloads/TestAutomation/docs/release-checklist.md).
