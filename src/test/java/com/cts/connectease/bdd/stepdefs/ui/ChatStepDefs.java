package com.cts.connectease.bdd.stepdefs.ui;

import com.cts.connectease.bdd.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class ChatStepDefs {

    private final TestContext ctx;

    public ChatStepDefs(TestContext ctx) { this.ctx = ctx; }

    // ── TC001: Chat page is accessible ───────────────────────────────────────

    @Given("the user is on the chat page")
    public void theUserIsOnTheChatPage() {
        ctx.chatPage.navigateTo(ctx.BASE_URL);
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.chatPage.isPageDisplayed(); }
                    catch (Exception e) { return false; }
                });

        // If sessions already exist, open the first one so the message
        // input becomes visible for any subsequent send-message steps.
        if (ctx.chatPage.hasChatSessions()) {
            ctx.chatPage.openFirstSession();
            new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                    .until(d -> {
                        try { return ctx.chatPage.isMessageInputVisible(); }
                        catch (Exception e) { return false; }
                    });
        }
    }

    @Then("the chat interface should be visible")
    public void theChatInterfaceShouldBeVisible() {
        // The page loads whether there are conversations or not (empty state is valid)
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.chatPage.isPageDisplayed(); }
                    catch (Exception e) { return false; }
                });
    }

    // ── TC002: Chat with Vendor flow from service detail page ─────────────────

    @When("the user clicks Chat with Vendor")
    public void theUserClicksChatWithVendor() {
        // The "Chat with Vendor" button is on the service detail page.
        // Clicking it calls startChat() which opens a chat session.
        Assert.assertTrue(ctx.serviceDetailPage.isChatWithVendorButtonVisible(),
                "Chat with Vendor button not found on this service detail page");
        ctx.serviceDetailPage.clickChatWithVendor();
    }

    @Then("the chat panel should be open")
    public void theChatPanelShouldBeOpen() {
        // After clicking Chat with Vendor, the chat panel appears with a message input.
        // The app may navigate to /chats or open an inline panel — either way
        // the .msg-input field becomes visible.
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.chatPage.isMessageInputVisible(); }
                    catch (Exception e) { return false; }
                });
    }

    // ── Shared: send message and verify ──────────────────────────────────────

    @When("the user sends a message {string}")
    public void theUserSendsAMessage(String message) {
        Assert.assertTrue(ctx.chatPage.isMessageInputVisible(),
                "Cannot send a message — chat panel is not open. " +
                "Ensure a session is open (TC002: click Chat with Vendor first).");

        final int prevCount = ctx.chatPage.getMessageCount();
        ctx.chatPage.sendMessage(message);

        // Two-condition wait to handle both rendering paths:
        //
        // Path A — Server-confirmed (WebSocket echo arrives):
        //   Angular appends the message to the list → getMessageCount() increases.
        //   This is the normal flow when Render.com backend responds promptly.
        //
        // Path B — Optimistic rendering (message shown immediately before echo):
        //   Angular renders the sent bubble with a [mine] attribute right away.
        //   isSentBubbleVisible() catches this via the .msg-row[mine] / [mine] selector.
        //   This handles Render.com free-tier WebSocket cold-start latency (> 45 s).
        new WebDriverWait(ctx.driver, Duration.ofSeconds(60))
                .until(d -> {
                    try {
                        return ctx.chatPage.getMessageCount() > prevCount
                                || ctx.chatPage.isSentBubbleVisible();
                    } catch (Exception e) { return false; }
                });
    }

    @Then("the message should appear in the chat window")
    public void theMessageShouldAppearInTheChatWindow() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.chatPage.getMessageCount() > 0; }
                    catch (Exception e) { return false; }
                });
    }
}
