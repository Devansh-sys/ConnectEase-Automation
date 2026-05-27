package com.cts.connectease.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Page Object — Real-Time Chat Page (/chats)
 * Shows a list of chat sessions on the left and the active conversation on the right.
 */
public class ChatPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Page-ready locators ───────────────────────────────────────────────────
    private final By[] pageReadyLocators = {
            By.cssSelector(".chat-page"),
            By.cssSelector(".chats-container"),
            By.cssSelector("[class*='chat-page']"),
            By.cssSelector("[class*='chats']"),
            By.cssSelector(".sessions-list, .chat-sessions")
    };

    // ── Session list ──────────────────────────────────────────────────────────
    private final By chatSessions = By.cssSelector(
            ".chat-session, .session-item, [class*='chat-session'], [class*='session-item'], .conversation-item");

    private final By noSessionsLocator = By.cssSelector(
            ".no-chats, .empty-chats, [class*='no-chat'], [class*='empty-state']");

    // ── Unread badge ──────────────────────────────────────────────────────────
    private final By unreadBadgeLocator = By.cssSelector(
            ".unread-badge, .badge, .unread-count, [class*='unread-badge'], " +
            "[class*='unread-count'], [class*='badge'], .notification-dot");

    // ── Right-panel / conversation pane ──────────────────────────────────────
    private final By[] conversationPaneLocators = {
            By.cssSelector(".chat-window"),
            By.cssSelector(".conversation-pane"),
            By.cssSelector("[class*='chat-window']"),
            By.cssSelector("[class*='conversation-pane']"),
            By.cssSelector(".messages-panel, .message-panel"),
            By.cssSelector("[class*='messages-panel'], [class*='message-panel']")
    };

    // ── Message input ─────────────────────────────────────────────────────────
    private final By[] messageInputLocators = {
            // Actual class confirmed from Angular bundle
            By.cssSelector(".msg-input"),
            By.cssSelector(".chat-input-bar input"),
            By.cssSelector("input[placeholder*='Type a message' i]"),
            // Generic fallbacks
            By.cssSelector("input[placeholder*='message' i]"),
            By.cssSelector("input[placeholder*='type' i]"),
            By.cssSelector("textarea[placeholder*='message' i]"),
            By.cssSelector(".message-input input"),
            By.cssSelector(".chat-input input"),
            By.cssSelector("[class*='message-input']"),
            By.cssSelector("input[type='text']:last-of-type")
    };

    // ── Send button ────────────────────────────────────────────────────────────
    private final By[] sendButtonLocators = {
            By.cssSelector("button[type='submit']"),
            By.cssSelector(".send-btn"),
            By.cssSelector(".send-button"),
            By.cssSelector("[class*='send']"),
            By.xpath("//button[contains(translate(normalize-space(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'send')]")
    };

    // ── Messages ──────────────────────────────────────────────────────────────
    private final By messages = By.cssSelector(
            // Actual class confirmed from Angular bundle: msg-row (+ [mine] for sent messages)
            ".msg-row, .message, .chat-message, [class*='message-bubble'], [class*='chat-msg'], .msg");

    private final By sentMessages = By.cssSelector(
            ".message.sent, .message.outgoing, [class*='message-sent'], [class*='sent-message'], " +
            "[class*='outgoing'], .my-message, [class*='my-message']");

    private final By receivedMessages = By.cssSelector(
            ".message.received, .message.incoming, [class*='message-received'], [class*='received-message'], " +
            "[class*='incoming'], .other-message, [class*='other-message']");

    // ── Constructor ───────────────────────────────────────────────────────────

    public ChatPage(WebDriver driver) {
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
        driver.get(baseUrl + "/chats");
        wait.until(d -> {
            for (By loc : pageReadyLocators) {
                if (!d.findElements(loc).isEmpty()) return true;
            }
            return !d.findElements(noSessionsLocator).isEmpty();
        });
    }

    // ── Verifications ─────────────────────────────────────────────────────────

    public boolean isPageDisplayed() {
        for (By loc : pageReadyLocators) {
            if (!driver.findElements(loc).isEmpty()) return true;
        }
        return !driver.findElements(noSessionsLocator).isEmpty();
    }

    public int getChatSessionCount() {
        return (int) driver.findElements(chatSessions).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    public boolean hasChatSessions() { return getChatSessionCount() > 0; }

    public boolean isMessageInputVisible() { return findFirst(messageInputLocators) != null; }

    public boolean isSendButtonVisible() { return findFirst(sendButtonLocators) != null; }

    public int getMessageCount() {
        return (int) driver.findElements(messages).stream().filter(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        }).count();
    }

    public boolean isOnChatPage() { return driver.getCurrentUrl().contains("/chats"); }

    /** Returns true if the right-side conversation panel is visible after opening a session. */
    public boolean isConversationPaneVisible() { return findFirst(conversationPaneLocators) != null; }

    /** Returns true if there is at least one element styled as a "sent" bubble. */
    public boolean isSentBubbleVisible() {
        return driver.findElements(sentMessages).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        });
    }

    /** Returns true if there is at least one element styled as a "received" bubble. */
    public boolean isReceivedBubbleVisible() {
        return driver.findElements(receivedMessages).stream().anyMatch(e -> {
            try { return e.isDisplayed(); } catch (Exception ex) { return false; }
        });
    }

    /**
     * Returns true if the message area appears scrolled to the bottom
     * (scrollTop + clientHeight ≥ scrollHeight − 50 px tolerance).
     */
    public boolean isScrolledToLatestMessage() {
        try {
            // Find the scrollable message container
            String[] containers = {
                ".chat-window", ".messages-panel", ".message-panel",
                "[class*='chat-window']", "[class*='messages-panel']",
                ".messages", "[class*='messages']"
            };
            for (String sel : containers) {
                List<WebElement> els = driver.findElements(By.cssSelector(sel));
                for (WebElement el : els) {
                    if (!el.isDisplayed()) continue;
                    Long scrollTop    = (Long) ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollTop;", el);
                    Long scrollHeight = (Long) ((JavascriptExecutor) driver).executeScript("return arguments[0].scrollHeight;", el);
                    Long clientHeight = (Long) ((JavascriptExecutor) driver).executeScript("return arguments[0].clientHeight;", el);
                    if (scrollTop != null && scrollHeight != null && clientHeight != null) {
                        return (scrollTop + clientHeight) >= (scrollHeight - 50);
                    }
                }
            }
        } catch (Exception ignored) {}
        return false;
    }

    /** Returns true if an unread badge is visible on any session in the session list. */
    public boolean isUnreadBadgeVisible() {
        return driver.findElements(unreadBadgeLocator).stream().anyMatch(e -> {
            try { return e.isDisplayed() && !e.getText().trim().isEmpty(); } catch (Exception ex) { return false; }
        });
    }

    /** Returns the text of the first visible unread badge (e.g. "3"), or "" if none found. */
    public String getUnreadBadgeText() {
        List<WebElement> badges = driver.findElements(unreadBadgeLocator);
        for (WebElement b : badges) {
            try {
                if (b.isDisplayed()) {
                    String t = b.getText().trim();
                    if (!t.isEmpty()) return t;
                }
            } catch (Exception ignored) {}
        }
        return "";
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    /** Clicks the first chat session in the list to open the conversation. */
    public void openFirstSession() {
        List<WebElement> sessions = driver.findElements(chatSessions);
        if (!sessions.isEmpty()) {
            WebElement session = sessions.get(0);
            highlight(session);
            try { session.click(); }
            catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", session); }
        }
    }

    /** Types a message in the chat input and sends it via the send button or Enter key. */
    public void sendMessage(String message) {
        WebElement input = findFirst(messageInputLocators);
        if (input != null) {
            highlight(input);
            // CTRL+A selects any existing text then the message replaces it,
            // which fires Angular's (input) event and keeps the form model in sync.
            // input.clear() alone does NOT trigger Angular change detection.
            input.click();
            input.sendKeys(Keys.chord(Keys.CONTROL, "a"), message);

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

    /** Waits briefly, then counts messages to verify one was added. */
    public int getMessageCountAfterSend(int previousCount) {
        try {
            wait.until(d -> {
                long visible = d.findElements(messages).stream().filter(e -> {
                    try { return e.isDisplayed(); } catch (Exception ex) { return false; }
                }).count();
                return visible > previousCount;
            });
        } catch (Exception ignored) {}
        return getMessageCount();
    }
}
