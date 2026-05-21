package com.cts.connectease.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Page Object — AI Chat Page (/ai-chat)
 * A conversational interface: the user types a query and the AI responds with
 * service recommendations or category information.
 */
public class AIChatPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Locators ──────────────────────────────────────────────────────────────
    private final By[] pageReadyLocators = {
            By.cssSelector(".ai-chat"),
            By.cssSelector(".ai-chat-page"),
            By.cssSelector("[class*='ai-chat']"),
            By.cssSelector(".chat-container"),
            By.cssSelector("[class*='chat-container']")
    };

    private final By[] queryInputLocators = {
            By.cssSelector("input[placeholder*='ask' i]"),
            By.cssSelector("input[placeholder*='type' i]"),
            By.cssSelector("input[placeholder*='search' i]"),
            By.cssSelector("input[placeholder*='message' i]"),
            By.cssSelector("input[placeholder*='question' i]"),
            By.cssSelector("textarea[placeholder*='ask' i]"),
            By.cssSelector("textarea[placeholder*='message' i]"),
            By.cssSelector(".chat-input input"),
            By.cssSelector(".ai-input input"),
            By.cssSelector("[class*='query'] input"),
            By.cssSelector("input[type='text']"),
            By.cssSelector("textarea")
    };

    private final By[] sendButtonLocators = {
            By.cssSelector("button[type='submit']"),
            By.cssSelector(".send-btn"),
            By.cssSelector("[class*='send']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'send')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'ask')]"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'go')]")
    };

    private final By aiResponseMessages = By.cssSelector(
            ".ai-message, .bot-message, [class*='ai-message'], [class*='bot-message'], " +
            ".response, [class*='response'], .chat-bubble");

    private final By loadingIndicator = By.cssSelector(
            ".loading, .spinner, [class*='loading'], [class*='typing'], [class*='spinner']");

    private final By serviceCards = By.cssSelector(
            ".service-card, .result-card, [class*='service-card'], [class*='result-card']");

    // ── Constructor ───────────────────────────────────────────────────────────

    public AIChatPage(WebDriver driver) {
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
        driver.get(baseUrl + "/ai-chat");
        wait.until(d -> {
            for (By loc : pageReadyLocators) {
                if (!d.findElements(loc).isEmpty()) return true;
            }
            return !d.findElements(By.cssSelector("input, textarea")).isEmpty();
        });
    }

    // ── Verifications ─────────────────────────────────────────────────────────

    public boolean isPageDisplayed() {
        for (By loc : pageReadyLocators) {
            if (!driver.findElements(loc).isEmpty()) return true;
        }
        return !driver.findElements(By.cssSelector("input, textarea")).isEmpty();
    }

    public boolean isQueryInputVisible()  { return findFirst(queryInputLocators) != null; }
    public boolean isSendButtonVisible()  { return findFirst(sendButtonLocators) != null; }
    public boolean isOnAiChatPage()       { return driver.getCurrentUrl().contains("/ai-chat"); }

    /** Returns true if the AI chat assistant panel / frame is visible. */
    public boolean isAiAssistantFrameVisible() {
        By[] locators = {
            By.cssSelector("[class*='ai-assistant' i], [class*='assistant-panel' i], [class*='chat-panel' i]"),
            By.cssSelector(".chat-container, [class*='chat-container']"),
            By.cssSelector("[class*='ai-chat' i]")
        };
        for (By loc : locators) {
            try { if (!driver.findElements(loc).isEmpty()) return true; } catch (Exception ignored) {}
        }
        return isPageDisplayed();
    }

    /** Returns true if the Smart Recommendations panel is visible. */
    public boolean isRecommendationsFrameVisible() {
        By[] locators = {
            By.cssSelector("[class*='recommendation' i], [class*='smart-rec' i], [class*='results-panel' i]"),
            By.cssSelector(".service-card, [class*='service-card'], [class*='result-card']"),
            By.cssSelector("[class*='suggestion' i]")
        };
        for (By loc : locators) {
            try { if (!driver.findElements(loc).isEmpty()) return true; } catch (Exception ignored) {}
        }
        return false;
    }

    /** Returns the chat input WebElement (or null if not found). */
    public WebElement getChatInput() { return findFirst(queryInputLocators); }

    /** Clicks the Send button, falling back to JS click if needed. */
    public void clickSendButton() {
        WebElement btn = findFirst(sendButtonLocators);
        if (btn != null) {
            highlight(btn);
            try { btn.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
        }
    }

    /** Alias for {@link #askQuestion(String)} — types and submits a query. */
    public void submitQuery(String query) { askQuestion(query); }

    public int getAiResponseCount() {
        return (int) driver.findElements(aiResponseMessages).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    public boolean hasServiceResultCards() {
        return driver.findElements(serviceCards).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        });
    }

    /**
     * Returns the full text of the LAST (most recent) visible AI response bubble.
     * Falls back through alternative CSS patterns before giving up.
     * Returns an empty string when no response is visible yet.
     */
    public String getLastAiResponseText() {
        // Walk all matching elements; keep the last one that is visible
        List<WebElement> responses = driver.findElements(aiResponseMessages);
        WebElement lastVisible = null;
        for (WebElement e : responses) {
            try { if (e.isDisplayed()) lastVisible = e; } catch (Exception ignored) {}
        }

        // Fallback locators when the primary set matches nothing
        if (lastVisible == null) {
            By[] fallbacks = {
                By.cssSelector("[class*='message']:not(input):not(textarea)"),
                By.cssSelector("[class*='answer'], [class*='reply']"),
                By.cssSelector("[class*='assistant' i], [class*='ai-text' i]")
            };
            outer:
            for (By loc : fallbacks) {
                for (WebElement e : driver.findElements(loc)) {
                    try {
                        if (e.isDisplayed() && !e.getText().trim().isEmpty()) {
                            lastVisible = e;
                            break outer;
                        }
                    } catch (Exception ignored) {}
                }
            }
        }

        if (lastVisible == null) return "";
        try { return lastVisible.getText().trim(); } catch (Exception e) { return ""; }
    }

    /**
     * Returns the text of ALL currently visible AI response messages in order.
     * Useful for multi-turn conversation assertions.
     */
    public List<String> getAllAiResponseTexts() {
        List<String> texts = new ArrayList<>();
        for (WebElement e : driver.findElements(aiResponseMessages)) {
            try {
                if (e.isDisplayed()) {
                    String t = e.getText().trim();
                    if (!t.isEmpty()) texts.add(t);
                }
            } catch (Exception ignored) {}
        }
        return texts;
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    /** Types a question and clicks Send (or presses Enter if no Send button found). */
    public void askQuestion(String question) {
        WebElement input = findFirst(queryInputLocators);
        if (input != null) {
            highlight(input);
            input.clear();
            input.sendKeys(question);

            WebElement sendBtn = findFirst(sendButtonLocators);
            if (sendBtn != null) {
                highlight(sendBtn);
                try { sendBtn.click(); }
                catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", sendBtn); }
            } else {
                input.sendKeys(Keys.RETURN);
            }
        }
    }

    /**
     * Waits up to 30 s for an AI response to appear (AI calls can be slow).
     * Returns true if at least one new response message appeared.
     */
    public boolean waitForAiResponse(int previousCount) {
        try {
            new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
                List<WebElement> spinners = d.findElements(loadingIndicator);
                boolean loading = spinners.stream().anyMatch(e -> {
                    try { return e.isDisplayed(); } catch (Exception ex) { return false; }
                });
                if (loading) return false;

                long count = d.findElements(aiResponseMessages).stream().filter(e -> {
                    try { return e.isDisplayed(); } catch (Exception ex) { return false; }
                }).count();
                return count > previousCount;
            });
            return true;
        } catch (Exception e) { return false; }
    }
}
