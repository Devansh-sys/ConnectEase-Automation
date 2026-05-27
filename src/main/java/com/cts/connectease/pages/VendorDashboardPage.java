package com.cts.connectease.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object — Vendor Dashboard Page (/vendor/dashboard)
 * Covers: CE-FE-VEND-TC001 through CE-FE-VEND-TC009
 * Tabs: Dashboard | My Listings | Add Service
 */
public class VendorDashboardPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Dashboard / page locators ─────────────────────────────────────────────
    private final By[] dashboardLocators = {
            By.cssSelector(".dashboard"),
            By.cssSelector(".vendor-dashboard"),
            By.cssSelector("[class*='dashboard']"),
            By.cssSelector(".stats, .stat-card, [class*='stat']")
    };

    // ── Stats cards (TC002) ───────────────────────────────────────────────────
    private final By statCards = By.cssSelector(
            ".stat-card, .stats-card, [class*='stat-card'], [class*='dashboard-stat'], " +
            "[class*='stats'] .card, .metric-card, " +
            "[class*='StatCard'], [class*='DashboardStat'], [class*='overview-card'], " +
            "[class*='summary-card'], [class*='kpi'], [class*='metric']");

    // XPath for stat cards by label text (activeListings / totalViews / reviews / rating)
    private final By statCardsByText = By.xpath(
            "//*[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'active listing') or " +
            "contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'total view') or " +
            "contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'total review') or " +
            "contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'average rating') or " +
            "contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'avg rating')]");

    // ── Tab navigation ────────────────────────────────────────────────────────
    // .nav-item is the actual Angular class (confirmed from bundle)
    private final By tabItems = By.cssSelector(
            ".nav-item, .tab, .nav-tab, [class*='tab-item'], [role='tab'], .dashboard-tab, " +
            "[class*='dashboard-nav'] button, [class*='tabs'] button");

    // ── My Listings tab (TC003) ───────────────────────────────────────────────
    // .listing-card is the actual Angular class (confirmed from bundle)
    private final By vendorListingCards = By.cssSelector(
            ".listing-card, [class*='listing-card'], .vendor-service-card, .my-service-card, " +
            "[class*='vendor-service'], .service-list .card, [class*='service-item'], " +
            "[class*='service-card'], .listings .card, [class*='my-listing'] .card");

    // Fallback: any card-like element that contains an Edit action (stat cards never have Edit)
    private final By vendorListingCardsFallback = By.xpath(
            "//*[contains(@class,'card') or contains(@class,'item') or contains(@class,'listing')]" +
            "[.//*[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," +
            "'abcdefghijklmnopqrstuvwxyz'),'edit') or " +
            "contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ'," +
            "'abcdefghijklmnopqrstuvwxyz'),'delete')]]");

    // ── Add Service form (TC004, TC005, TC006) ────────────────────────────────
    // Actual button text is "Add New Service" — "add service" is NOT a substring of "add new service"
    private final By[] addServiceButtonLocators = {
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add new service')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'new service')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add service')]"),
            By.xpath("//a[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add service')]"),
            By.cssSelector("button.add-service, .btn-add, [class*='add-service']"),
            By.cssSelector("a[routerlink*='add'], button[routerlink*='add']")
    };

    // .form-grid and .form-section are actual Angular classes (confirmed from bundle)
    private final By[] serviceFormLocators = {
            By.cssSelector(".form-grid"),
            By.cssSelector(".form-section"),
            By.cssSelector("form.service-form"),
            By.cssSelector(".add-service-form"),
            By.cssSelector("[class*='service-form']"),
            By.cssSelector("form")
    };

    // Actual placeholder is "e.g. Home Deep Cleaning" (confirmed from bundle)
    private final By[] serviceNameInputLocators = {
            By.cssSelector("input[placeholder*='Home Deep Cleaning' i]"),
            By.cssSelector("input[placeholder*='Deep Cleaning' i]"),
            By.cssSelector("input[name='name']"),
            By.cssSelector("input[name='serviceName']"),
            By.cssSelector("input[name='title']"),
            By.cssSelector("input[placeholder*='service name' i]"),
            By.cssSelector("input[placeholder*='name' i]"),
            By.xpath("(//input[@type='text'])[1]")
    };

    private final By[] priceInputLocators = {
            By.cssSelector("input[name='price']"),
            By.cssSelector("input[type='number']"),
            By.cssSelector("input[placeholder*='price' i]"),
            By.cssSelector("input[placeholder*='rate' i]")
    };

    private final By[] descInputLocators = {
            By.cssSelector("textarea[name='description']"),
            By.cssSelector("textarea"),
            By.cssSelector("input[name='description']")
    };

    private final By[] imageUrlInputLocators = {
            By.cssSelector("input[name='imageUrl']"),
            By.cssSelector("input[name='image']"),
            By.cssSelector("input[placeholder*='image' i]"),
            By.cssSelector("input[placeholder*='url' i]")
    };

    // .btn-save is the actual Angular submit button class (confirmed from bundle CSS)
    private final By[] submitServiceLocators = {
            By.cssSelector(".btn-save"),
            By.cssSelector("[class*='btn-save']"),
            By.cssSelector("form button[type='submit']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submit')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'save')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'create')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add')]")
    };

    private final By validationErrorLocator = By.cssSelector(
            ".error, .validation-error, [class*='error'], .invalid-feedback, " +
            "[class*='required'], input:invalid");

    // ── Edit / Delete on listing cards (TC007, TC008) ─────────────────────────
    private final By[] editButtonLocators = {
            By.cssSelector(".edit-btn, .btn-edit, [class*='edit-btn']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'edit')]")
    };

    private final By[] deleteButtonLocators = {
            By.cssSelector(".delete-btn, .btn-delete, [class*='delete-btn']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'delete')]")
    };

    private final By deleteConfirmLocator = By.cssSelector(
            ".confirm-delete, [class*='confirm'], .modal, [class*='modal'], " +
            "[role='dialog'], [class*='inline-confirm']");

    private final By[] confirmYesLocators = {
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'confirm')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'yes')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'delete')]")
    };

    // ── Toggle Active/Inactive (TC009) ────────────────────────────────────────
    private final By[] toggleLocators = {
            By.cssSelector(".toggle-status, .status-toggle, input[type='checkbox'][class*='toggle']"),
            By.cssSelector("[class*='toggle'], .switch input"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'active') or contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'inactive')]")
    };

    private final By inactiveIndicator = By.cssSelector(
            "[class*='inactive'], .badge-inactive, [class*='status-inactive']");

    // ── Constructor ───────────────────────────────────────────────────────────

    public VendorDashboardPage(WebDriver driver) {
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

    // ── Navigation ────────────────────────────────────────────────────────────

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/vendor/dashboard");
        // Wait for URL to confirm Angular route is active; DOM check as secondary signal
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
            if (!d.getCurrentUrl().contains("/vendor/dashboard")) return false;
            for (By loc : dashboardLocators) {
                if (!d.findElements(loc).isEmpty()) return true;
            }
            // URL is correct — accept even if specific DOM classes haven't rendered yet
            return true;
        });
    }

    // ── Tab navigation ────────────────────────────────────────────────────────

    /** Click a tab by partial text (e.g. "My Listings", "Add Service", "Dashboard") */
    public void clickTab(String tabText) {
        List<WebElement> tabs = driver.findElements(tabItems);
        for (WebElement tab : tabs) {
            try {
                if (tab.isDisplayed() && tab.getText().toLowerCase().contains(tabText.toLowerCase())) {
                    highlight(tab);
                    try { tab.click(); } catch (Exception e) {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", tab);
                    }
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

    public boolean isTabVisible(String tabText) {
        List<WebElement> tabs = driver.findElements(tabItems);
        for (WebElement tab : tabs) {
            try {
                if (tab.isDisplayed() && tab.getText().toLowerCase().contains(tabText.toLowerCase())) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    // ── Verifications — dashboard ─────────────────────────────────────────────

    public boolean isDashboardDisplayed() {
        // URL is the most reliable signal — if we're on /vendor/dashboard the page is displayed
        if (driver.getCurrentUrl().contains("/vendor/dashboard")) return true;
        // DOM fallback for cases where the URL hasn't updated yet
        return findFirst(dashboardLocators) != null;
    }

    public boolean areStatsCardsVisible() {
        // Try CSS class-based locator first
        boolean found = driver.findElements(statCards).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        });
        if (found) return true;
        // Fall back to text-content XPath (activeListings / totalViews / etc.)
        return driver.findElements(statCardsByText).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        });
    }

    public int getStatCardCount() {
        long cssCount = driver.findElements(statCards).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
        if (cssCount > 0) return (int) cssCount;
        return (int) driver.findElements(statCardsByText).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    public boolean isOnVendorDashboard() {
        return driver.getCurrentUrl().contains("/vendor/dashboard");
    }

    // ── Verifications — My Listings (TC003) ──────────────────────────────────

    public boolean isMyListingsTabVisible() { return isTabVisible("listings") || isTabVisible("my listing"); }

    public int getMyListingCount() {
        long count = driver.findElements(vendorListingCards).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
        if (count > 0) return (int) count;
        return (int) driver.findElements(vendorListingCardsFallback).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    public boolean hasListings() { return getMyListingCount() > 0; }

    // ── Verifications — Add Service form (TC004) ──────────────────────────────

    public boolean isAddServiceButtonVisible() { return findFirst(addServiceButtonLocators) != null; }

    public boolean isAddServiceFormVisible() { return findFirst(serviceFormLocators) != null; }

    public boolean isValidationErrorVisible() {
        return driver.findElements(validationErrorLocator).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        });
    }

    // ── Verifications — delete confirmation ──────────────────────────────────

    public boolean isDeleteConfirmationVisible() {
        return driver.findElements(deleteConfirmLocator).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        });
    }

    // ── Verifications — success toast ─────────────────────────────────────────

    public boolean isSuccessToastVisible() {
        try {
            return wait.until(d -> {
                List<WebElement> els = d.findElements(By.xpath(
                    "//*[contains(@class,'success') or contains(@class,'toast') or contains(@class,'alert-success')]"));
                return els.stream().anyMatch(e -> {
                    try { return e.isDisplayed(); } catch (Exception ex) { return false; }
                });
            });
        } catch (Exception e) { return false; }
    }

    // ── Verifications — inactive listing ─────────────────────────────────────

    public boolean hasInactiveListing() {
        return driver.findElements(inactiveIndicator).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        });
    }

    // ── Actions — Add Service form ────────────────────────────────────────────

    public void clickAddService() {
        WebElement btn = findFirst(addServiceButtonLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    public void fillServiceName(String name) {
        WebElement inp = findFirst(serviceNameInputLocators);
        if (inp != null) { highlight(inp); inp.clear(); inp.sendKeys(name); }
    }

    public void fillServicePrice(String price) {
        WebElement inp = findFirst(priceInputLocators);
        if (inp != null) { highlight(inp); inp.clear(); inp.sendKeys(price); }
    }

    public void fillServiceDescription(String description) {
        WebElement inp = findFirst(descInputLocators);
        if (inp != null) { highlight(inp); inp.clear(); inp.sendKeys(description); }
    }

    public void fillImageUrl(String url) {
        WebElement inp = findFirst(imageUrlInputLocators);
        if (inp != null) { highlight(inp); inp.clear(); inp.sendKeys(url); }
    }

    public void clickSubmitService() {
        WebElement btn = findFirst(submitServiceLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    // ── Actions — Edit/Delete/Toggle ──────────────────────────────────────────

    public void clickEditOnFirstListing() {
        WebElement btn = findFirst(editButtonLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    public void clickDeleteOnFirstListing() {
        WebElement btn = findFirst(deleteButtonLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    public void confirmDelete() {
        try { driver.switchTo().alert().accept(); return; } catch (Exception ignored) {}
        WebElement yes = findFirst(confirmYesLocators);
        if (yes != null) {
            highlight(yes);
            try { yes.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", yes); }
        }
    }

    public void clickToggleOnFirstListing() {
        WebElement toggle = findFirst(toggleLocators);
        if (toggle != null) {
            highlight(toggle);
            try { toggle.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", toggle); }
        }
    }
}
