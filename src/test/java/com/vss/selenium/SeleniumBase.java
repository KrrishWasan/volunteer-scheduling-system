package com.vss.selenium;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * Shared Selenium setup. The full application boots once on a fixed test port
 * and a single headless Chrome (Chrome-for-Testing headless shell) is reused
 * by all UI journeys.
 *
 * <p>Driver binaries live in {@code drivers/} (downloaded once from the
 * official Chrome-for-Testing endpoints, see {@code docs/week09-selenium.md}).
 * Selenium Manager is bypassed via {@code webdriver.chrome.driver} because the
 * default driver CDN is unreachable from this network.
 *
 * <p>Run locally with: {@code mvn test -Pselenium}
 */
@Tag("selenium")
@ExtendWith(ScreenshotOnFailure.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = {"server.port=18081", "app.seed-data=true"})
public abstract class SeleniumBase {

    protected static final String BASE_URL = "http://localhost:18081";

    private static WebDriver driver;

    @BeforeAll
    static void startBrowser() {
        Path root = Paths.get("").toAbsolutePath();
        Path driverBin = root.resolve("drivers/chromedriver-win64/chromedriver.exe");
        Path browserBin = root.resolve("drivers/chrome-headless-shell-win64/chrome-headless-shell.exe");
        if (!driverBin.toFile().exists()) {
            throw new IllegalStateException("chromedriver not found at " + driverBin
                    + " - see docs/week09-selenium.md for the one-time setup.");
        }
        System.setProperty("webdriver.chrome.driver", driverBin.toString());

        ChromeOptions options = new ChromeOptions();
        if (browserBin.toFile().exists()) {
            options.setBinary(browserBin.toString());
        }
        options.addArguments("--no-sandbox", "--disable-dev-shm-usage",
                "--window-size=1280,1024");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2));
    }

    @AfterAll
    static void stopBrowser() {
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    static WebDriver getDriver() {
        return driver;
    }

    protected WebDriver driver() {
        return driver;
    }

    protected WebDriverWait wait5() {
        return new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    protected void open(String path) {
        driver.get(BASE_URL + path);
    }

    protected WebElement visible(By locator) {
        return wait5().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected static String uniqueEmail(String prefix) {
        return prefix + System.nanoTime() + "@example.com";
    }

    /** Registers a volunteer through the UI and returns the e-mail used. */
    protected String registerVolunteer(String name, String email) {
        open("/register");
        visible(By.id("fullName")).sendKeys(name);
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("phone")).sendKeys("9876543210");
        driver.findElement(By.id("btn-register")).click();
        wait5().until(ExpectedConditions.urlContains("/slots"));
        return email;
    }

    /** Clicks the first "Book" link on the slots page and submits the booking form. */
    protected String bookFirstAvailableSlot(String email) {
        open("/slots");
        visible(By.cssSelector(".btn-book")).click();
        wait5().until(ExpectedConditions.visibilityOfElementLocated(By.id("book-form")));
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("btn-book")).click();
        wait5().until(ExpectedConditions.urlContains("/status"));
        return driver.getCurrentUrl();
    }

    /** Extracts the booking reference from a ".../status?ref=VSS-XXXXXX" URL. */
    protected static String referenceFromUrl(String url) {
        return url.substring(url.indexOf("ref=") + 4);
    }
}
