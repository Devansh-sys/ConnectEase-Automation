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

    /**
     * ThreadLocal driver store — one slot per thread.
     *
     * Why static + ThreadLocal:
     *   • static   → shared across all instances of BaseTest and its subclasses
     *   • ThreadLocal → each thread gets its own independent WebDriver reference
     *
     * With parallel="classes" in TestNG, each class runs on its own thread.
     * driverHolder ensures Thread-1's driver never leaks into Thread-2, even
     * if the thread is reused by the pool after a class finishes.
     *
     * Lifecycle:
     *   @BeforeClass → driverHolder.set(driver)   — register driver for this thread
     *   @AfterClass  → driverHolder.remove()       — clean slot; prevents stale-driver leak
     */
    private static final ThreadLocal<WebDriver>     driverHolder = new ThreadLocal<>();
    private static final ThreadLocal<WebDriverWait> waitHolder   = new ThreadLocal<>();

    // ── Instance fields kept for backward compatibility ───────────────────────
    // All existing test classes access `driver` and `wait` directly.
    // These are set in setUp() to the same object stored in ThreadLocal,
    // so both driverHolder.get() and this.driver always point to the same instance.
    protected WebDriver     driver;
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

        // Register driver + wait in ThreadLocal so this thread's driver
        // is accessible via getDriver() / getWait() and is isolated from
        // any other thread running a different test class in parallel.
        driverHolder.set(driver);
        waitHolder.set(wait);

        // Set page-load timeout so a hanging page fails fast instead of blocking forever
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

        driver.get(BASE_URL);
    }

    /**
     * Closes the browser after all tests in the class have finished.
     * Removes the ThreadLocal entry so the thread slot is clean for reuse —
     * without remove(), a recycled thread would still hold a reference to the
     * quit (stale) driver, causing subtle failures in the next class on that thread.
     */
    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
        // Clean ThreadLocal slots — mandatory to prevent stale-driver leaks
        driverHolder.remove();
        waitHolder.remove();
        // Null instance fields so subclasses cannot accidentally use a dead driver
        driver = null;
        wait   = null;
    }

    // ── ThreadLocal accessors ─────────────────────────────────────────────────

    /**
     * Returns the WebDriver for the current thread.
     *
     * Prefer this over the {@code driver} field in any new code you write.
     * Existing test classes continue to use {@code driver} directly — both
     * point to the same object so there is no difference in behaviour.
     *
     * With parallel="classes": each class thread gets its own driver here.
     */
    protected static WebDriver getDriver() {
        return driverHolder.get();
    }

    /**
     * Returns the WebDriverWait for the current thread.
     */
    protected static WebDriverWait getWait() {
        return waitHolder.get();
    }

    // ── Visual helpers ────────────────────────────────────────────────────────

    /**
     * Briefly highlights an element with a yellow/orange border so you can see
     * exactly which field or button the test is about to interact with.
     * Silently does nothing if the element is stale or the JS fails.
     */
    protected void highlight(WebElement element) {
        try {
            // Use getDriver() so the correct thread's driver is always used,
            // even if this method is called from a parallel context.
            JavascriptExecutor js = (JavascriptExecutor) getDriver();
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
        // Use getDriver() — picks up the correct thread's driver instance.
        WebDriver d = getDriver();
        try { d.manage().deleteAllCookies(); } catch (Exception ignored) {}
        try {
            ((JavascriptExecutor) d).executeScript(
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
