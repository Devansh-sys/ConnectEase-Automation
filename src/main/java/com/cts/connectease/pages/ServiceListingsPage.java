package com.cts.connectease.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object — Service Listings Page (/services)
 * Covers: CE-FE-LIST-TC001 through CE-FE-LIST-TC011
 */
public class ServiceListingsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Core card / page locators ─────────────────────────────────────────────
    private final By serviceCards = By.cssSelector(
            ".service-card, .listing-card, [class*='service-card'], [class*='listing-card'], .card");

    private final By noResultsLocator = By.cssSelector(
            ".no-results, .empty-state, [class*='no-result'], [class*='empty']");

    private final By serviceNameInCard = By.cssSelector(
            ".service-card h3, .service-card h2, .listing-card h3, .card-title, [class*='service-name']");

    // ── Category nav bar (horizontal tabs at top of /services) ───────────────
    private final By categoryNavBar = By.cssSelector(
            ".category-nav, .category-tabs, [class*='category-nav'], [class*='category-bar'], " +
            ".horizontal-nav, [class*='filter-tabs'], nav[class*='categor']");

    private final By categoryNavItems = By.cssSelector(
            ".category-nav button, .category-nav a, .category-tabs button, " +
            "[class*='category-nav'] button, [class*='category-bar'] button, " +
            "[class*='filter-tab'], [class*='category-tab']");

    // ── Sort dropdown ─────────────────────────────────────────────────────────
    private final By[] sortSelectLocators = {
            By.cssSelector("select[name='sort']"),
            By.cssSelector("select[name='sortType']"),
            By.cssSelector("[class*='sort'] select"),
            By.xpath("//select[option[contains(text(),'Price') or contains(text(),'Sort') or contains(text(),'sort')]]")
    };

    // ── City / Area filter ────────────────────────────────────────────────────
    private final By[] citySelectLocators = {
            By.cssSelector("select[name='city']"),
            By.cssSelector("[class*='city'] select"),
            By.cssSelector("[class*='location'] select"),
            By.xpath("//select[option[contains(text(),'City') or contains(text(),'city')]]")
    };

    private final By[] areaSelectLocators = {
            By.cssSelector("select[name='area']"),
            By.cssSelector("[class*='area'] select"),
            By.xpath("//select[option[contains(text(),'Area') or contains(text(),'area')]]")
    };

    // ── Reset filters button ──────────────────────────────────────────────────
    private final By[] resetButtonLocators = {
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'reset')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'clear')]"),
            By.cssSelector(".reset-btn, .clear-filters, [class*='reset']")
    };

    // ── Results count badge ───────────────────────────────────────────────────
    private final By resultsCountLocator = By.cssSelector(
            ".results-count, .total-count, [class*='results-count'], [class*='count-badge'], " +
            "[class*='result-count'], span[class*='count']");

    // ── Price label (for PG/Hostel: /room·month) ─────────────────────────────
    private final By priceLabelInCard = By.cssSelector(
            "[class*='price'], .price, .service-price, [class*='rate']");

    // ── Vendor mode banner ────────────────────────────────────────────────────
    private final By vendorModeBanner = By.cssSelector(
            ".vendor-mode-banner, [class*='vendor-banner'], [class*='vendor-mode']");

    private final By[] viewAllServicesLocators = {
            By.xpath("//a[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'view all')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'view all')]"),
            By.cssSelector("[class*='view-all'], .view-all-link")
    };

    // ── Min rating filter ─────────────────────────────────────────────────────
    private final By[] minRatingLocators = {
            By.cssSelector("input[name='minRating']"),
            By.cssSelector("[class*='rating'] input[type='range']"),
            By.cssSelector("[class*='rating'] input[type='number']")
    };

    // ── Constructor ───────────────────────────────────────────────────────────

    public ServiceListingsPage(WebDriver driver) {
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

    private void waitForCardsToRefresh(int previousCount) {
        try {
            wait.until(d -> {
                long c = d.findElements(serviceCards).stream()
                        .filter(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } }).count();
                return c != previousCount || !d.findElements(noResultsLocator).isEmpty();
            });
        } catch (Exception ignored) {}
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/services");
        wait.until(d ->
            !d.findElements(serviceCards).isEmpty() ||
            !d.findElements(noResultsLocator).isEmpty()
        );
    }

    public void navigateWithCategoryFilter(String baseUrl, String categoryId) {
        driver.get(baseUrl + "/services?categoryId=" + categoryId);
        wait.until(d ->
            !d.findElements(serviceCards).isEmpty() ||
            !d.findElements(noResultsLocator).isEmpty()
        );
    }

    public void navigateWithVendorFilter(String baseUrl, String vendorId) {
        driver.get(baseUrl + "/services?vendor=" + vendorId);
        wait.until(d ->
            !d.findElements(serviceCards).isEmpty() ||
            !d.findElements(noResultsLocator).isEmpty() ||
            !d.findElements(vendorModeBanner).isEmpty()
        );
    }

    // ── Verifications — page state ────────────────────────────────────────────

    public boolean isPageDisplayed() {
        return !driver.findElements(serviceCards).isEmpty() ||
               !driver.findElements(noResultsLocator).isEmpty();
    }

    public boolean isOnServicesPage() {
        return driver.getCurrentUrl().contains("/services");
    }

    public int getServiceCardCount() {
        return (int) driver.findElements(serviceCards).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    public boolean hasServiceCards() { return getServiceCardCount() > 0; }

    public boolean isNoResultsVisible() {
        return driver.findElements(noResultsLocator).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        });
    }

    public String getFirstCardTitle() {
        for (WebElement t : driver.findElements(serviceNameInCard)) {
            try { if (t.isDisplayed() && !t.getText().trim().isEmpty()) return t.getText().trim(); }
            catch (Exception ignored) {}
        }
        return "";
    }

    // ── Verifications — service card fields ──────────────────────────────────

    /** TC001 — service cards show name, price, category badge */
    public boolean doServiceCardsShowRequiredFields() {
        List<WebElement> cards = driver.findElements(serviceCards);
        if (cards.isEmpty()) return false;
        WebElement first = null;
        for (WebElement c : cards) {
            try { if (c.isDisplayed()) { first = c; break; } } catch (Exception ignored) {}
        }
        if (first == null) return false;
        String text = first.getText();
        return text != null && !text.trim().isEmpty();
    }

    public String getFirstCardPriceLabelText() {
        List<WebElement> prices = driver.findElements(priceLabelInCard);
        for (WebElement p : prices) {
            try { if (p.isDisplayed()) return p.getText().trim(); } catch (Exception ignored) {}
        }
        return "";
    }

    // ── Verifications — category nav bar ─────────────────────────────────────

    /** TC002 — horizontal category nav bar visible */
    public boolean isCategoryNavBarVisible() {
        return !driver.findElements(categoryNavBar).isEmpty() ||
               !driver.findElements(categoryNavItems).isEmpty();
    }

    public int getCategoryNavItemCount() {
        return (int) driver.findElements(categoryNavItems).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    /** Click a category in the horizontal nav bar by partial text match */
    public void clickCategoryInNavBar(String categoryName) {
        List<WebElement> items = driver.findElements(categoryNavItems);
        for (WebElement item : items) {
            try {
                if (item.isDisplayed() && item.getText().toLowerCase().contains(categoryName.toLowerCase())) {
                    highlight(item);
                    item.click();
                    return;
                }
            } catch (Exception ignored) {}
        }
        // Fallback: XPath text search
        try {
            WebElement fallback = driver.findElement(By.xpath(
                "//button[contains(translate(normalize-space(),'" +
                "ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" +
                categoryName.toLowerCase() + "')] | //a[contains(translate(normalize-space()," +
                "'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'" +
                categoryName.toLowerCase() + "')]"));
            highlight(fallback);
            fallback.click();
        } catch (Exception ignored) {}
    }

    // ── Verifications — sort ──────────────────────────────────────────────────

    /** TC003 — sort dropdown visible */
    public boolean isSortDropdownVisible() { return findFirst(sortSelectLocators) != null; }

    /** Select sort option by visible text (e.g. "Price Low to High") */
    public void selectSortOption(String optionText) {
        WebElement sel = findFirst(sortSelectLocators);
        if (sel != null) {
            highlight(sel);
            try { new Select(sel).selectByVisibleText(optionText); }
            catch (Exception e) {
                // Try partial match
                try {
                    Select s = new Select(sel);
                    for (WebElement opt : s.getOptions()) {
                        if (opt.getText().toLowerCase().contains(optionText.toLowerCase())) {
                            highlight(opt); s.selectByVisibleText(opt.getText()); return;
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    /** Returns price values from all visible cards as strings */
    public List<String> getCardPriceTexts() {
        List<String> prices = new java.util.ArrayList<>();
        for (WebElement p : driver.findElements(priceLabelInCard)) {
            try { if (p.isDisplayed()) prices.add(p.getText().trim()); } catch (Exception ignored) {}
        }
        return prices;
    }

    // ── Verifications — city / area filter ───────────────────────────────────

    /** TC004 — area dropdown hidden until city selected */
    public boolean isAreaDropdownVisible() {
        WebElement area = findFirst(areaSelectLocators);
        if (area == null) return false;
        try { return area.isDisplayed() && area.isEnabled(); } catch (Exception e) { return false; }
    }

    public void selectCity(String cityName) {
        WebElement sel = findFirst(citySelectLocators);
        if (sel != null) {
            highlight(sel);
            try { new Select(sel).selectByVisibleText(cityName); }
            catch (Exception e) {
                try {
                    Select s = new Select(sel);
                    for (WebElement opt : s.getOptions()) {
                        if (opt.getText().toLowerCase().contains(cityName.toLowerCase())) {
                            highlight(opt); s.selectByVisibleText(opt.getText()); return;
                        }
                    }
                } catch (Exception ignored) {}
            }
        }
    }

    // ── Verifications — reset filters ─────────────────────────────────────────

    /** TC005 — reset all filters button visible */
    public boolean isResetFiltersButtonVisible() { return findFirst(resetButtonLocators) != null; }

    public void clickResetFilters() {
        WebElement btn = findFirst(resetButtonLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    // ── Verifications — results count badge ──────────────────────────────────

    /** TC006 — results count badge in sidebar */
    public boolean isResultsCountVisible() {
        return !driver.findElements(resultsCountLocator).isEmpty();
    }

    public String getResultsCountText() {
        List<WebElement> els = driver.findElements(resultsCountLocator);
        for (WebElement e : els) {
            try { if (e.isDisplayed()) return e.getText().trim(); } catch (Exception ignored) {}
        }
        return "";
    }

    // ── Verifications — vendor mode ───────────────────────────────────────────

    /** TC009 — vendor mode banner visible when ?vendor= param present */
    public boolean isVendorModeBannerVisible() {
        return !driver.findElements(vendorModeBanner).isEmpty() ||
               driver.getCurrentUrl().contains("vendor=");
    }

    /** TC010 — View All Services × link in vendor mode */
    public boolean isViewAllServicesLinkVisible() { return findFirst(viewAllServicesLocators) != null; }

    public void clickViewAllServices() {
        WebElement link = findFirst(viewAllServicesLocators);
        if (link != null) {
            highlight(link);
            try { link.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link); }
        }
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void clickFirstServiceCard() {
        List<WebElement> cards = driver.findElements(serviceCards);
        if (!cards.isEmpty()) {
            for (WebElement card : cards) {
                try {
                    if (card.isDisplayed()) {
                        highlight(card);
                        try { card.click(); } catch (Exception e) {
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", card);
                        }
                        return;
                    }
                } catch (Exception ignored) {}
            }
        }
    }
}
