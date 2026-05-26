package com.cts.connectease.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object — Service Detail Page (/services/{sid})
 * Shows full service info, vendor details, reviews, and a review submission form.
 */
public class ServiceDetailPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    // Actual Angular classes confirmed from bundle: component uses sd-page / sd-wrap / sd-grid
    private final By[] pageReadyLocators = {
            By.cssSelector(".sd-page"),
            By.cssSelector(".sd-wrap"),
            By.cssSelector(".sd-grid"),
            By.cssSelector(".sd-main"),
            By.cssSelector(".info-card"),
            // Generic fallback
            By.cssSelector("h1")
    };

    // Service name is rendered inside .info-header > h1 (confirmed from bundle)
    private final By[] serviceNameLocators = {
            By.cssSelector(".info-header h1"),
            By.cssSelector(".info-card h1"),
            By.cssSelector(".sd-main h1"),
            By.cssSelector("h1"),
            By.cssSelector(".service-name"),
            By.cssSelector("[class*='service-name']")
    };

    private final By[] priceLocators = {
            // Actual Angular classes confirmed from bundle
            By.cssSelector(".price-tag"),
            By.cssSelector(".price-block"),
            // Generic fallbacks
            By.cssSelector(".service-price"),
            By.cssSelector("[class*='price']")
    };

    private final By[] vendorNameLocators = {
            // Actual Angular classes confirmed from bundle
            By.cssSelector(".vendor-line"),
            By.cssSelector(".vendor-card"),
            By.cssSelector(".vendor-avatar"),
            // Generic fallbacks
            By.cssSelector(".vendor-name"),
            By.cssSelector("[class*='vendor-name']"),
            By.cssSelector("[class*='vendor']"),
            By.cssSelector(".provider-name"),
            By.cssSelector("[class*='provider']"),
            By.xpath("//*[starts-with(normalize-space(text()),'By ')]")
    };

    private final By[] reviewSectionLocators = {
            By.cssSelector(".reviews"),
            By.cssSelector(".review-section"),
            By.cssSelector("[class*='review']"),
            By.xpath("//*[contains(@class,'review')]")
    };

    private final By[] reviewFormLocators = {
            By.cssSelector(".review-form"),
            By.cssSelector("form[class*='review']"),
            By.cssSelector("[class*='review-form']")
    };

    private final By[] reviewTextInputLocators = {
            By.cssSelector("textarea[name='review']"),
            By.cssSelector("textarea[name='comment']"),
            By.cssSelector("textarea[name='text']"),
            By.cssSelector("textarea[placeholder*='review' i]"),
            By.cssSelector("textarea[placeholder*='comment' i]"),
            By.cssSelector("textarea")
    };

    private final By[] ratingInputLocators = {
            By.cssSelector("input[name='rating']"),
            By.cssSelector("input[name='score']"),
            By.cssSelector("[class*='star']"),
            By.cssSelector("[class*='rating'] input")
    };

    private final By[] submitReviewLocators = {
            By.cssSelector("form button[type='submit']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submit')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'review')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'post')]")
    };

    private final By reviewCards = By.cssSelector(
            ".review-card, [class*='review-card'], .review-item, [class*='review-item']");

    // ── Chat with Vendor button ───────────────────────────────────────────────
    // Actual class confirmed from Angular bundle: .btn-chat  Text: "💬 Chat with Vendor"
    private final By[] chatButtonLocators = {
            By.cssSelector(".btn-chat"),
            By.cssSelector("[class*='btn-chat']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'chat with vendor')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'chat')]"),
            By.cssSelector(".chat-vendor-btn, [class*='chat-with-vendor']")
    };

    // ── View count (CE-FE-SERV-TC007) ────────────────────────────────────────
    private final By viewCountLocator = By.cssSelector(
            ".view-count, [class*='view-count'], [class*='views'], .views-count, " +
            "[class*='view'] span, [class*='views'] span");

    // ── Vendor card / "See all their services" (CE-FE-SERV-TC008) ────────────
    private final By[] vendorCardLocators = {
            By.cssSelector(".vendor-card"),
            By.cssSelector("[class*='vendor-card']"),
            By.cssSelector(".provider-card"),
            By.cssSelector(".service-provider-card")
    };

    private final By[] seeAllVendorServicesLocators = {
            By.xpath("//a[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'see all')]"),
            By.xpath("//a[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'all services')]"),
            By.cssSelector("[class*='see-all'], [class*='all-services']")
    };

    // ── Image gallery thumbnails (CE-FE-SERV-TC002) ───────────────────────────
    private final By thumbnails = By.cssSelector(
            ".thumbnail, [class*='thumbnail'], .gallery-thumb, [class*='gallery'] img[class*='thumb']");

    private final By primaryImage = By.cssSelector(
            ".primary-image img, .main-image img, [class*='primary-image'] img, " +
            "[class*='main-image'] img, .service-image img");

    // ── Constructor ───────────────────────────────────────────────────────────

    public ServiceDetailPage(WebDriver driver) {
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

    public void navigateTo(String baseUrl, String sid) {
        driver.get(baseUrl + "/services/" + sid);
        // pageReadyLocators now includes .sd-page which renders immediately (even during loading)
        wait.until(d -> {
            for (By loc : pageReadyLocators) {
                if (!d.findElements(loc).isEmpty()) return true;
            }
            return false;
        });
    }

    // ── Verifications ─────────────────────────────────────────────────────────

    public boolean isPageDisplayed() {
        return findFirst(pageReadyLocators) != null ||
               !driver.findElements(By.cssSelector("h1")).isEmpty();
    }

    public String getServiceName() {
        WebElement el = findFirst(serviceNameLocators);
        return el != null ? el.getText().trim() : "";
    }

    public boolean isPriceVisible() {
        return findFirst(priceLocators) != null;
    }

    public boolean isVendorNameVisible() {
        // First check without scrolling
        if (findFirst(vendorNameLocators) != null) return true;
        // Scroll halfway down — vendor info is often below the service title
        try {
            ((JavascriptExecutor) driver)
                    .executeScript("window.scrollTo(0, document.body.scrollHeight / 2)");
        } catch (Exception ignored) {}
        return findFirst(vendorNameLocators) != null;
    }

    public boolean isReviewSectionVisible() {
        return findFirst(reviewSectionLocators) != null;
    }

    public boolean isReviewFormVisible() {
        return findFirst(reviewFormLocators) != null;
    }

    public int getReviewCount() {
        List<WebElement> reviews = driver.findElements(reviewCards);
        return (int) reviews.stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    public boolean isOnServiceDetailPage() {
        return driver.getCurrentUrl().contains("/services/");
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void submitReview(String reviewText) {
        WebElement textArea = findFirst(reviewTextInputLocators);
        if (textArea != null) {
            highlight(textArea);
            textArea.clear();
            textArea.sendKeys(reviewText);
        }

        // Try to click a star rating (click the 5th star for max)
        List<WebElement> stars = driver.findElements(By.cssSelector("[class*='star'], [class*='rating'] span"));
        if (stars.size() >= 5) {
            WebElement fifthStar = stars.get(4);
            highlight(fifthStar);
            try { fifthStar.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", fifthStar); }
        }

        WebElement submitBtn = findFirst(submitReviewLocators);
        if (submitBtn != null) {
            highlight(submitBtn);
            try { submitBtn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", submitBtn); }
        }
    }

    // ── Chat / vendor card / view count ──────────────────────────────────────

    public boolean isChatWithVendorButtonVisible() { return findFirst(chatButtonLocators) != null; }

    public void clickChatWithVendor() {
        WebElement btn = findFirst(chatButtonLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    public String getViewCountText() {
        // Scroll first — view count is often in the sidebar
        try { ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 400);"); } catch (Exception ignored) {}
        List<WebElement> els = driver.findElements(viewCountLocator);
        for (WebElement e : els) {
            try { if (e.isDisplayed()) return e.getText().trim(); } catch (Exception ignored) {}
        }
        return "";
    }

    public boolean isViewCountVisible() { return !driver.findElements(viewCountLocator).isEmpty(); }

    public boolean isVendorCardVisible() {
        // Try primary vendor card locators
        WebElement el = findFirst(vendorCardLocators);
        if (el != null) return true;
        // Fall back to the broader vendor name check
        return isVendorNameVisible();
    }

    public boolean isSeeAllVendorServicesLinkVisible() { return findFirst(seeAllVendorServicesLocators) != null; }

    public void clickSeeAllVendorServices() {
        WebElement link = findFirst(seeAllVendorServicesLocators);
        if (link != null) {
            highlight(link);
            try { link.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", link); }
        }
    }

    // ── Image gallery ─────────────────────────────────────────────────────────

    public boolean isImageGalleryVisible() {
        return !driver.findElements(thumbnails).isEmpty() ||
               !driver.findElements(primaryImage).isEmpty();
    }

    public int getThumbnailCount() {
        return (int) driver.findElements(thumbnails).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    public void clickThumbnailByIndex(int index) {
        List<WebElement> thumbs = driver.findElements(thumbnails);
        if (thumbs.size() > index) {
            WebElement thumb = thumbs.get(index);
            highlight(thumb);
            try { thumb.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", thumb); }
        }
    }

    // ── Price label text ──────────────────────────────────────────────────────

    public String getPriceLabelText() {
        WebElement el = findFirst(priceLocators);
        return el != null ? el.getText().trim() : "";
    }

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
}
