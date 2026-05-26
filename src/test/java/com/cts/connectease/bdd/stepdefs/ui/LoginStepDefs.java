package com.cts.connectease.bdd.stepdefs.ui;

import com.cts.connectease.bdd.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginStepDefs {

    private final TestContext ctx;

    public LoginStepDefs(TestContext ctx) { this.ctx = ctx; }

    @Given("the user is on the login page")
    public void theUserIsOnTheLoginPage() {
        ctx.loginPage.navigateTo(ctx.BASE_URL);
    }

    @When("the user logs in with email {string} and password {string}")
    public void theUserLogsInWithEmailAndPassword(String email, String password) {
        ctx.loginPage.enterEmail(email);
        ctx.loginPage.enterPassword(password);
        ctx.loginPage.clickSignIn();
    }

    @Then("the user should be redirected to the home page")
    public void theUserShouldBeRedirectedToTheHomePage() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    String url = d.getCurrentUrl();
                    return url.contains("/home") || url.contains("/dashboard") || url.equals(ctx.BASE_URL + "/");
                });
    }

    @Then("an error message should be displayed on login")
    public void anErrorMessageShouldBeDisplayed() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return ctx.loginPage.isErrorMessageDisplayed(); }
                    catch (Exception e) { return false; }
                });
    }

    @When("the user clicks the logout button")
    public void theUserClicksTheLogoutButton() {
        ctx.navbarPage.clickSignOut();
    }

    @Then("the user should be redirected to the login page")
    public void theUserShouldBeRedirectedToTheLoginPage() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> d.getCurrentUrl().contains("/login"));
    }

    @Given("the user is logged in as a customer")
    public void theUserIsLoggedInAsACustomer() {
        ctx.loginPage.navigateTo(ctx.BASE_URL);
        ctx.loginPage.enterEmail(ctx.CUSTOMER_EMAIL);
        ctx.loginPage.enterPassword(ctx.CUSTOMER_PASSWORD);
        ctx.loginPage.clickSignIn();
        // 90 s: Render.com free-tier backend takes 50-60 s to cold-start on the first request.
        // Also accept BASE_URL without trailing slash (Angular sometimes lands there).
        new WebDriverWait(ctx.driver, Duration.ofSeconds(90))
                .until(d -> {
                    String url = d.getCurrentUrl();
                    return url.contains("/home") || url.contains("/dashboard")
                            || url.equals(ctx.BASE_URL + "/") || url.equals(ctx.BASE_URL);
                });
    }

    @Given("the user is logged in as a vendor")
    public void theUserIsLoggedInAsAVendor() {
        ctx.loginPage.navigateTo(ctx.BASE_URL);
        ctx.loginPage.enterEmail(ctx.VENDOR_EMAIL);
        ctx.loginPage.enterPassword(ctx.VENDOR_PASSWORD);
        ctx.loginPage.clickSignIn();
        new WebDriverWait(ctx.driver, Duration.ofSeconds(90))
                .until(d -> {
                    String url = d.getCurrentUrl();
                    return url.contains("/home") || url.contains("/dashboard")
                            || url.equals(ctx.BASE_URL + "/") || url.equals(ctx.BASE_URL);
                });
    }

    @Given("the user is not logged in")
    public void theUserIsNotLoggedIn() {
        ctx.clearSession();
        ctx.driver.get(ctx.BASE_URL);
    }

    @When("the user navigates directly to {string}")
    public void theUserNavigatesDirectlyTo(String path) {
        ctx.driver.get(ctx.BASE_URL + path);
    }

    @Then("the browser URL should contain {string}")
    public void theBrowserUrlShouldContain(String expectedPath) {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> d.getCurrentUrl().contains(expectedPath));
    }
}
