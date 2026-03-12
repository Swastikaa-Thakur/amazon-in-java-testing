package com.amazon.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Base test class providing WebDriver lifecycle, ExtentReports, and
 * helper utilities for all Amazon.in test classes.
 */
public class BaseTest {

    protected static final Logger log = LogManager.getLogger(BaseTest.class);

    // WebDriver — ThreadLocal for parallel execution safety
    private static final ThreadLocal<WebDriver> driverThreadLocal = new ThreadLocal<>();

    // ExtentReports
    protected static ExtentReports extent;
    protected static final ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();

    // Config
    protected static final String BASE_URL   = "https://www.amazon.in";
    protected static final String BROWSER    = System.getProperty("browser", "chrome");
    protected static final boolean HEADLESS  = Boolean.parseBoolean(System.getProperty("headless", "true"));
    protected static final int IMPLICIT_WAIT = 10;   // seconds
    protected static final int PAGE_TIMEOUT  = 30;   // seconds

    // ── SUITE SETUP ────────────────────────────────────────────────
    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportPath = "reports/AmazonIn_TestReport_" + timestamp + ".html";

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setDocumentTitle("Amazon.in Automation Report");
        spark.config().setReportName("Amazon India — Selenium Test Suite");
        spark.config().setTheme(com.aventstack.extentreports.reporter.configuration.Theme.DARK);

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Target URL",  BASE_URL);
        extent.setSystemInfo("Browser",     BROWSER);
        extent.setSystemInfo("Headless",    String.valueOf(HEADLESS));
        extent.setSystemInfo("Java",        System.getProperty("java.version"));
        extent.setSystemInfo("OS",          System.getProperty("os.name"));

        log.info("ExtentReports initialised → {}", reportPath);
    }

    // ── TEST SETUP ─────────────────────────────────────────────────
    @BeforeMethod(alwaysRun = true)
    @Parameters({"browser"})
    public void setUpDriver(@Optional String browserParam) {
        String useBrowser = (browserParam != null) ? browserParam : BROWSER;
        WebDriver driver = createDriver(useBrowser);
        driverThreadLocal.set(driver);
        log.info("Driver started [{}] thread={}", useBrowser, Thread.currentThread().getId());
    }

    // ── TEST TEARDOWN ──────────────────────────────────────────────
    @AfterMethod(alwaysRun = true)
    public void tearDownDriver(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            log.error("TEST FAILED: {}", result.getName());
            String screenshotPath = takeScreenshot(result.getName());
            if (extentTest.get() != null) {
                extentTest.get().fail(result.getThrowable());
                try {
                    extentTest.get().addScreenCaptureFromPath(screenshotPath);
                } catch (Exception ignored) {}
            }
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            if (extentTest.get() != null) extentTest.get().pass("Test passed");
        } else {
            if (extentTest.get() != null) extentTest.get().skip("Test skipped");
        }

        WebDriver driver = getDriver();
        if (driver != null) {
            driver.quit();
            driverThreadLocal.remove();
            log.info("Driver closed for test: {}", result.getName());
        }
    }

    // ── SUITE TEARDOWN ─────────────────────────────────────────────
    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        if (extent != null) {
            extent.flush();
            log.info("ExtentReports flushed.");
        }
    }

    // ── DRIVER FACTORY ─────────────────────────────────────────────
    private WebDriver createDriver(String browser) {
        return switch (browser.toLowerCase()) {
            case "firefox" -> {
                WebDriverManager.firefoxdriver().setup();
                FirefoxOptions opts = new FirefoxOptions();
                if (HEADLESS) opts.addArguments("--headless");
                FirefoxDriver fd = new FirefoxDriver(opts);
                configureDriver(fd);
                yield fd;
            }
            default -> {
                WebDriverManager.chromedriver().setup();
                ChromeOptions opts = new ChromeOptions();
                if (HEADLESS) opts.addArguments("--headless=new");
                opts.addArguments("--no-sandbox");
                opts.addArguments("--disable-dev-shm-usage");
                opts.addArguments("--disable-blink-features=AutomationControlled");
                opts.addArguments("--window-size=1280,720");
                opts.addArguments("--lang=en-IN");
                opts.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
                ChromeDriver cd = new ChromeDriver(opts);
                configureDriver(cd);
                yield cd;
            }
        };
    }

    private void configureDriver(WebDriver driver) {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(IMPLICIT_WAIT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(PAGE_TIMEOUT));
        driver.manage().window().maximize();
    }

    // ── HELPERS ────────────────────────────────────────────────────
    public WebDriver getDriver() {
        return driverThreadLocal.get();
    }

    public String takeScreenshot(String testName) {
        try {
            String dir = "reports/screenshots/";
            Files.createDirectories(Paths.get(dir));
            String path = dir + testName + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("HHmmss")) + ".png";
            byte[] bytes = ((TakesScreenshot) getDriver()).getScreenshotAs(OutputType.BYTES);
            Files.write(Path.of(path), bytes);
            log.info("Screenshot saved: {}", path);
            return path;
        } catch (IOException e) {
            log.warn("Could not save screenshot: {}", e.getMessage());
            return "";
        }
    }

    public void navigateTo(String url) {
        getDriver().get(url);
        log.info("Navigated to: {}", url);
    }

    public void navigateToBase() {
        navigateTo(BASE_URL);
    }
}
