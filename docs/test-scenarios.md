# Test Scenarios Specification: ShopSphere E-Commerce

This document catalogs core test scenarios across critical user journeys on the ShopSphere application (nopCommerce demo). Each scenario is mapped to test type, risk priority, expected outcome, and automation coverage status.

## Scenario Matrix

| ID | Area | Scenario | Type | Priority | Expected Result | Automation Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **SS-SMK-001** | Home / Core | Verify homepage loads successfully with expected title and branding | Smoke | P0 Critical | Homepage loads within threshold; page title contains "nopCommerce demo store"; header logo is visible. | Automated |
| **SS-SMK-002** | Home / Nav | Verify global navigation menu is rendered with core categories | Smoke | P0 Critical | Top menu bar displays primary category links (Computers, Electronics, Apparel, etc.). | Automated |
| **SS-SMK-003** | Home / Header | Verify search bar and header action links are present | Smoke | P0 Critical | Search input box, Search button, Shopping Cart link, and Login link are visible and interactable. | Automated |
| **SS-SRCH-001** | Search | Search for an existing exact product keyword ("Apple MacBook Pro") | Functional | P0 Critical | Search results page renders matching product card containing "Apple MacBook Pro 13-inch" with price and details button. | Automated |
| **SS-SRCH-002** | Search | Search using partial product name ("Build your own") | Functional | P1 High | Search results page displays products matching the partial token (e.g. "Build your own computer"). | Automated |
| **SS-SRCH-003** | Search | Search for a non-existent product SKU ("XYZ-Product-Does-Not-Exist") | Negative | P1 High | Search returns zero product cards and displays the message "No products were found that matched your criteria." | Automated |
| **SS-SRCH-004** | Search | Navigate to product detail page directly from search results | Functional | P1 High | Clicking product title card navigates to the dedicated Product Details Page with URL matching product slug. | Automated |
| **SS-SRCH-005** | Search | Empty keyword search submission | Negative | P2 Medium | System prompts user with alert warning or stays on search page with validation message. | Automated |
| **SS-CART-001** | Cart | Add standard catalog product to cart from Product Details Page | Functional | P0 Critical | Notification bar confirms "The product has been added to your shopping cart"; header cart badge counter increments. | Automated |
| **SS-CART-002** | Cart | Verify item details in Shopping Cart page | Functional | P0 Critical | Shopping Cart page lists correct product name, unit price, quantity `1`, and accurate subtotal. | Automated |
| **SS-CART-003** | Cart | Add the same product a second time to verify quantity accumulation | Functional | P1 High | Product line item quantity in cart increments to `2` with subtotal reflecting `2 * unit price`. | Automated |
| **SS-CART-004** | Cart | Update product quantity directly on Shopping Cart page | Functional | P1 High | Updating quantity input field and clicking "Update shopping cart" recalculates line total and cart order subtotal. | Automated |
| **SS-CART-005** | Cart | Remove a product from Shopping Cart | Functional | P1 High | Clicking remove icon removes line item; when empty, page displays "Your Shopping Cart is empty!". | Automated |
| **SS-CART-006** | Cart | Add multiple distinct products and verify consolidated cart | Functional | P1 High | All distinct items appear as separate rows; summary subtotal reflects sum of all row totals. | Automated |
| **SS-CART-007** | Cart | Add product with zero/negative quantity value | Boundary / Negative | P2 Medium | Validation error displayed or minimum quantity clamped to `1`; invalid quantity cannot be added. | Automated |
| **SS-NAV-001** | Navigation | Verify navigation to Customer Login page | Functional | P1 High | Clicking header "Log in" navigates to `/login` with login form, "Returning Customer" header, and inputs. | Automated |
| **SS-NAV-002** | Navigation | Verify navigation to Customer Registration page | Functional | P2 Medium | Clicking "Register" navigates to `/register` with registration fields (gender, names, email, password). | Automated |
| **SS-NAV-003** | Navigation | Verify header Shopping Cart flyout/page navigation | Functional | P1 High | Clicking "Shopping cart" header link navigates to `/cart`. | Automated |
| **SS-NAV-004** | Navigation | Verify primary category navigation (e.g. Computers) | Functional | P2 Medium | Clicking category link navigates to Category page with relevant subcategories and breadcrumb trail. | Automated |
| **SS-AUTH-001** | Auth / Negative | Submit login with unregistered email and invalid password | Negative | P1 High | Login rejected; error message displayed stating "Login was unsuccessful. Please correct the errors and try again." | Automated |
| **SS-AUTH-002** | Auth / Negative | Submit login with blank email and password | Negative | P2 Medium | Client-side validation displays "Please enter your email" beneath the email field. | Automated |
| **SS-CHK-001** | Checkout | Guest checkout journey through shipping/billing/payment | End-to-End | P0 Critical | Step-by-step checkout billing, shipping method, and fake test payment confirmation. | *Out of Scope (Shared Demo Env)* |

---

## Priority Definitions
* **P0 - Critical:** Core conversion blocking issues. Automated in Smoke suite. Zero tolerance for regression.
* **P1 - High:** Essential e-commerce features (quantity adjustments, search edge cases, validation). Automated in Regression suite.
* **P2 - Medium:** Secondary workflows, edge validation, and auxiliary navigation. Automated where high value.
* **P3 - Low:** Minor cosmetic or auxiliary enhancements. Validated via exploratory testing.
