package com.cts.connectease.base;

import com.cts.connectease.constants.AppConstants;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.time.Duration;

/**
 * Base class for all Selenium UI tests.
 *
 * Visual test-observation features:
 *  • Chrome always opens maximised with no notifications (never headless)
 *  • highlight(element) — flashes a yellow/orange outline around an element for
 *    ~400 ms so you can see exactly what the test is interacting with
 *  • pause(ms)          — explicit pause so you can read the current page state
 *  • tearDown           — waits 2 s after every test before closing the browser,
 *    giving you time to see the pass/fail result before the window disappears
 */
public class BaseTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected final String BASE_URL          = AppConstants.BASE_URL;
    protected final String CUSTOMER_EMAIL    = AppConstants.CUSTOMER_EMAIL;
    protected final String CUSTOMER_PASSWORD = AppConstants.CUSTOMER_PASSWORD;
    protected final String VENDOR_EMAIL      = AppConstants.VENDOR_EMAIL;
    protected final String VENDOR_PASSWORD   = AppConstants.VENDOR_PASSWORD;

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    /**
     * Opens Chrome ONCE per test class.
     * All test methods in a class share the same browser window — no repeated
     * login screens between tests in the same class.
     */
    @BeforeClass
    public void setUp() {
        WebDriverManager.chromedriver().browserVersion("146").setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        // ↓ Do NOT add --headless — user must see the browser

        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(10));

        // Set page-load timeout so a hanging page fails fast instead of blocking forever
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        driver.get(BASE_URL);
    }

    /**
     * Closes the browser after all tests in the class have finished.
     */
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // ── Visual helpers ────────────────────────────────────────────────────────

    /**
     * Briefly highlights an element with a yellow/orange border so you can see
     * exactly which field or button the test is about to interact with.
     * Silently does nothing if the element is stale or the JS fails.
     */
    protected void highlight(WebElement element) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "arguments[0].style.outline='3px solid #f59e0b';" +
                "arguments[0].style.backgroundColor='#fef9c3';" +
                "arguments[0].style.transition='all 0.1s';",
                element);
            js.executeScript(
                "arguments[0].style.outline='';" +
                "arguments[0].style.backgroundColor='';",
                element);
        } catch (Exception ignored) {}
    }

    /**
     * Clears all cookies, localStorage, and sessionStorage so the next action
     * starts from a fully logged-out, clean-slate state.
     *
     * Call this at the top of:
     *  • any loginAs*() helper — so logging in from an already-authed browser works
     *  • any test that explicitly needs an unauthenticated browser state
     */
    protected void clearSession() {
        try { driver.manage().deleteAllCookies(); } catch (Exception ignored) {}
        try {
            ((JavascriptExecutor) driver).executeScript(
                "try { localStorage.clear(); } catch(e){} " +
                "try { sessionStorage.clear(); } catch(e){}");
        } catch (Exception ignored) {}
    }

    /**
     * Pauses test execution for {@code ms} milliseconds.
     * Use this when you want to hold the browser on a page long enough to read it.
     */
    protected static void pause(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
