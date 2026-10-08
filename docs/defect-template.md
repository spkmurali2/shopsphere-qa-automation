# Standard Defect Report Template

> **Usage Note:** This is the standardized defect reporting template used by the QA team to log reproducible issues into Jira / GitHub Issues. Do not submit bug tickets with missing reproduction steps or absent evidence.

---

## Defect Report: [SHORT SUMMARY OF DEFECT]

* **Defect ID:** `DEF-[AREA]-[NUMBER]` *(e.g. DEF-CART-042)*
* **Date Reported:** `YYYY-MM-DD`
* **Reported By:** `[QA Engineer Name]`
* **Component / Area:** `[Cart | Search | Auth | Checkout | Navigation]`
* **Environment:** `[Public Demo / Staging / Chrome 134 / Windows 11]`
* **Severity:** `[Critical (S1) | Major (S2) | Minor (S3) | Trivial (S4)]`
* **Priority:** `[P0 (Blocker) | P1 (High) | P2 (Medium) | P3 (Low)]`
* **Status:** `[New | Triaged | In Progress | Ready for Retest | Closed | Rejected]`

---

### 1. Summary
[A concise, unambiguous one-sentence description of the symptom, specifying what failed under what condition.]

### 2. Preconditions
* Browser session opened to `[URL]`.
* Specific test data or prerequisite state (e.g., cart preloaded with 1 item, user logged out).

### 3. Steps to Reproduce
1. Navigate to `...`
2. Perform action `...`
3. Click on `...`
4. Observe the resulting behavior.

### 4. Expected Result
[Clear statement of how the application should behave according to requirements or UX specification.]

### 5. Actual Result
[Exact observation of the incorrect behavior, error code, visual distortion, or unhandled exception.]

### 6. Reproducibility
* `[Always (100%) | Intermittent (~50%) | Once only]`

### 7. Evidence & Attachments
* **Screenshots / Video:** `[Attach screenshot or link to CI artifact in test-output/screenshots/]`
* **Console Logs / Network Payloads:** `[Attach relevant browser DevTools console errors or failing API responses]`
* **Surefire Stack Trace (if applicable):**
  ```text
  [Insert stack trace here]
  ```

### 8. Workaround (if known)
[Describe temporary workaround available to users, if any. Otherwise state: "No workaround available."]

### 9. Regression Verification Result
* **Retested In Build:** `[Build / Commit Hash]`
* **Retest Date:** `YYYY-MM-DD`
* **Verified By:** `[QA Engineer Name]`
* **Outcome:** `[Pass - Closed | Fail - Reopened with comments]`
