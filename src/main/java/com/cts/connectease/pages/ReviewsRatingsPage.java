package com.cts.connectease.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object — Reviews & Ratings section (embedded in /services/{sid})
 * Covers: CE-FE-REV-TC001 through CE-FE-REV-TC008
 *
 * This is not a standalone page — it is a section within the Service Detail page.
 * Navigate to a service detail page first, then use these methods.
 */
public class ReviewsRatingsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Reviews list ──────────────────────────────────────────────────────────
    private final By reviewCards = By.cssSelector(
            ".review-card, [class*='review-card'], .review-item, [class*='review-item'], " +
            ".review, [class*='reviews'] > div, [class*='review-list'] > div");

    private final By noReviewsLocator = By.cssSelector(
            ".no-reviews, [class*='no-review'], [class*='empty-reviews'], " +
            "[class*='no-ratings']");

    private final By reviewerNameLocator = By.cssSelector(
            ".reviewer-name, [class*='reviewer'], .review-author, [class*='review-user']");

    private final By reviewDateLocator = By.cssSelector(
            ".review-date, [class*='review-date'], .review-time, time");

    private final By reviewTextLocator = By.cssSelector(
            ".review-text, [class*='review-text'], .review-comment, [class*='comment']");

    private final By reviewRatingStars = By.cssSelector(
            ".review-stars, [class*='review-star'], [class*='star-rating'], .rating-stars");

    // ── Average rating (sidebar or header) ───────────────────────────────────
    private final By averageRatingLocator = By.cssSelector(
            ".average-rating, [class*='avg-rating'], [class*='average-rating'], " +
            ".overall-rating, [class*='overall-rating']");

    // ── Star picker (submit form) ─────────────────────────────────────────────
    private final By starPickerStars = By.cssSelector(
            ".star-picker .star, [class*='star-picker'] span, [class*='star-input'] span, " +
            ".rating-input .star, [class*='rating-picker'] [class*='star'], " +
            "label[class*='star'], [class*='interactive-star']");

    private final By starPickerLabel = By.cssSelector(
            ".star-label, [class*='star-label'], .rating-label, [class*='rating-label']");

    // ── Review submission form ────────────────────────────────────────────────
    private final By[] reviewFormLocators = {
            By.cssSelector(".review-form"),
            By.cssSelector("form[class*='review']"),
            By.cssSelector("[class*='review-form']"),
            By.cssSelector("[class*='add-review']"),
            By.cssSelector("[class*='write-review']")
    };

    private final By[] reviewTextInputLocators = {
            By.cssSelector("textarea[name='review']"),
            By.cssSelector("textarea[name='comment']"),
            By.cssSelector("textarea[name='text']"),
            By.cssSelector("textarea[placeholder*='review' i]"),
            By.cssSelector("textarea[placeholder*='comment' i]"),
            By.cssSelector(".review-form textarea"),
            By.cssSelector("textarea")
    };

    private final By[] submitReviewLocators = {
            By.cssSelector(".review-form button[type='submit']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submit review')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submit')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'post review')]")
    };

    // ── Delete review button ──────────────────────────────────────────────────
    private final By[] deleteReviewLocators = {
            By.cssSelector(".review-card .delete-btn, .review-item .delete-btn"),
            By.cssSelector("[class*='review-delete']"),
            By.xpath("//div[contains(@class,'review')]//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'delete')]")
    };

    // ── Constructor ───────────────────────────────────────────────────────────

    public ReviewsRatingsPage(WebDriver driver) {
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

    // ── Scroll helper — reviews are often below the fold ─────────────────────

    public void scrollToReviews() {
        try {
            WebElement reviewSection = driver.findElement(
                By.cssSelector(".reviews, .review-section, [class*='review']"));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", reviewSection);
        } catch (Exception e) {
            try { ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight * 0.7);"); }
            catch (Exception ignored) {}
        }
    }

    // ── Verifications — TC001: reviews list ──────────────────────────────────

    public int getReviewCount() {
        return (int) driver.findElements(reviewCards).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    public boolean hasReviews() { return getReviewCount() > 0; }

    public boolean isReviewerNameVisible() {
        return !driver.findElements(reviewerNameLocator).isEmpty();
    }

    public boolean isReviewTextVisible() {
        return driver.findElements(reviewTextLocator).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        });
    }

    public boolean isReviewRatingStarsVisible() {
        return !driver.findElements(reviewRatingStars).isEmpty();
    }

    // ── Verifications — TC002: no reviews empty state ────────────────────────

    public boolean isNoReviewsMessageVisible() {
        List<WebElement> els = driver.findElements(noReviewsLocator);
        if (els.stream().anyMatch(e -> { try { return e.isDisplayed(); } catch (Exception ex) { return false; } }))
            return true;
        // Fallback: text scan
        try {
            return !driver.findElements(By.xpath(
                "//*[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'no review') or " +
                "contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'be the first')]")).isEmpty();
        } catch (Exception e) { return false; }
    }

    // ── Verifications — TC003: star picker ───────────────────────────────────

    public boolean isStarPickerVisible() { return !driver.findElements(starPickerStars).isEmpty(); }

    public int getStarPickerCount() {
        return (int) driver.findElements(starPickerStars).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    /** Hover over nth star (1-based) */
    public void hoverStar(int starIndex) {
        List<WebElement> stars = driver.findElements(starPickerStars);
        if (stars.size() >= starIndex) {
            WebElement star = stars.get(starIndex - 1);
            highlight(star);
            try {
                new org.openqa.selenium.interactions.Actions(driver).moveToElement(star).perform();
            } catch (Exception ignored) {}
        }
    }

    /** Click nth star to lock the rating (1-based) */
    public void clickStar(int starIndex) {
        List<WebElement> stars = driver.findElements(starPickerStars);
        if (stars.size() >= starIndex) {
            WebElement star = stars.get(starIndex - 1);
            highlight(star);
            try { star.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", star); }
        }
    }

    public boolean isStarLabelVisible() {
        return !driver.findElements(starPickerLabel).isEmpty();
    }

    // ── Page-level check (the reviews section lives inside /services/{sid}) ───

    /**
     * Returns true if the browser is still on a service detail page
     * (URL contains "/services/") and at least one structural element is present.
     */
    public boolean isPageDisplayed() {
        if (!driver.getCurrentUrl().contains("/services/")) return false;
        return !driver.findElements(By.cssSelector("h1, .service-detail, [class*='service-detail']")).isEmpty();
    }

    // ── Verifications — TC003 extras ─────────────────────────────────────────

    /**
     * Counts stars that are visually marked as selected/active after a click.
     * Looks for common CSS conventions: 'active', 'selected', 'filled', 'checked'.
     */
    public int getSelectedStarCount() {
        By selectedStars = By.cssSelector(
            "[class*='star'][class*='active'], [class*='star'][class*='selected'], " +
            "[class*='star'][class*='filled'], [class*='star'][class*='checked'], " +
            "[class*='star-active'], [class*='star-filled'], [class*='star-selected'], " +
            "label.active[class*='star'], label.checked[class*='star']");
        return (int) driver.findElements(selectedStars).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    /** Returns true if a text label describing the selected rating is visible (e.g. "Good", "Excellent"). */
    public boolean isRatingLabelVisible() { return isStarLabelVisible(); }

    // ── Verifications — TC004: submit button disabled ────────────────────────

    /** Returns true if the review text textarea is present and visible. */
    public boolean isReviewTextInputVisible() { return findFirst(reviewTextInputLocators) != null; }

    public boolean isReviewFormVisible() { return findFirst(reviewFormLocators) != null; }

    public boolean isSubmitReviewButtonEnabled() {
        WebElement btn = findFirst(submitReviewLocators);
        if (btn == null) return false;
        try { return btn.isEnabled(); } catch (Exception e) { return false; }
    }

    // ── Verifications — TC007: delete button on own reviews ──────────────────

    public boolean isDeleteReviewButtonVisible() {
        return findFirst(deleteReviewLocators) != null;
    }

    // ── Verifications — TC008: average rating ────────────────────────────────

    public String getAverageRatingText() {
        List<WebElement> els = driver.findElements(averageRatingLocator);
        for (WebElement e : els) {
            try { if (e.isDisplayed()) return e.getText().trim(); } catch (Exception ignored) {}
        }
        return "";
    }

    public boolean isAverageRatingVisible() {
        return !driver.findElements(averageRatingLocator).isEmpty();
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void enterReviewText(String text) {
        WebElement ta = findFirst(reviewTextInputLocators);
        if (ta != null) { highlight(ta); ta.clear(); ta.sendKeys(text); }
    }

    public void submitReview(int starRating, String text) {
        scrollToReviews();
        clickStar(starRating);
        enterReviewText(text);
        WebElement btn = findFirst(submitReviewLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    public void deleteFirstOwnReview() {
        WebElement btn = findFirst(deleteReviewLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    /** Alias used by TC008 — clicks the delete button on the first own review. */
    public void clickDeleteFirstOwnReview() { deleteFirstOwnReview(); }

    // ── Toast verifications ───────────────────────────────────────────────────

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

    public boolean isFormResetAfterSubmit() {
        WebElement ta = findFirst(reviewTextInputLocators);
        if (ta == null) return true;
        try { return ta.getAttribute("value") == null || ta.getAttribute("value").isEmpty(); }
        catch (Exception e) { return false; }
    }

}
