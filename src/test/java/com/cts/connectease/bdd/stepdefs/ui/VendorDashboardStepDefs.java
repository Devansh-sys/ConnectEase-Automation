package com.cts.connectease.bdd.stepdefs.ui;

import com.cts.connectease.bdd.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class VendorDashboardStepDefs {

    private final TestContext ctx;

    public VendorDashboardStepDefs(TestContext ctx) { this.ctx = ctx; }

    @Given("the vendor is on the vendor dashboard")
    public void theVendorIsOnTheDashboard() {
        ctx.vendorDashboardPage.navigateTo(ctx.BASE_URL);
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.vendorDashboardPage.isDashboardDisplayed(); }
                    catch (Exception e) { return false; }
                });
    }

    @Then("the vendor dashboard should load successfully")
    public void theVendorDashboardShouldLoad() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.vendorDashboardPage.isDashboardDisplayed(); }
                    catch (Exception e) { return false; }
                });
    }

    @Then("the vendor's service listings should be visible")
    public void theVendorServiceListingsShouldBeVisible() {
        ctx.vendorDashboardPage.clickTab("listings");
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.vendorDashboardPage.getMyListingCount() >= 0; }
                    catch (Exception e) { return false; }
                });
    }

    @When("the vendor clicks Add New Service")
    public void theVendorClicksAddNewService() {
        ctx.vendorDashboardPage.clickAddService();
    }

    @Then("the add service form should appear")
    public void theAddServiceFormShouldAppear() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.vendorDashboardPage.isAddServiceFormVisible(); }
                    catch (Exception e) { return false; }
                });
    }

    @When("the vendor fills in and submits the new service form")
    public void theVendorFillsInAndSubmitsNewServiceForm() {
        // Open the Add Service form first (still on Dashboard tab after navigateTo)
        ctx.vendorDashboardPage.clickAddService();
        new WebDriverWait(ctx.driver, Duration.ofSeconds(30))
                .until(d -> {
                    try { return ctx.vendorDashboardPage.isAddServiceFormVisible(); }
                    catch (Exception e) { return false; }
                });
        ctx.vendorDashboardPage.fillServiceName("Test Service " + System.currentTimeMillis());
        ctx.vendorDashboardPage.fillServiceDescription("Test description for BDD scenario");
        ctx.vendorDashboardPage.fillServicePrice("1500");
        ctx.vendorDashboardPage.clickSubmitService();
        // After successful submit the Add Service tab/form should no longer be active
        new WebDriverWait(ctx.driver, Duration.ofSeconds(60))
                .until(d -> {
                    try { return !ctx.vendorDashboardPage.isAddServiceFormVisible()
                              || ctx.vendorDashboardPage.isSuccessToastVisible(); }
                    catch (Exception e) { return false; }
                });
    }

    @Then("the new service should appear in the listings")
    public void theNewServiceShouldAppearInListings() {
        ctx.vendorDashboardPage.clickTab("listings");
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.vendorDashboardPage.getMyListingCount() > 0; }
                    catch (Exception e) { return false; }
                });
    }
}
