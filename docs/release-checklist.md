# Release Readiness Checklist: ShopSphere

This checklist governs the formal release readiness verification process. Before any candidate build is approved for promotion to production, each quality gate item must be validated and signed off by the QA Lead.

---

## 1. Release Metadata
* **Release Version / Tag:** `vX.Y.Z`
* **Commit SHA:** `[git commit hash]`
* **Target Environment:** `[Staging / Pre-Production]`
* **Target Release Date:** `YYYY-MM-DD`
* **Lead QA Assessor:** `[QA Engineer Name]`

---

## 2. Quality Gate Verification Table

| Verification Item | Requirement / Threshold | Status | Evidence / Notes |
| :--- | :--- | :--- | :--- |
| **Functional Validation** | All in-scope user stories and acceptance criteria verified | `[ ] PASS / [ ] FAIL` | Linked Jira release dashboard |
| **Automated Smoke Suite** | 100% pass rate on all P0 critical journey tests | `[ ] PASS / [ ] FAIL` | GitHub Actions Run #`[ID]` |
| **Automated Regression Suite** | 100% pass rate on P0/P1 regression tests | `[ ] PASS / [ ] FAIL` | Maven Surefire Test Report |
| **Exploratory Testing** | Time-boxed charter sessions completed across volatile areas | `[ ] PASS / [ ] FAIL` | Refer to exploratory-testing.md |
| **P0 (Critical) Defects** | Exactly 0 open unresolved critical defects | `[ ] PASS / [ ] FAIL` | Defect Tracker Query: 0 open |
| **P1 (High) Defects** | Exactly 0 open high defects (or approved PM waiver) | `[ ] PASS / [ ] FAIL` | Defect Tracker Query: 0 open |
| **Known Issues & Workarounds** | All minor (P2/P3) deferred defects documented with workarounds | `[ ] PASS / [ ] FAIL` | Documented in Release Notes |
| **Automation Health** | Zero unmanaged test flakiness; all assertions deterministic | `[ ] PASS / [ ] FAIL` | Clean retry logs |
| **CI Build Status** | Green pipeline on `main` branch with clean test artifact upload | `[ ] PASS / [ ] FAIL` | GitHub Actions workflow status |
| **Evidence Archived** | Test execution reports, logs, and screenshots safely archived | `[ ] PASS / [ ] FAIL` | `target/surefire-reports/` stored |

---

## 3. Known Limitations & Edge-Case Considerations
* **Shared Environment Constraints:** Verification on the shared public demo excludes live payment gateway transactions; manual smoke check of sandbox payment is conducted on isolated pre-production staging where applicable.
* **Network Latency Overhead:** Cloudflare caching or cold starts may slightly elevate initial page load; resilient explicit waits handle this gracefully.

---

## 4. Final QA Recommendation
* [ ] **GO:** All critical quality criteria satisfied. No blocking risks identified. Recommended for release.
* [ ] **CONDITIONAL GO:** Minor non-blocking issues identified with documented workarounds and engineering agreement.
* [ ] **NO-GO:** Critical test failure, regression defect, or incomplete quality gate. Release blocked pending resolution.

**Lead QA Sign-Off:** ___________________________  **Date:** _______________
