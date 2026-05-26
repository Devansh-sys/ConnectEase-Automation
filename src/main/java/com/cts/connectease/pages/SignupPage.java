package com.cts.connectease.pages;

import com.cts.connectease.constants.AppConstants;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object Model for the Signup Page
 * URL: https://connect-ease-nu.vercel.app/signup
 *
 * Actual form fields (confirmed via debugPrintInputFields):
 *   [0] text     placeholder="Enter your full name"
 *   [1] email    placeholder="name@example.com"
 *   [2] password placeholder="Create a password"
 *   [3] text     placeholder="Your phone number"
 *   [4] radio    id="role-customer"
 *   [5] radio    id="role-vendor"
 * There is NO confirm-password field on this form.
 */
public class SignupPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locator banks ─────────────────────────────────────────────────────────

    private static final By[] NAME_LOCATORS = {
            By.xpath("//input[@placeholder='Enter your full name']"),
            By.cssSelector("input[name='name']"),
            By.cssSelector("input[name='fullName']"),
            By.id("name"),
            By.id("fullName"),
            By.xpath("//input[contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'full name')]"),
            By.xpath("(//input[@type='text'])[1]")
    };

    private static final By[] EMAIL_LOCATORS = {
            By.cssSelector("input[type='email']"),
            By.cssSelector("input[name='email']"),
            By.id("email"),
            By.xpath("//input[@placeholder='name@example.com']"),
            By.xpath("//input[contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'email')]")
    };

    private static final By[] PASSWORD_LOCATORS = {
            By.xpath("//input[@placeholder='Create a password']"),
            By.cssSelector("input[name='password']"),
            By.id("password"),
            By.xpath("(//input[@type='password'])[1]")
    };

    private static final By[] PHONE_LOCATORS = {
            By.xpath("//input[@placeholder='Your phone number']"),
            By.cssSelector("input[name='phone']"),
            By.cssSelector("input[name='phoneNumber']"),
            By.cssSelector("input[name='mobile']"),
            By.id("phone"),
            By.id("phoneNumber"),
            By.xpath("//input[contains(translate(@placeholder,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'phone')]"),
            By.xpath("(//input[@type='text'])[2]")
    };

    private static final By[] SUBMIT_LOCATORS = {
            By.cssSelector("button[type='submit']"),
            By.cssSelector("input[type='submit']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'sign up')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'register')]"),
            By.xpath("//form//button[not(@type='button')]"),
            By.xpath("//form//button[last()]")
    };

    private static final By[] ERROR_LOCATORS = {
            By.cssSelector("[class*='error']"),
            By.cssSelector("[class*='Error']"),
            By.cssSelector(".alert-danger"),
            By.cssSelector("[role='alert']"),
            By.cssSelector("[class*='invalid']"),
            By.cssSelector("[class*='warning']"),
            By.xpath("//*[contains(@class,'error') or contains(@class,'Error')]")
    };

    private static final By ROLE_CUSTOMER = By.id("role-customer");
    private static final By ROLE_VENDOR   = By.id("role-vendor");

    // ── Constructor ───────────────────────────────────────────────────────────

    public SignupPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ── Visual helper ─────────────────────────────────────────────────────────

    /**
     * Briefly highlights the element with a yellow border so the viewer can see
     * which field is about to be interacted with.
     */
    private void highlight(WebElement el) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "arguments[0].style.outline='3px solid #f59e0b';" +
                "arguments[0].style.backgroundColor='#fef9c3';", el);
            js.executeScript(
                "arguments[0].style.outline='';" +
                "arguments[0].style.backgroundColor='';", el);
        } catch (Exception ignored) {}
    }

    // ── Core helper ───────────────────────────────────────────────────────────

    private WebElement findWithFallback(By[] locators, String fieldName) {
        for (By locator : locators) {
            try {
                WebElement el = driver.findElement(locator);
                if (el.isDisplayed()) {
                    System.out.println("   [Locator OK] " + fieldName + " → " + locator);
                    return el;
                }
            } catch (Exception ignored) {}
        }
        System.err.println("\n[LOCATOR FAIL] Could not find: " + fieldName);
        System.err.println("[LOCATOR FAIL] Current URL  : " + driver.getCurrentUrl());
        throw new RuntimeException(
                "No locator matched for: " + fieldName
                        + ". Run debugPrintInputFields() in your test to see real field attributes.");
    }

    private void waitForPageReady() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("input")));
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void navigateToSignup() {
        driver.get(AppConstants.BASE_URL + "/signup");
        waitForPageReady();
    }

    public void enterName(String name) {
        WebElement field = findWithFallback(NAME_LOCATORS, "Name");
        highlight(field);
        field.clear();
        field.sendKeys(name);
    }

    public void enterEmail(String email) {
        WebElement field = findWithFallback(EMAIL_LOCATORS, "Email");
        highlight(field);
        field.clear();
        field.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement field = findWithFallback(PASSWORD_LOCATORS, "Password");
        highlight(field);
        field.clear();
        field.sendKeys(password);
    }

    public void enterPhone(String phone) {
        WebElement field = findWithFallback(PHONE_LOCATORS, "Phone");
        highlight(field);
        field.clear();
        field.sendKeys(phone);
    }

    /**
     * Selects the role radio button.
     * Pass "customer" or "vendor" (case-insensitive).
     *
     * React apps typically hide the actual &lt;input type="radio"&gt; behind a styled
     * &lt;label&gt;. The element is NOT "clickable" in WebDriver terms, so we skip the
     * elementToBeClickable wait and use a cascade of click strategies instead.
     */
    public void selectRole(String role) {
        String id = "vendor".equalsIgnoreCase(role) ? "role-vendor" : "role-customer";
        JavascriptExecutor js = (JavascriptExecutor) driver;

        // Strategy 1 — click <label for="id"> (most reliable for styled radio buttons)
        try {
            List<WebElement> labels = driver.findElements(By.cssSelector("label[for='" + id + "']"));
            if (!labels.isEmpty()) {
                WebElement lbl = labels.get(0);
                highlight(lbl);
                try { lbl.click(); }
                catch (Exception e) { js.executeScript("arguments[0].click();", lbl); }
                System.out.println("   [Role] Clicked label[for=" + id + "]");
                return;
            }
        } catch (Exception ignored) {}

        // Strategy 2 — JS click the radio input directly
        try {
            WebElement radio = driver.findElement(By.id(id));
            highlight(radio);
            js.executeScript("arguments[0].click();", radio);
            System.out.println("   [Role] JS-clicked radio #" + id);
            return;
        } catch (Exception ignored) {}

        // Strategy 3 — JS set .checked + dispatch change/input/click events (last resort)
        try {
            js.executeScript(
                "var el = document.getElementById('" + id + "');" +
                "if (el) {" +
                "  el.checked = true;" +
                "  el.dispatchEvent(new Event('change', {bubbles:true}));" +
                "  el.dispatchEvent(new Event('input',  {bubbles:true}));" +
                "  el.dispatchEvent(new Event('click',  {bubbles:true}));" +
                "}"
            );
            System.out.println("   [Role] JS-forced checked on #" + id);
        } catch (Exception e) {
            System.err.println("[WARN] All selectRole strategies failed for '" + role + "': " + e.getMessage());
        }
    }

    public void clickSignupButton() {
        WebElement btn = findWithFallback(SUBMIT_LOCATORS, "Submit Button");
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btn);
        highlight(btn);
        try {
            wait.until(ExpectedConditions.elementToBeClickable(btn));
            btn.click();
        } catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
        }
    }

    /** Full signup as Customer: fills name, email, password, phone, selects Customer role, then submits. */
    public void signup(String name, String email, String password, String phone) {
        enterName(name);
        enterEmail(email);
        enterPassword(password);
        enterPhone(phone);
        selectRole("customer");
        clickSignupButton();
    }

    /** Full signup as Vendor: same as signup() but selects the Vendor radio button. */
    public void signupAsVendor(String name, String email, String password, String phone) {
        enterName(name);
        enterEmail(email);
        enterPassword(password);
        enterPhone(phone);
        selectRole("vendor");
        clickSignupButton();
    }

    // ── Verifications ─────────────────────────────────────────────────────────

    public boolean isSignupSuccessful() {
        try {
            wait.until(ExpectedConditions.not(
                    ExpectedConditions.urlContains("/signup")));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getErrorMessage() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        try {
            shortWait.until(driver -> {
                for (By locator : ERROR_LOCATORS) {
                    for (WebElement el : driver.findElements(locator)) {
                        try {
                            if (el.isDisplayed() && !el.getText().trim().isEmpty()) return true;
                        } catch (Exception ignored) {}
                    }
                }
                return false;
            });
        } catch (Exception ignored) {}

        for (By locator : ERROR_LOCATORS) {
            for (WebElement el : driver.findElements(locator)) {
                try {
                    if (el.isDisplayed()) {
                        String text = el.getText().trim();
                        if (!text.isEmpty()) return text;
                    }
                } catch (Exception ignored) {}
            }
        }
        return "";
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isSignupPageDisplayed() {
        try {
            List<WebElement> inputs = driver.findElements(By.cssSelector("input"));
            return inputs.stream().anyMatch(WebElement::isDisplayed);
        } catch (Exception e) {
            return false;
        }
    }

    // ── Debug helper ──────────────────────────────────────────────────────────

    public void debugPrintInputFields() {
        System.out.println("\n╔══════════════════════════════════════════════════════╗");
        System.out.println("║  DEBUG: All <input> fields on page                   ║");
        System.out.println("╠══════════════════════════════════════════════════════╣");
        System.out.println("  URL: " + driver.getCurrentUrl());
        List<WebElement> inputs = driver.findElements(By.cssSelector("input"));
        for (int i = 0; i < inputs.size(); i++) {
            WebElement el = inputs.get(i);
            System.out.printf("  [%d] type=%-10s  name=%-18s  id=%-18s  placeholder=%s%n",
                    i, attr(el, "type"), attr(el, "name"), attr(el, "id"), attr(el, "placeholder"));
        }
        System.out.println("╚══════════════════════════════════════════════════════╝\n");
    }

    private String attr(WebElement el, String attribute) {
        try {
            String v = el.getAttribute(attribute);
            return (v == null) ? "" : v;
        } catch (Exception e) {
            return "";
        }
    }
}
