# Exploratory Testing Charter & Notes: ShopSphere E-Commerce

## 1. Overview & Objective
While automated checks verify known expected behaviors and regression baselines, **exploratory testing** is actively utilized to uncover unscripted anomalies, usability frictions, edge-case race conditions, and emergent bugs in dynamic workflows.

This document records the exploratory charters, heuristics, and observation notes conducted across key areas of the nopCommerce demo store.

---

## 2. Testing Charters & Focus Areas

### Charter 1: Product Discovery & Search Heuristics
* **Focus:** Search input elasticity, special characters, partial matches, whitespace handling, and result rendering.
* **Heuristics Applied:** Goldilocks (too short, too long, just right), character boundaries, and query manipulation.
* **Observations:**
  - Standard keyword queries (e.g., "Apple", "computer") resolve quickly with accurate card previews.
  - Very long character strings (>100 characters) do not cause server errors; the UI cleanly collapses and renders the standard no-results banner.
  - Leading and trailing spaces are trimmed cleanly by the application prior to query submission.

### Charter 2: Shopping Cart Concurrency & Dynamic State
* **Focus:** Rapid sequential adds, quantity mutations, dynamic AJAX notifications, and cart persistence across browser tabs.
* **Identified Potential Risk:**
  > **Risk Note:** Repeated or rapid Add to Cart actions may expose duplicate requests, incorrect quantities, stale UI state, or cart synchronisation issues.
* **Observations:**
  - The green notification toast ("The product has been added to your shopping cart") appears via an AJAX overlay. If navigated away before the toast fades or the header counter updates, network synchronization can take up to 1-2 seconds.
  - Adding a product with custom required attributes (e.g. RAM, HDD selection on "Build your own computer") correctly diverts the user to the product details page rather than failing silently from category listings.

### Charter 3: Quantity Field Boundaries & Cart Updates
* **Focus:** Changing quantity to zero, non-numeric characters, extreme high quantities, decimal values.
* **Observations:**
  - Setting quantity to `0` and clicking "Update shopping cart" triggers automatic item removal or validation error.
  - Entering alphabetic characters into the quantity box is prevented either by HTML5 input type constraints (`input type="number"`) or server-side fallback resetting to minimum value `1`.
  - Recalculation of totals (unit price * qty) updates consistently upon explicit update action.

### Charter 4: Multi-Product Cart Combinations & Price Summation
* **Focus:** Mixing varied product categories (apparel with sizing attributes + standard electronics) in a single cart session.
* **Observations:**
  - Cart line items maintain independent options without cross-polluting attribute values.
  - Subtotal accurately computes the aggregated sum of all distinct rows.

### Charter 5: Category Hierarchy & Breadcrumb Navigation
* **Focus:** Navigation through nested submenus (Computers -> Desktops / Notebooks / Software) and breadcrumb back-navigation.
* **Observations:**
  - Breadcrumbs maintain correct hierarchical trails across standard catalog routes.
  - Deep linking directly to subcategories renders appropriate facet filters (price ranges, manufacturers).

### Charter 6: Authentication & Boundary Form Feedback
* **Focus:** Malformed email structures, mismatched passwords, SQL injection probe strings (`' OR '1'='1`).
* **Observations:**
  - Input fields enforce client-side and server-side validation.
  - SQL injection probes in search or login fields do not trigger database error disclosures; input is treated as literal search string or rejected authentication attempt.

---

## 3. Exploratory Testing Summary & Recommendations
1. **Synchronization Awareness:** The floating notification bar on item addition is an asynchronous visual cue. Automation tests should synchronize on the notification banner or use explicit waits for cart count updates rather than naive fixed pauses.
2. **Catalog Reset Resilience:** Because the public demo store periodically refreshes its demo database, exploratory verification confirmed that relying on stable catalog cornerstones ("Apple MacBook Pro 13-inch", "Build your own computer") provides the highest test repeatability.
