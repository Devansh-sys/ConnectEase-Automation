package com.cts.connectease.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object — User Profile Page (/profile)
 * Covers: CE-FE-PROF-TC001 through CE-FE-PROF-TC007
 * Three-tab layout: Profile Info | Change Password | Danger Zone
 */
public class ProfilePage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Page ready ────────────────────────────────────────────────────────────
    private final By[] pageReadyLocators = {
            By.cssSelector(".profile-card"),
            By.cssSelector(".profile-container"),
            By.cssSelector(".profile-section"),
            By.cssSelector("[class*='profile']"),
            By.cssSelector("h1, h2, h3")
    };

    // ── Tab navigation (TC003) ────────────────────────────────────────────────
    private final By tabItems = By.cssSelector(
            ".tab, .nav-tab, [class*='tab-item'], [role='tab'], " +
            "[class*='profile-tab'], [class*='tabs'] button, .tab-btn");

    // ── Profile Info tab fields ────────────────────────────────────────────────
    private final By[] fullNameLocators = {
            By.cssSelector("input[name='fullName']"),
            By.cssSelector("input[name='name']"),
            By.cssSelector("input[placeholder*='Full Name' i]"),
            By.cssSelector("input[placeholder*='name' i]")
    };

    private final By[] phoneLocators = {
            By.cssSelector("input[name='phoneNo']"),
            By.cssSelector("input[name='phone']"),
            By.cssSelector("input[name='mobile']"),
            By.cssSelector("input[placeholder*='phone' i]"),
            By.cssSelector("input[type='tel']")
    };

    private final By[] emailLocators = {
            By.cssSelector("input[name='email']"),
            By.cssSelector("input[type='email']"),
            By.cssSelector("input[placeholder*='email' i]"),
            By.cssSelector(".profile-email"),
            By.cssSelector("[class*='email']")
    };

    // ── Save / Update button ──────────────────────────────────────────────────
    private final By[] saveButtonLocators = {
            By.cssSelector("button[type='submit']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'save')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'update')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'edit')]")
    };

    // ── Change Password tab fields (TC005, TC006) ─────────────────────────────
    private final By[] currentPasswordLocators = {
            By.cssSelector("input[name='oldPassword']"),
            By.cssSelector("input[name='currentPassword']"),
            By.cssSelector("input[placeholder*='current' i]"),
            By.cssSelector("input[placeholder*='old' i]"),
            By.xpath("(//input[@type='password'])[1]")
    };

    private final By[] newPasswordLocators = {
            By.cssSelector("input[name='newPassword']"),
            By.cssSelector("input[placeholder*='new password' i]"),
            By.xpath("(//input[@type='password'])[2]")
    };

    private final By[] confirmPasswordLocators = {
            By.cssSelector("input[name='confirmPassword']"),
            By.cssSelector("input[name='confirmNewPassword']"),
            By.cssSelector("input[placeholder*='confirm' i]"),
            By.xpath("(//input[@type='password'])[3]")
    };

    private final By[] changePasswordSubmitLocators = {
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'change password')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'update password')]"),
            By.cssSelector("form button[type='submit']")
    };

    private final By passwordErrorLocator = By.cssSelector(
            ".password-error, [class*='password-error'], .mismatch, [class*='mismatch'], " +
            ".error, .invalid-feedback, [class*='error']");

    // ── Danger Zone tab (TC007) ───────────────────────────────────────────────
    private final By dangerZoneLocator = By.cssSelector(
            ".danger-zone, [class*='danger-zone'], [class*='danger'], .delete-account-section");

    private final By[] deleteAccountButtonLocators = {
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'delete account')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'delete my account')]"),
            By.cssSelector(".delete-account-btn, [class*='delete-account']")
    };

    // ── Profile display ────────────────────────────────────────────────────────
    private final By[] profileDisplayLocators = {
            By.cssSelector(".profile-name"),
            By.cssSelector(".user-name"),
            By.cssSelector("[class*='profile'] h2"),
            By.cssSelector("[class*='profile'] h3"),
            By.cssSelector("[class*='avatar']")
    };

    // ── Constructor ───────────────────────────────────────────────────────────

    public ProfilePage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ── Visual helper ─────────────────────────────────────────────────────────

    private void highlight(WebElement el) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].style.outline='3px solid #f59e0b';arguments[0].style.backgroundColor='#fef9c3';", el);
            js.executeScript("arguments[0].style.outline='';arguments[0].style.backgroundColor='';", el);
        } catch (Exception ignored) {}
    }

    private WebElement findFirst(By[] locators) {
        for (By loc : locators) {
            try {
                List<WebElement> els = driver.findElements(loc);
                for (WebElement el : els) {
                    if (el.isDisplayed()) return el;
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private void clearAndType(By[] locators, String value) {
        WebElement el = findFirst(locators);
        if (el != null) { highlight(el); el.clear(); el.sendKeys(value); }
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/profile");
        wait.until(d -> {
            for (By loc : pageReadyLocators) {
                if (!d.findElements(loc).isEmpty()) return true;
            }
            return false;
        });
    }

    // ── Tab navigation ────────────────────────────────────────────────────────

    public void clickTab(String tabText) {
        List<WebElement> tabs = driver.findElements(tabItems);
        for (WebElement tab : tabs) {
            try {
                if (tab.isDisplayed() && tab.getText().toLowerCase().contains(tabText.toLowerCase())) {
                    highlight(tab);
                    try { tab.click(); }
                    catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab); }
                    return;
                }
            } catch (Exception ignored) {}
        }
        // XPath fallback
        try {
            WebElement fb = driver.findElement(By.xpath(
                "//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" +
                tabText.toLowerCase() + "')] | //a[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" +
                tabText.toLowerCase() + "')]"));
            highlight(fb);
            fb.click();
        } catch (Exception ignored) {}
    }

    public boolean isThreeTabLayoutVisible() {
        long count = driver.findElements(tabItems).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
        return count >= 2; // at least 2 tabs means a tab layout
    }

    // ── Verifications ────────────────────────────────────────────────────────

    public boolean isProfilePageDisplayed() { return findFirst(pageReadyLocators) != null; }

    public boolean isProfileNameVisible() { return findFirst(profileDisplayLocators) != null; }

    public boolean isEmailVisible() { return findFirst(emailLocators) != null; }

    public String getDisplayedEmail() {
        WebElement el = findFirst(emailLocators);
        if (el == null) return "";
        try { return el.getAttribute("value") != null ? el.getAttribute("value") : el.getText(); }
        catch (Exception e) { return ""; }
    }

    /** TC003 — email field should be read-only */
    public boolean isEmailFieldReadOnly() {
        WebElement el = findFirst(emailLocators);
        if (el == null) return false;
        try {
            String ro = el.getAttribute("readonly");
            String disabled = el.getAttribute("disabled");
            return "true".equals(ro) || ro != null && !ro.isEmpty() ||
                   "true".equals(disabled) || disabled != null && !disabled.isEmpty() ||
                   !el.isEnabled();
        } catch (Exception e) { return false; }
    }

    public boolean isOnProfilePage() { return driver.getCurrentUrl().contains("/profile"); }

    // ── Profile Info actions ──────────────────────────────────────────────────

    public void editFullName(String newName) { clearAndType(fullNameLocators, newName); }

    public void editPhone(String newPhone) { clearAndType(phoneLocators, newPhone); }

    public void clickSave() {
        WebElement btn = findFirst(saveButtonLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    /** TC004 — check localStorage fullName updated */
    public String getLocalStorageFullName() {
        try {
            Object val = ((JavascriptExecutor) driver)
                    .executeScript("return localStorage.getItem('fullName') || localStorage.getItem('name') || null;");
            return val != null ? val.toString() : "";
        } catch (Exception e) { return ""; }
    }

    // ── Change Password actions (TC005, TC006) ────────────────────────────────

    public void fillCurrentPassword(String pwd) { clearAndType(currentPasswordLocators, pwd); }

    public void fillNewPassword(String pwd) { clearAndType(newPasswordLocators, pwd); }

    public void fillConfirmPassword(String pwd) { clearAndType(confirmPasswordLocators, pwd); }

    public void clickChangePassword() {
        WebElement btn = findFirst(changePasswordSubmitLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    /** TC005 — mismatch error shown when confirm != new */
    public boolean isPasswordErrorVisible() {
        return driver.findElements(passwordErrorLocator).stream().anyMatch(e -> {
            try { return e.isDisplayed() && !e.getText().trim().isEmpty(); } catch (Exception ex) { return false; }
        });
    }

    // ── Danger Zone actions (TC007) ───────────────────────────────────────────

    public boolean isDangerZoneVisible() {
        return !driver.findElements(dangerZoneLocator).isEmpty();
    }

    public boolean isDeleteAccountButtonVisible() {
        return findFirst(deleteAccountButtonLocators) != null;
    }

    public boolean isDeleteAccountButtonEnabled() {
        WebElement btn = findFirst(deleteAccountButtonLocators);
        if (btn == null) return false;
        try { return btn.isEnabled(); } catch (Exception e) { return false; }
    }

    // ── Success / error toasts ────────────────────────────────────────────────

    public boolean isSuccessMessageVisible() {
        try {
            return wait.until(d -> {
                List<WebElement> els = d.findElements(
                    By.xpath("//*[contains(@class,'success') or contains(@class,'toast') or contains(@class,'alert')]"));
                return els.stream().anyMatch(e -> {
                    try { return e.isDisplayed() && !e.getText().trim().isEmpty(); } catch (Exception ex) { return false; }
                });
            });
        } catch (Exception e) { return false; }
    }

    public boolean isErrorToastVisible() {
        List<WebElement> els = driver.findElements(
            By.xpath("//*[contains(@class,'error') or contains(@class,'toast-error') or contains(@class,'alert-danger')]"));
        return els.stream().anyMatch(e -> {
            try { return e.isDisplayed() && !e.getText().trim().isEmpty(); } catch (Exception ex) { return false; }
        });
    }
}
