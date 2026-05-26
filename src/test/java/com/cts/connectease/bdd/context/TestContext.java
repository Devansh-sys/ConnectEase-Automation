package com.cts.connectease.bdd.context;

import com.cts.connectease.constants.AppConstants;
import com.cts.connectease.pages.AIChatPage;
import com.cts.connectease.pages.ChatPage;
import com.cts.connectease.pages.CommunityForumPage;
import com.cts.connectease.pages.HomePage;
import com.cts.connectease.pages.LoginPage;
import com.cts.connectease.pages.NavbarPage;
import com.cts.connectease.pages.ProfilePage;
import com.cts.connectease.pages.ReviewsRatingsPage;
import com.cts.connectease.pages.ServiceDetailPage;
import com.cts.connectease.pages.ServiceListingsPage;
import com.cts.connectease.pages.SignupPage;
import com.cts.connectease.pages.VendorDashboardPage;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * TestContext — shared mutable state for a single Cucumber scenario.
 *
 * PicoContainer injects a fresh instance of this class into every step
 * definition class that declares it in its constructor. This means each
 * scenario gets its own WebDriver, its own page objects, and its own scratch
 * fields — enabling true parallel isolation.
 */
public class TestContext {

    // ── WebDriver ─────────────────────────────────────────────────────────────
    public WebDriver driver;
    public WebDriverWait wait;

    // ── Config ────────────────────────────────────────────────────────────────
    public final String BASE_URL          = AppConstants.BASE_URL;
    public final String CUSTOMER_EMAIL    = AppConstants.CUSTOMER_EMAIL;
    public final String CUSTOMER_PASSWORD = AppConstants.CUSTOMER_PASSWORD;
    public final String VENDOR_EMAIL      = AppConstants.VENDOR_EMAIL;
    public final String VENDOR_PASSWORD   = AppConstants.VENDOR_PASSWORD;

    // ── Page objects (initialised in initDriver) ──────────────────────────────
    public SignupPage          signupPage;
    public LoginPage           loginPage;
    public HomePage            homePage;
    public NavbarPage          navbarPage;
    public ServiceListingsPage serviceListingsPage;
    public ServiceDetailPage   serviceDetailPage;
    public ReviewsRatingsPage  reviewsRatingsPage;
    public VendorDashboardPage vendorDashboardPage;
    public CommunityForumPage  communityForumPage;
    public ChatPage            chatPage;
    public AIChatPage          aiChatPage;
    public ProfilePage         profilePage;

    // ── Cross-step scratch fields ─────────────────────────────────────────────
    public String customerUid;
    public String vendorUid;
    public String customerCookie;
    public String vendorCookie;
    public String scenarioData;

    // ── Driver lifecycle ──────────────────────────────────────────────────────

    public void initDriver() {
        WebDriverManager.chromedriver().browserVersion("146").setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-notifications");
        // Uncomment for headless CI:
        // options.addArguments("--headless=new");

        driver = new ChromeDriver(options);
        wait   = new WebDriverWait(driver, Duration.ofSeconds(AppConstants.EXPLICIT_WAIT));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(AppConstants.PAGE_LOAD_TIMEOUT));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(AppConstants.IMPLICIT_WAIT));

        // Initialise all page objects with this scenario's driver
        signupPage          = new SignupPage(driver);
        loginPage           = new LoginPage(driver);
        homePage            = new HomePage(driver);
        navbarPage          = new NavbarPage(driver);
        serviceListingsPage = new ServiceListingsPage(driver);
        serviceDetailPage   = new ServiceDetailPage(driver);
        reviewsRatingsPage  = new ReviewsRatingsPage(driver);
        vendorDashboardPage = new VendorDashboardPage(driver);
        communityForumPage  = new CommunityForumPage(driver);
        chatPage            = new ChatPage(driver);
        aiChatPage          = new AIChatPage(driver);
        profilePage         = new ProfilePage(driver);

        driver.get(BASE_URL);
    }

    public void quitDriver() {
        if (driver != null) {
            try { driver.quit(); } catch (Exception ignored) {}
            driver = null;
        }
    }

    public void clearSession() {
        if (driver == null) return;
        try { driver.manage().deleteAllCookies(); } catch (Exception ignored) {}
        try {
            ((JavascriptExecutor) driver).executeScript(
                "try{localStorage.clear();}catch(e){} try{sessionStorage.clear();}catch(e){}");
        } catch (Exception ignored) {}
    }

    public static void pause(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
