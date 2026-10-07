# Week 9 – Selenium Test Design and Local Execution

## 1. Test plan (5 critical user journeys → stories VSS-1 … VSS-6)

| # | Journey | Steps | Assertions |
|---|---------|-------|------------|
| 1 | Volunteer registration | Home → Register → valid data → submit | Redirect to `/slots`, "Registration successful"; duplicate e-mail → "already registered" |
| 2 | Slot booking request | Register → Slots → first Book → enter e-mail → submit | Redirect to `/status?ref=VSS-…`, table shows `PENDING` |
| 3 | Status tracking | Book → track by reference, then by e-mail | Reference found both ways |
| 4 | Coordinator confirmation | Book → `/coordinator` → Confirm | Status becomes `CONFIRMED` |
| 5 | Cancellation | Book → `/status` → Cancel | Status becomes `CANCELLED` |

Negative cases inside the journeys: duplicate e-mail (J1), unregistered e-mail and
duplicate booking (covered by `BookingServiceTest`), full-slot booking blocked
(`capacityIsNeverExceeded`).

## 2. Implementation
- `src/test/java/com/vss/selenium/`: `SeleniumBase` (boots the app once on port
  18081, one headless Chrome), `ScreenshotOnFailure` (TestWatcher → PNG in
  `target/screenshots/` on any failure), plus one class per journey.
- Stable locators: element IDs (`btn-register`, `btn-book`, `bookings-table`,
  `btn-confirm`, `btn-cancel`, `success-box`, `error-box`).
- Unit profile: default `mvn test` runs only fast tests (`-Pselenium` selects the
  `selenium` group; see `pom.xml` surefire `groups`/`excludedGroups`).

## 3. One-time driver setup (this network blocks the default Edge-driver CDN,
   so Selenium Manager is bypassed)
```powershell
# Chrome-for-Testing stable release (see googlechromelabs "chrome-for-testing"):
#   drivers/chromedriver-win64/chromedriver.exe
#   drivers/chrome-headless-shell-win64/chrome-headless-shell.exe
```
`SeleniumBase` uses `drivers/` automatically; the folder is git-ignored.

## 4. Local execution
```bash
mvn test -Pselenium
# Tests run: 5, Failures: 0, Errors: 0, Skipped: 0
# Report: target/surefire-reports/*UiTest.txt (+ JUnit XML for Jenkins)
```
Failure evidence: `target/screenshots/<TestClass>-<testMethod>.png` is saved
automatically by `ScreenshotOnFailure`.
