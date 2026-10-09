# Pre-Release Checklist: ShopSphere

This is the checklist I use before tagging a release, opening a pull request, or pushing changes to `main`.

---

## 1. Local Verification

- [ ] **Clean compile check (Offline / Local build):** Project compiles cleanly without errors (`mvn clean test-compile`).
- [ ] **Smoke tests on live demo store (Live Demo, Headed Chrome):** Default smoke suite passes against the public demo store (`mvn test`).
- [ ] **Smoke tests on embedded mock server (Mock Mode, Headed Chrome):** Sanity run on the local mock server (`mvn test "-DmockServer=true"`).
- [ ] **Full regression on embedded mock server (Mock Mode, Headed Chrome):** All 66 tests pass against the local mock server (`mvn test "-DsuiteFile=testng.xml" "-DmockServer=true"`).
- [ ] **Full regression on live demo store (Live Demo, Headed Chrome):** Full suite against the public demo store, noting network latency and shared data (`mvn test "-DsuiteFile=testng.xml"`).
- [ ] **Headless mode check (Mock Mode, Headless Chrome):** Tests pass without opening browser windows (`mvn test "-DmockServer=true" "-Dheadless=true"`).
- [ ] **Surefire reports verified (Local filesystem):** Reports generated under `target/surefire-reports/index.html` and `target/surefire-reports/emailable-report.html`.
- [ ] **Allure report verified (Local filesystem / browser):** Standalone HTML report generated (`mvn allure:report` to `target/site/allure-maven-plugin/`) or served interactively (`mvn allure:serve`).
- [ ] **Failure screenshots:** If any test fails, screenshots are captured in `test-output/screenshots/` and attached to Allure.

---

## 2. Code and Repository Health

- [ ] **Clean working tree:** `git status` shows no unexpected or temporary files.
- [ ] **Configuration integrity:** `src/test/resources/config/` files have correct default values.
- [ ] **Documentation updated:** Any new test scenarios or changes are documented in `README.md` and `docs/test-scenarios.md`.
- [ ] **No hardcoded secrets:** Credentials, passwords, or personal paths are not committed.

---

## 3. CI Pipeline Check

- [ ] **GitHub Actions passes:** The push or PR workflow finishes green on GitHub.
- [ ] **Artifacts uploaded:** Surefire test reports and Allure results are attached to the workflow run.

---

## 4. Known Boundaries

- Tests running against the embedded mock server verify test logic and framework stability.
- Live payment and order submission are intentionally excluded because the public nopCommerce demo does not provide an isolated payment sandbox.
