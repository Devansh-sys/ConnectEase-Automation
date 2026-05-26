package com.cts.connectease.bdd.stepdefs.ui;

import com.cts.connectease.bdd.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class AIChatStepDefs {

    private final TestContext ctx;

    public AIChatStepDefs(TestContext ctx) { this.ctx = ctx; }

    @Given("the user navigates to the AI Chat page")
    public void theUserNavigatesToAiChatPage() {
        ctx.aiChatPage.navigateTo(ctx.BASE_URL);
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.aiChatPage.isQueryInputVisible(); }
                    catch (Exception e) { return false; }
                });
    }

    @Then("the query input field should be visible")
    public void theQueryInputFieldShouldBeVisible() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.aiChatPage.isQueryInputVisible(); }
                    catch (Exception e) { return false; }
                });
    }

    @When("the user asks AI Chat {string}")
    public void theUserAsksAiChat(String query) {
        final int prevCount = ctx.aiChatPage.getAiResponseCount();
        ctx.aiChatPage.askQuestion(query);
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.aiChatPage.getAiResponseCount() > prevCount; }
                    catch (Exception e) { return false; }
                });
    }

    @Then("the AI Chat should respond to the first query")
    public void theAiChatShouldRespondToFirstQuery() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.aiChatPage.getAiResponseCount() > 0; }
                    catch (Exception e) { return false; }
                });
    }

    @Then("the AI Chat page should be displayed")
    public void theAiChatPageShouldBeDisplayed() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> d.getCurrentUrl().contains("ai") || d.getCurrentUrl().contains("chat"));
    }
}
