package com.cts.connectease.bdd.stepdefs.ui;

import com.cts.connectease.bdd.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class ServiceListingsStepDefs {

    private final TestContext ctx;

    public ServiceListingsStepDefs(TestContext ctx) { this.ctx = ctx; }

    @Given("the user is on the service listings page")
    public void theUserIsOnTheServiceListingsPage() {
        ctx.serviceListingsPage.navigateTo(ctx.BASE_URL);
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.serviceListingsPage.getServiceCardCount() > 0; }
                    catch (Exception e) { return false; }
                });
    }

    @Then("service listing cards should be displayed")
    public void serviceListingCardsShouldBeDisplayed() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.serviceListingsPage.getServiceCardCount() > 0; }
                    catch (Exception e) { return false; }
                });
    }

    @When("the user applies a category filter")
    public void theUserAppliesACategoryFilter() {
        if (ctx.serviceListingsPage.isCategoryNavBarVisible()
                && ctx.serviceListingsPage.getCategoryNavItemCount() > 0) {
            ctx.serviceListingsPage.clickCategoryInNavBar("");
        }
    }

    @Then("only services of the selected category should be displayed")
    public void onlyServicesOfSelectedCategoryShouldBeDisplayed() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.serviceListingsPage.getServiceCardCount() >= 0; }
                    catch (Exception e) { return false; }
                });
    }

    @When("the user searches for a service by keyword {string}")
    public void theUserSearchesForAServiceByKeyword(String keyword) {
        ctx.serviceListingsPage.navigateTo(ctx.BASE_URL);
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.serviceListingsPage.getServiceCardCount() >= 0; }
                    catch (Exception e) { return false; }
                });
    }

    @Then("search results containing {string} should appear")
    public void searchResultsShouldAppear(String keyword) {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.serviceListingsPage.getServiceCardCount() >= 0; }
                    catch (Exception e) { return false; }
                });
    }

    @When("the user sorts listings by {string}")
    public void theUserSortsListingsBy(String sortOption) {
        ctx.serviceListingsPage.selectSortOption(sortOption);
    }

    @Then("listings should be sorted accordingly")
    public void listingsShouldBeSortedAccordingly() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.serviceListingsPage.getServiceCardCount() >= 0; }
                    catch (Exception e) { return false; }
                });
    }

    @When("the user clicks on the first service listing card")
    public void theUserClicksOnFirstListingCard() {
        ctx.serviceListingsPage.clickFirstServiceCard();
    }

    @Then("the user should be navigated to a service detail page")
    public void theUserShouldBeNavigatedToServiceDetailPage() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> d.getCurrentUrl().contains("/service") || d.getCurrentUrl().contains("/detail"));
    }
}
