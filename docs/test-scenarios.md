# Test Scenarios Specification: ShopSphere E-Commerce

This document catalogs 66 test scenarios covering functional journeys, edge cases, input boundaries, and failure guardrails across the ShopSphere platform (nopCommerce demo store). Each scenario is mapped to an ID, test type, priority, and automation status.

## Scenario Matrix (66 Automated Scenarios)

| ID | Area | Scenario | Type | Priority | Expected Result | Automation Status |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **SS-SMK-001** | Home / Core | Verify homepage loads successfully with expected title and branding | Smoke | P0 Critical | Homepage loads within threshold; page title contains "nopCommerce"; header logo is visible. | Automated |
| **SS-SMK-002** | Home / Nav | Verify global navigation menu is rendered with core categories | Smoke | P0 Critical | Top menu bar displays primary category links (Computers, Electronics, etc.). | Automated |
| **SS-SMK-003** | Home / Header | Verify search bar and search button availability | Smoke | P0 Critical | Search input box and search button are visible and interactive. | Automated |
| **SS-SMK-004** | Home / Header | Verify header shopping cart navigation | Smoke | P0 Critical | Clicking cart link navigates to `/cart`. | Automated |
| **SS-SMK-005** | Home / Header | Verify header wishlist link availability | Smoke | P0 Critical | Wishlist link displayed and counter reflects non-negative count. | Automated |
| **SS-SMK-006** | Home / Localization| Verify customer currency dropdown selector availability | Smoke | P0 Critical | Currency dropdown rendered with US Dollar and Euro options. | Automated |
| **SS-SRCH-001** | Search | Exact product keyword search ("Apple MacBook Pro") | Functional | P0 Critical | Renders matching product card with price and details button. | Automated |
| **SS-SRCH-002** | Search | Partial product name search ("Build your own") | Functional | P1 High | Search results display products matching the partial token. | Automated |
| **SS-SRCH-003** | Search | Non-existent SKU search ("XYZ-Product-Does-Not-Exist") | Negative | P1 High | Renders "No products were found that matched your criteria." banner. | Automated |
| **SS-SRCH-004** | Search | Navigate to product detail from search result card | Functional | P1 High | Clicking product title card navigates to Product Details Page. | Automated |
| **SS-SRCH-005** | Search | Empty keyword search submission | Negative | P2 Medium | System prompts user via native JavaScript alert dialog. | Automated |
| **SS-SRCH-006** | Search | Case-insensitive keyword search ("apple macbook pro") | Functional | P2 Medium | Lowercase search matches mixed-case catalog items. | Automated |
| **SS-SRCH-007** | Search | Search with leading/trailing whitespaces | Edge Case | P2 Medium | Whitespaces trimmed; matching products returned. | Automated |
| **SS-SRCH-008** | Search | Search with special characters ("!@#$%^&*()") | Negative | P2 Medium | Safe handling without 500 error; renders no-results banner. | Automated |
| **SS-SRCH-009** | Search | Search by brand keyword ("Apple") | Functional | P1 High | Brand search yields matching brand catalogue items. | Automated |
| **SS-SRCH-010** | Search | Boundary search query with 120+ characters | Boundary | P3 Low | Extremely long input string handled safely without buffer error. | Automated |
| **SS-CART-001** | Cart | Add standard catalog product to cart from PDP | Functional | P0 Critical | Confirmation banner displayed; cart counter increments. | Automated |
| **SS-CART-002** | Cart | Add duplicate product accumulates quantity | Functional | P1 High | Line item quantity increments ($\ge 2$) rather than duplicating rows. | Automated |
| **SS-CART-003** | Cart | Update item quantity in cart table | Functional | P1 High | Updating quantity input and clicking update recalculates values. | Automated |
| **SS-CART-004** | Cart | Remove product from cart renders empty state | Functional | P1 High | Removing line item renders "Your Shopping Cart is empty!". | Automated |
| **SS-CART-005** | Cart | Setting quantity to 0 removes line item | Boundary | P1 High | Setting quantity to 0 removes the item upon cart update. | Automated |
| **SS-CART-006** | Cart | Add multiple distinct products | Functional | P1 High | Multi-item cart displays separate rows for each SKU. | Automated |
| **SS-CART-007** | Cart | Continue shopping button navigation | Functional | P2 Medium | Clicking "Continue shopping" navigates back to catalog view. | Automated |
| **SS-CART-008** | Cart | Empty cart state disables checkout | Negative | P2 Medium | Checkout button is hidden/inaccessible when cart is empty. | Automated |
| **SS-CART-009** | Cart | Empty discount coupon code submission | Edge Case | P3 Low | Submitting empty coupon code maintains cart state safely. | Automated |
| **SS-CART-010** | Cart | Invalid discount coupon code error validation | Negative | P2 Medium | Submitting invalid coupon code renders failure message. | Automated |
| **SS-PDP-001** | PDP | Verify product title and price display | Functional | P0 Critical | Product title heading and price tag are rendered. | Automated |
| **SS-PDP-002** | PDP | Verify breadcrumb hierarchy trail | Functional | P1 High | Breadcrumb reflects Home > Category > Product hierarchy. | Automated |
| **SS-PDP-003** | PDP | Verify default quantity input value is 1 | Boundary | P1 High | Quantity field defaults to integer 1. | Automated |
| **SS-PDP-004** | PDP | Add product to wishlist from PDP | Functional | P1 High | Clicking "Add to wishlist" displays confirmation banner. | Automated |
| **SS-PDP-005** | PDP | Add product to comparison list from PDP | Functional | P2 Medium | Clicking "Add to compare list" displays confirmation banner. | Automated |
| **SS-PDP-006** | PDP | Verify product reviews link accessibility | Functional | P2 Medium | Product reviews link is visible and interactable. | Automated |
| **SS-PDP-007** | PDP | Verify email a friend referral button presence | Functional | P3 Low | "Email a friend" button is displayed on product page. | Automated |
| **SS-WSH-001** | Wishlist | Add item to wishlist and verify presence | Functional | P1 High | Added SKU appears in wishlist table rows. | Automated |
| **SS-WSH-002** | Wishlist | Verify wishlist sharable URL format | Functional | P2 Medium | Wishlist generates shareable permalink containing `/wishlist/`. | Automated |
| **SS-WSH-003** | Wishlist | Remove item from wishlist | Functional | P1 High | Removing item renders "The wishlist is empty!" state. | Automated |
| **SS-WSH-004** | Wishlist | Transfer item from wishlist into shopping cart | Functional | P1 High | Selecting "Add to cart" in wishlist moves item to `/cart`. | Automated |
| **SS-WSH-005** | Wishlist | Empty wishlist notification state | Negative | P2 Medium | Empty wishlist displays standard placeholder banner. | Automated |
| **SS-REG-001** | Register | Verify registration form input elements | Functional | P0 Critical | First Name, Last Name, Email, Password fields present. | Automated |
| **SS-REG-002** | Register | Submit registration with blank inputs | Negative | P1 High | Required field validation errors displayed for missing inputs. | Automated |
| **SS-REG-003** | Register | Submit registration with invalid email format | Negative | P1 High | Enters malformed email; verifies "Wrong email" message. | Automated |
| **SS-REG-004** | Register | Submit registration with mismatched passwords | Negative | P1 High | Verifies "The password and confirmation password do not match." | Automated |
| **SS-REG-005** | Register | Submit registration with short password (<6 chars) | Negative | P2 Medium | Verifies password complexity/length policy warning. | Automated |
| **SS-REG-006** | Register | Successful customer registration with unique email | Functional | P0 Critical | Generates unique email; verifies "Your registration completed". | Automated |
| **SS-REG-007** | Register | Registration fails with existing customer email | Negative | P1 High | Submits duplicate email; verifies conflict message. | Automated |
| **SS-AUTH-001** | Auth | Login fails with unregistered credentials | Negative | P0 Critical | Summary error displays "No customer account found". | Automated |
| **SS-AUTH-002** | Auth | Login fails with blank credentials | Negative | P1 High | Inline field validation displays "Please enter your email". | Automated |
| **SS-AUTH-003** | Auth | Login fails with malformed email format | Negative | P1 High | Inline validation displays "Wrong email". | Automated |
| **SS-AUTH-004** | Auth | Returning customer login section displayed | Functional | P1 High | Returning customer section and submit button visible. | Automated |
| **SS-AUTH-005** | Auth | "Forgot password?" link navigation | Functional | P1 High | Navigates to `/passwordrecovery` page. | Automated |
| **SS-AUTH-006** | Auth | Password recovery with empty email | Negative | P2 Medium | Validates email required prompt on password recovery. | Automated |
| **SS-AUTH-007** | Auth | Password recovery with malformed email | Negative | P2 Medium | Validates "Wrong email" on password recovery. | Automated |
| **SS-AUTH-008** | Auth | Password recovery with unregistered email | Negative | P2 Medium | Displays recovery submission confirmation/feedback. | Automated |
| **SS-NAV-001** | Navigation | Category navigation to Computers | Functional | P1 High | Navigates to `/computers` with correct header. | Automated |
| **SS-NAV-002** | Navigation | Category navigation to Electronics | Functional | P1 High | Navigates to `/electronics` with correct header. | Automated |
| **SS-NAV-003** | Navigation | Category navigation to Apparel | Functional | P1 High | Navigates to `/apparel` with correct header. | Automated |
| **SS-NAV-004** | Navigation | Category navigation to Digital Downloads | Functional | P1 High | Navigates to `/digital-downloads` with correct header. | Automated |
| **SS-NAV-005** | Navigation | Category navigation to Books | Functional | P1 High | Navigates to `/books` with correct header. | Automated |
| **SS-NAV-006** | Navigation | Category navigation to Jewelry | Functional | P1 High | Navigates to `/jewelry` with correct header. | Automated |
| **SS-NAV-007** | Navigation | Category navigation to Gift Cards | Functional | P1 High | Navigates to `/gift-cards` with correct header. | Automated |
| **SS-FOT-001** | Footer | Contact Us form blank submission | Negative | P2 Medium | Validates Full Name, Email, and Enquiry required errors. | Automated |
| **SS-FOT-002** | Footer | Footer Sitemap navigation | Functional | P2 Medium | Navigates to `/sitemap`. | Automated |
| **SS-FOT-003** | Footer | Footer Shipping & Returns navigation | Functional | P2 Medium | Navigates to `/shipping-returns`. | Automated |
| **SS-FOT-004** | Footer | Footer Privacy Notice navigation | Functional | P2 Medium | Navigates to `/privacy-notice`. | Automated |
| **SS-FOT-005** | Footer | Footer Conditions of Use navigation | Functional | P2 Medium | Navigates to `/conditions-of-use`. | Automated |
| **SS-FOT-006** | Localization| Currency switcher toggles product prices | Functional | P1 High | Switching between US Dollar and Euro updates currency symbols. | Automated |
| **SS-CHK-001** | Checkout | End-to-end payment submission & order placement | End-to-End | P0 Critical | Full payment checkout flow. | *Documented Manual Scope (Shared Public Demo)* |
