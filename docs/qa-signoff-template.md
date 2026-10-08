# QA Sign-Off Certificate Template

> **Document Type:** Release Quality Assurance Sign-Off Record  
> **Status:** Template (To be populated per release candidate)

---

## 1. Release & Build Identification
* **Release Version:** `[e.g., Release 2026.10-RC1]`
* **Git Commit Hash:** `[e.g., a1b2c3d4e5f6]`
* **Target Environment:** `[Staging / UAT / Pre-Prod / Demo Store]`
* **Evaluation Date:** `YYYY-MM-DD`
* **Assigned QA Engineer:** `[Name / Title]`

---

## 2. Summary of Testing Activities Completed
* [ ] Automated Smoke Suite Execution (`smoke` group)
* [ ] Automated Full Regression Suite Execution (`regression`, `negative` groups)
* [ ] Manual / Exploratory Testing Charters (Discovery, Cart mutation, Form validation)
* [ ] Cross-browser validation (Headless Chrome in CI / Standard Chrome locally)
* [ ] Negative scenario & error-handling verification

---

## 3. Test Execution Metrics & Quality Gates

| Test Phase | Total Planned | Executed | Passed | Failed | Blocked | Pass Rate |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **Smoke Suite (P0)** | `[ ]` | `[ ]` | `[ ]` | `[ ]` | `[ ]` | `[ ] %` |
| **Regression Suite (P1/P2)** | `[ ]` | `[ ]` | `[ ]` | `[ ]` | `[ ]` | `[ ] %` |
| **Negative Scenarios** | `[ ]` | `[ ]` | `[ ]` | `[ ]` | `[ ]` | `[ ] %` |
| **Exploratory Charters** | `[ ]` | `[ ]` | `[ ]` | `[ ]` | `[ ]` | N/A |

### 3.1 CI / Quality Gate Verification
* **GitHub Actions Workflow:** `[qa-regression.yml]`
* **CI Execution Run ID / Link:** `[Run URL / #ID]`
* **Surefire Report Location:** `[target/surefire-reports/emailable-report.html]`
* **Failure Screenshots:** `[None / Archived under test-output/screenshots/]`

---

## 4. Defect Status at Time of Assessment
* **Critical (S1 / P0) Open Defects:** `0` *(Mandatory for release)*
* **High (S2 / P1) Open Defects:** `0` *(Mandatory for release)*
* **Medium / Low (P2 / P3) Open Defects:** `[Count]` *(Detailed below)*

### Known Limitations & Deferred Items
1. **Scope Boundary:** End-to-end payment gateway transaction completion is intentionally out of automated scope on the public shared demo environment due to lack of an isolated merchant sandbox.
2. `[Detail any accepted minor cosmetic or non-blocking deferred tickets with Jira ID]`

---

## 5. QA Assessment & Final Recommendation
* [ ] **APPROVED FOR RELEASE (GO):** Build satisfies all quality gates, 100% smoke/regression pass rate, 0 critical/high open defects.
* [ ] **REJECTED (NO-GO):** Build fails quality gates or contains unmitigated high-risk defects.

**QA Engineer Signature:** ___________________________________  **Date:** _______________  
**QA Lead / Engineering Manager:** ___________________________  **Date:** _______________  
