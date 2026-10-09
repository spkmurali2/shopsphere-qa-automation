# Exploratory Testing Notes: ShopSphere

While automated tests check specific regression assertions, I also used manual exploratory testing to understand the application behavior, find edge cases, and design better automated checks.

This document records what I observed during hands-on exploration of the live [nopCommerce demo store](https://demo.nopcommerce.com/), as well as test ideas to investigate in the future.

---

## 1. Verified Observations (Live Demo Store)

These are behaviors observed directly while manually testing the public nopCommerce demo store:

### Product Search
- Common search terms (like "Apple" or "computer") return matching product cards with prices and images.
- Case-insensitivity works: searching "apple macbook pro" returns the same items as "Apple MacBook Pro".
- Submitting an empty search triggers a browser JavaScript alert: `"Please enter some search keyword"`.
- Searching for non-existent keywords (e.g. `XYZ-Product-Does-Not-Exist`) displays a clear message: `"No products were found that matched your criteria."`.

### Product Details and Cart
- Product details pages display the product name, price, breadcrumb path, and a default quantity of 1.
- When a standard product is added to the cart, a green notification banner appears at the top: `"The product has been added to your shopping cart"`.
- Products with configurable options (such as "Build your own computer") require the customer to select specifications (like RAM and HDD) on the product page before adding the item to the cart.

### Navigation and Header
- Main category links (Computers, Electronics, Apparel, etc.) open category pages with hierarchical breadcrumbs (e.g. `Home / Computers / Desktops`).
- The currency dropdown in the header allows switching between US Dollar and Euro, which updates displayed price symbols across the store.

### Form Validation
- Registration requires First Name, Last Name, Email, and Password. Submitting with empty fields shows inline validation errors for missing fields.
- Submitting an invalid email format (e.g., missing `@` or domain) displays a `"Wrong email"` message.

---

## 2. Test Ideas and Areas to Explore Further

These are additional scenarios and edge cases to test in future rounds:

- **Search whitespace handling:** Testing how leading, trailing, and excessive whitespace within queries are handled.
- **Password rules and confirmation:** Testing password mismatch warnings and minimum length policy enforcement.
- **Cart quantity updates:** Testing boundary values in the cart table (setting quantity to `0`, entering letters, or very large quantities) and verifying how the cart updates or removes items.
- **Cart concurrency:** Opening two browser tabs with the same session and modifying the cart in one tab to see how the other tab updates.
- **Coupon code handling:** Submitting invalid, blank, or expired coupon codes in the cart to check validation messages.
- **Rapid button clicks:** Quickly clicking "Add to cart" multiple times to check if duplicate requests or quantities are added.
- **Session expiry:** Leaving items in the cart until the session expires to verify whether cart contents persist after re-login.
- **Security input testing:** Testing search and login fields with SQL injection probes or script tags (`<script>alert(1)</script>`) to confirm inputs are sanitized safely.
- **Browser navigation:** Using the browser Back and Forward buttons after logging out to check whether sensitive account pages are cached.

---

## 3. Practical Takeaways for Automation

1. **Dynamic explicit waits:** The floating green notification bar on item addition takes a moment to appear and fade. Using explicit waits (`wait.until(...)`) for the notification or cart counter keeps the tests reliable without using fixed sleeps.
2. **Stable catalog items:** Because the public demo store can be reset or edited by other users, sticking to core catalog items ("Apple MacBook Pro 13-inch", "Build your own computer") gives the most repeatable results.
