package com.cts.connectease.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object — Community Forum Page (/community)
 * Lists posts; authenticated users can create, edit, and delete their own posts.
 */
public class CommunityForumPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Post cards ────────────────────────────────────────────────────────────
    private final By postCards = By.cssSelector(
            ".post-card, .community-post, [class*='post-card'], [class*='post-item'], .forum-post");

    private final By noPostsLocator = By.cssSelector(
            ".no-posts, .empty-state, [class*='no-post'], [class*='empty']");

    // ── Create post button ────────────────────────────────────────────────────
    private final By[] createPostButtonLocators = {
            // Actual class confirmed from Angular bundle
            By.cssSelector(".btn-post"),
            By.cssSelector("[class*='btn-post']"),
            // Text-based — button shows "+ Write a Post"
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'write a post')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'create post')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'new post')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'add post')]"),
            By.xpath("//a[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'create')]"),
            By.cssSelector(".create-post-btn, .new-post-btn, [class*='create-post'], [class*='write-post']")
    };

    // ── Post form / modal ─────────────────────────────────────────────────────
    private final By[] postFormLocators = {
            By.cssSelector(".post-form"),
            By.cssSelector("form[class*='post']"),
            By.cssSelector("[class*='post-form']"),
            By.cssSelector(".modal form"),
            By.cssSelector("form")
    };

    private final By[] postTitleInputLocators = {
            // Actual placeholder confirmed from Angular bundle
            By.cssSelector("input[placeholder*='What' i][placeholder*='mind' i]"),
            By.cssSelector("input[maxlength='200']"),
            // Generic fallbacks
            By.cssSelector("input[name='title']"),
            By.cssSelector("input[placeholder*='title' i]"),
            By.cssSelector("input[placeholder*='subject' i]")
    };

    private final By[] postDescInputLocators = {
            // Actual placeholder confirmed from Angular bundle
            By.cssSelector("textarea[placeholder*='Share your experience' i]"),
            By.cssSelector("textarea[placeholder*='ask a question' i]"),
            // Generic fallbacks
            By.cssSelector("textarea[name='description']"),
            By.cssSelector("textarea[name='content']"),
            By.cssSelector("textarea[placeholder*='description' i]"),
            By.cssSelector("textarea[placeholder*='content' i]"),
            By.cssSelector("textarea")
    };

    private final By[] postCategoryLocators = {
            By.cssSelector("input[name='category']"),
            By.cssSelector("select[name='category']"),
            By.cssSelector("input[placeholder*='category' i]"),
            By.xpath("//input[contains(@placeholder,'category') or contains(@name,'category')]")
    };

    private final By[] submitPostLocators = {
            // Actual class confirmed from Angular bundle — NOT type='submit'
            By.cssSelector(".btn-submit"),
            By.cssSelector("[class*='btn-submit']"),
            // Text-based fallbacks (button shows "🚀 Post" when creating)
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'post')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'submit')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'publish')]"),
            By.cssSelector("form button[type='submit']")
    };

    // ── Edit / Delete buttons ─────────────────────────────────────────────────
    private final By[] editButtonLocators = {
            By.cssSelector(".edit-btn, .btn-edit, [class*='edit-post']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'edit')]")
    };

    private final By[] deleteButtonLocators = {
            By.cssSelector(".delete-btn, .btn-delete, [class*='delete-post']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'delete')]")
    };

    // ── Confirmation dialog ───────────────────────────────────────────────────
    private final By[] confirmLocators = {
            By.cssSelector(".confirm-dialog, .modal [class*='confirm'], [class*='confirm-modal']"),
            By.xpath("//div[contains(@class,'modal') and .//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'confirm')]]"),
            By.xpath("//div[contains(@class,'modal') and .//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'yes')]]")
    };

    // ── Category filter chips / bubbles ───────────────────────────────────────
    // XPath text-based locators match the actual button labels shown in the FRD
    // (All Posts, General, PG/Hostel, Food Service, Electrician, Plumber, Cleaners, Laundry)
    // regardless of the Angular CSS class name used.
    private final By[] categoryFilterLocators = {
            // Text-content based — most reliable regardless of CSS class
            By.xpath("//button[contains(normalize-space(),'All Posts')]"),
            By.xpath("//button[contains(normalize-space(),'General') or contains(normalize-space(),'PG/Hostel') or contains(normalize-space(),'Food Service')]"),
            By.xpath("//button[contains(normalize-space(),'Electrician') or contains(normalize-space(),'Plumber') or contains(normalize-space(),'Laundry')]"),
            // Buttons that follow a "Filter" label (FRD: "Filter by category:")
            By.xpath("//*[contains(normalize-space(),'Filter by category')]/following-sibling::*//button"),
            By.xpath("//*[contains(normalize-space(),'Filter by category')]//following-sibling::button"),
            // Class-based fallbacks
            By.cssSelector(".category-filter, .category-chip, [class*='category-filter'], [class*='category-chip']"),
            By.cssSelector(".filter-btn, .tag-filter, [class*='filter-tag'], [class*='filter-btn']"),
            By.cssSelector("button[class*='category'], a[class*='category']"),
            By.cssSelector("[class*='active'][class*='filter'], [class*='active'][class*='category']")
    };

    // ── Post card content ─────────────────────────────────────────────────────
    private final By postCardTitleLocator = By.cssSelector(
            ".post-card h2, .post-card h3, .post-card .title, " +
            "[class*='post-card'] h2, [class*='post-card'] h3, [class*='post-card'] .title, " +
            "[class*='post-item'] h2, [class*='post-item'] .title");

    private final By postCardAuthorLocator = By.cssSelector(
            ".post-card .author, .post-card [class*='author'], .post-card [class*='user'], " +
            "[class*='post-card'] .author, [class*='post-card'] [class*='author']");

    private final By postCardImageLocator = By.cssSelector(
            ".post-card img, [class*='post-card'] img, [class*='post-item'] img");

    // ── Constructor ───────────────────────────────────────────────────────────

    public CommunityForumPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ── Visual helper ─────────────────────────────────────────────────────────

    private void highlight(WebElement el) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript("arguments[0].style.outline='3px solid #f59e0b';arguments[0].style.backgroundColor='#fef9c3';", el);
            Thread.sleep(400);
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
        driver.get(baseUrl + "/community");
        wait.until(d ->
            !d.findElements(postCards).isEmpty() ||
            !d.findElements(noPostsLocator).isEmpty() ||
            !d.findElements(By.cssSelector("h1, h2")).isEmpty()
        );
    }

    // ── Verifications ─────────────────────────────────────────────────────────

    public boolean isPageDisplayed() {
        return !driver.findElements(postCards).isEmpty() ||
               !driver.findElements(noPostsLocator).isEmpty() ||
               !driver.findElements(By.cssSelector("h1, h2")).isEmpty();
    }

    public int getPostCount() {
        return (int) driver.findElements(postCards).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    public boolean hasPostCards() { return getPostCount() > 0; }

    public boolean isCreatePostButtonVisible() { return findFirst(createPostButtonLocators) != null; }

    public boolean isPostFormVisible() { return findFirst(postFormLocators) != null; }

    public boolean isOnCommunityPage() { return driver.getCurrentUrl().contains("/community"); }

    /** Returns true if at least one category filter chip/button is visible. */
    public boolean isCategoryFilterVisible() { return findFirst(categoryFilterLocators) != null; }

    /**
     * Clicks a category filter chip whose label text contains {@code name}.
     * Falls back to clicking the first filter chip if none matches.
     */
    public void clickCategoryFilter(String name) {
        String lower = name.toLowerCase();
        for (By loc : categoryFilterLocators) {
            try {
                List<WebElement> chips = driver.findElements(loc);
                for (WebElement chip : chips) {
                    if (chip.isDisplayed() && chip.getText().toLowerCase().contains(lower)) {
                        highlight(chip);
                        try { chip.click(); }
                        catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", chip); }
                        return;
                    }
                }
            } catch (Exception ignored) {}
        }
        // fallback — click first visible chip
        WebElement first = findFirst(categoryFilterLocators);
        if (first != null) {
            highlight(first);
            try { first.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", first); }
        }
    }

    /** Returns the title text of the first visible post card, or empty string. */
    public String getFirstPostCardTitle() {
        List<WebElement> titles = driver.findElements(postCardTitleLocator);
        for (WebElement t : titles) {
            try { if (t.isDisplayed()) return t.getText().trim(); } catch (Exception ignored) {}
        }
        // Fallback: first post card's text
        List<WebElement> cards = driver.findElements(postCards);
        if (!cards.isEmpty()) {
            try { return cards.get(0).getText().trim(); } catch (Exception ignored) {}
        }
        return "";
    }

    /** Returns true if the first post card shows an author/user name element. */
    public boolean isFirstPostCardAuthorVisible() {
        List<WebElement> authors = driver.findElements(postCardAuthorLocator);
        return authors.stream().anyMatch(e -> {
            try { return e.isDisplayed() && !e.getText().trim().isEmpty(); } catch (Exception ex) { return false; }
        });
    }

    /** Returns true if the first post card contains an image element. */
    public boolean isFirstPostCardImageVisible() {
        List<WebElement> imgs = driver.findElements(postCardImageLocator);
        return imgs.stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        });
    }

    /** Returns true if any Edit button is visible (own posts). */
    public boolean isEditButtonVisible() { return findFirst(editButtonLocators) != null; }

    /** Returns true if any Delete button is visible (own posts). */
    public boolean isDeleteButtonVisible() { return findFirst(deleteButtonLocators) != null; }

    /** Returns the current value inside the title input of the post form (for edit pre-fill check). */
    public String getPostFormTitleValue() {
        WebElement titleInput = findFirst(postTitleInputLocators);
        if (titleInput != null) {
            String val = titleInput.getAttribute("value");
            return val != null ? val.trim() : "";
        }
        return "";
    }

    /** Returns true if a delete confirmation dialog/modal is visible. */
    public boolean isDeleteConfirmationVisible() {
        // Check for native alert first
        try {
            driver.switchTo().alert();
            driver.switchTo().defaultContent();
            return true;
        } catch (Exception ignored) {}
        return findFirst(confirmLocators) != null;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void clickCreatePost() {
        WebElement btn = findFirst(createPostButtonLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    public void fillPostForm(String title, String description, String category) {
        WebElement titleInput = findFirst(postTitleInputLocators);
        if (titleInput != null) { highlight(titleInput); titleInput.clear(); titleInput.sendKeys(title); }

        WebElement descInput = findFirst(postDescInputLocators);
        if (descInput != null) { highlight(descInput); descInput.clear(); descInput.sendKeys(description); }

        WebElement catInput = findFirst(postCategoryLocators);
        if (catInput != null) { highlight(catInput); catInput.clear(); catInput.sendKeys(category); }
    }

    public void submitPost() {
        WebElement btn = findFirst(submitPostLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    /** Submit the post form with empty title to trigger mandatory-field validation. */
    public void submitEmptyPostForm() {
        WebElement btn = findFirst(submitPostLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    /** Click edit on the first visible post card. */
    public void clickEditOnFirstPost() {
        WebElement editBtn = findFirst(editButtonLocators);
        if (editBtn != null) {
            highlight(editBtn);
            try { editBtn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", editBtn); }
        }
    }

    /** Click delete on the first visible post card. */
    public void clickDeleteOnFirstPost() {
        WebElement deleteBtn = findFirst(deleteButtonLocators);
        if (deleteBtn != null) {
            highlight(deleteBtn);
            try { deleteBtn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", deleteBtn); }
        }
    }

    public boolean isSuccessMessageVisible() {
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

    /** Accept any confirmation dialog (native alert or modal confirm button). */
    public void confirmDeletion() {
        try {
            driver.switchTo().alert().accept();
        } catch (Exception e) {
            WebElement confirmBtn = findFirst(new By[]{
                By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'confirm')]"),
                By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'yes')]"),
                By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'ok')]")
            });
            if (confirmBtn != null) {
                highlight(confirmBtn);
                confirmBtn.click();
            }
        }
    }
}
