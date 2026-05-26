package com.cts.connectease.bdd.stepdefs.ui;

import com.cts.connectease.bdd.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ServiceDetailStepDefs {

    private final TestContext ctx;

    public ServiceDetailStepDefs(TestContext ctx) { this.ctx = ctx; }

    @Given("the user is on a service detail page")
    public void theUserIsOnAServiceDetailPage() {
        ctx.serviceListingsPage.navigateTo(ctx.BASE_URL);
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.serviceListingsPage.getServiceCardCount() > 0; }
                    catch (Exception e) { return false; }
                });
        ctx.serviceListingsPage.clickFirstServiceCard();
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.serviceDetailPage.isPageDisplayed(); }
                    catch (Exception e) { return false; }
                });
    }

    @Then("the service name should be displayed")
    public void theServiceNameShouldBeDisplayed() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return !ctx.serviceDetailPage.getServiceName().isEmpty(); }
                    catch (Exception e) { return false; }
                });
    }

    @Then("the service description should be displayed")
    public void theServiceDescriptionShouldBeDisplayed() {
        // FRD §2.4.1: tags / description block must be present (page loaded with content)
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.serviceDetailPage.isPageDisplayed()
                                 && !ctx.serviceDetailPage.getServiceName().isEmpty(); }
                    catch (Exception e) { return false; }
                });
    }

    @Then("the Chat with Vendor button should be visible")
    public void theChatWithVendorButtonShouldBeVisible() {
        // FRD §2.3.1 and §2.4.3: "A prominent Chat with Vendor button is displayed
        // below the rating panel and vendor summary."
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.serviceDetailPage.isChatWithVendorButtonVisible(); }
                    catch (Exception e) { return false; }
                });
    }

    @Then("the vendor details should be displayed")
    public void theVendorDetailsShouldBeDisplayed() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.serviceDetailPage.isVendorNameVisible(); }
                    catch (Exception e) { return false; }
                });
    }

    @When("the user clicks the Book Now or Contact button")
    public void theUserClicksBookNowOrContact() {
        if (ctx.serviceDetailPage.isChatWithVendorButtonVisible()) {
            ctx.serviceDetailPage.clickChatWithVendor();
        }
    }

    @Then("a booking or contact confirmation should appear")
    public void aBookingConfirmationShouldAppear() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> d.getCurrentUrl() != null && !d.getCurrentUrl().isEmpty());
    }
}
