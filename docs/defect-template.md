# Defect Report Template

This is the template I use to log reproducible bugs found during testing.

## Defect Summary: [Short description of the bug]

- **Defect ID:** `DEF-[AREA]-[NUMBER]` (e.g. `DEF-CART-001`)
- **Date Found:** `YYYY-MM-DD`
- **Area:** `[Cart | Search | Auth | Navigation | Product Details]`
- **Environment:** `[nopCommerce Demo / Local Mock Server | Chrome | Windows 11]`
- **Severity:** `[Critical | Major | Minor | Low]`
- **Priority:** `[P0 | P1 | P2 | P3]`
- **Status:** `[Open | In Progress | Fixed | Retested | Closed]`

---

### 1. Description
A clear one-sentence summary of the defect and the condition where it happens.

### 2. Preconditions
- Initial state required to reproduce (e.g., cart contains 1 item, user is logged out).
- Target URL.

### 3. Steps to Reproduce
1. Navigate to `...`
2. Perform action `...`
3. Click on `...`
4. Observe the result.

### 4. Expected Result
What the application should do according to expected behavior.

### 5. Actual Result
What actually happened (e.g., error message displayed, incorrect total, missing button).

### 6. Reproducibility
- Always (100%) / Intermittent / Once only

### 7. Evidence
- Screenshot or recording (stored under `test-output/screenshots/`)
- Browser console error or network response (if any)
- Stack trace or test failure log:
  ```text
  [Insert stack trace or log excerpt here]
  ```

### 8. Workaround

Any temporary workaround found, or "None".

### 9. Verification
- **Retested in build / commit:** `[Commit hash or build]`
- **Retest date:** `YYYY-MM-DD`
- **Result:** `[Pass - Closed | Fail - Reopened]`
