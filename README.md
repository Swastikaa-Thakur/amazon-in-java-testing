# ☕ Amazon.in — Java Selenium Automation Suite

**Selenium WebDriver 4 + TestNG 7 + Maven** automation test suite for [amazon.in](https://www.amazon.in).

## Tech Stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Selenium WebDriver | 4.18.1 | Browser automation |
| TestNG | 7.9.0 | Test framework |
| WebDriverManager | 5.7.0 | Auto browser driver setup |
| ExtentReports | 5.1.1 | HTML test reports |
| Maven | 3.x | Build & dependency management |
| Log4j2 | 2.23.0 | Logging |

## Quick Start

```bash
# 1. Clone
git clone https://github.com/yourname/amazon-in-java-testing.git
cd amazon-in-java-testing

# 2. Run all tests (headless Chrome)
mvn test -Dheadless=true

# 3. Run with visible Chrome
mvn test -Dheadless=false

# 4. Run single suite
mvn test -Dheadless=true -Dbrowser=chrome \
  -Dsurefire.suiteXmlFiles=src/test/resources/testng.xml

# 5. Specific test class
mvn test -Dtest=SearchTest -Dheadless=true
```

## Test Classes

| Class | Tests | Covers |
|---|---|---|
| `HomepageTest` | 9 | Logo, search bar, nav links, cart icon |
| `SearchTest` | 18 | Search, autocomplete, DataProvider, PDP click-through |
| `ProductDetailTest` | 14 | ₹ price, rating, Add to Cart, /dp/ URL |
| `CartTest` | 8 | Add, badge count, ₹ subtotal, delete |
| `FiltersTest` | 5 | Price range, star rating, sort |
| `SignInTest` | 9 | Form validation, error messages |

## Reports

After test run, open `reports/AmazonIn_TestReport_<timestamp>.html` in your browser.

## Deploy to Netlify

Drag-drop the project folder to Netlify — publish dir is `.` (the root with `index.html`).

## Notes

- Amazon's CAPTCHA blocks automated login in CI — `SignInTest` covers UI/form only.
- `WebDriverManager` auto-downloads matching `chromedriver` — no manual setup needed.
- ThreadLocal WebDriver supports parallel test execution.
