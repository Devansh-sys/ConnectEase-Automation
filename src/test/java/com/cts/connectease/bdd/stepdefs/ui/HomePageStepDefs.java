package com.cts.connectease.bdd.stepdefs.ui;

import com.cts.connectease.bdd.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class HomePageStepDefs {

    private final TestContext ctx;

    public HomePageStepDefs(TestContext ctx) { this.ctx = ctx; }

    @Given("the user is on the home page")
    public void theUserIsOnTheHomePage() {
        ctx.homePage.navigateTo(ctx.BASE_URL);
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.homePage.isHeroTitleVisible(); }
                    catch (Exception e) { return false; }
                });
    }

    @Then("the hero section should be visible")
    public void theHeroSectionShouldBeVisible() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.homePage.isHeroTitleVisible(); }
                    catch (Exception e) { return false; }
                });
    }

    @Then("service category cards should be displayed")
    public void serviceCategoryCardsShouldBeDisplayed() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.homePage.getCategoryCount() > 0; }
                    catch (Exception e) { return false; }
                });
    }

    @When("the user clicks on a service category card")
    public void theUserClicksOnAServiceCategoryCard() {
        ctx.homePage.clickCategoryByIndex(0);
    }

    @Then("the user should be navigated to the service listings page")
    public void theUserShouldBeNavigatedToServiceListings() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> d.getCurrentUrl().contains("/listings") || d.getCurrentUrl().contains("/services"));
    }

    @When("the user clicks the Connect Ease logo")
    public void theUserClicksTheLogo() {
        ctx.navbarPage.clickLogo();
    }

    @Then("the user should remain on or return to the home page")
    public void theUserShouldRemainOnHomePage() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    String url = d.getCurrentUrl();
                    return url.contains("/home") || url.equals(ctx.BASE_URL + "/");
                });
    }
}
