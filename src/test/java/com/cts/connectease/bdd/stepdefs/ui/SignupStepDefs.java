package com.cts.connectease.bdd.stepdefs.ui;

import com.cts.connectease.bdd.context.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class SignupStepDefs {

    private final TestContext ctx;

    public SignupStepDefs(TestContext ctx) { this.ctx = ctx; }

    @Given("the user navigates to the signup page")
    public void theUserNavigatesToTheSignupPage() {
        ctx.signupPage.navigateToSignup();
    }

    @When("the user fills in valid signup details with name {string} phone {string} email {string} and password {string}")
    public void theUserFillsInValidSignupDetails(String name, String phone, String email, String password) {
        // Append a timestamp to the local part so every run registers a fresh account.
        // Without this, the second+ run always gets "Email already exists" from the
        // deployed backend, the form never redirects, and the Then-step times out.
        // e.g. "bdd_smoke_user@test.com" → "bdd_smoke_user_1748391557123@test.com"
        String uniqueEmail = email.contains("@")
                ? email.split("@")[0] + "_" + System.currentTimeMillis() + "@" + email.split("@")[1]
                : email;
        System.out.println("   [SignupStepDefs] Using unique email: " + uniqueEmail);
        ctx.signupPage.enterName(name);
        ctx.signupPage.enterPhone(phone);
        ctx.signupPage.enterEmail(uniqueEmail);
        ctx.signupPage.enterPassword(password);
    }

    @And("the user submits the signup form")
    public void theUserSubmitsTheSignupForm() {
        ctx.signupPage.clickSignupButton();
    }

    @Then("the user should be redirected to the login page after signup")
    public void theUserShouldBeRedirectedToLoginAfterSignup() {
        // After successful signup the backend returns 201 with NO JWT cookie —
        // the user is registered but not auto-logged-in. Angular redirects to /login.
        // 90 s: Render.com free-tier backend may cold-start on the first signup request.
        new WebDriverWait(ctx.driver, Duration.ofSeconds(90))
                .until(d -> d.getCurrentUrl().contains("/login"));
    }

    @Given("the user is on the signup page")
    public void theUserIsOnTheSignupPage() {
        ctx.signupPage.navigateToSignup();
    }

    @When("the user submits the signup form without filling any fields")
    public void theUserSubmitsWithoutFillingAnyFields() {
        ctx.signupPage.clickSignupButton();
    }

    @Then("validation error messages should appear on the signup form")
    public void validationErrorMessagesShouldAppear() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    boolean hasError = !d.findElements(
                            By.cssSelector("[class*='error'],[role='alert'],[class*='invalid']")).isEmpty();
                    boolean stillOnSignup = d.getCurrentUrl().contains("/signup");
                    return hasError || stillOnSignup;
                });
    }

    @When("the user enters an already registered email {string}")
    public void theUserEntersAlreadyRegisteredEmail(String email) {
        ctx.signupPage.enterName("Test User");
        ctx.signupPage.enterPhone("9999999999");
        ctx.signupPage.enterEmail(email);
        ctx.signupPage.enterPassword("Test@1234");
        ctx.signupPage.clickSignupButton();
    }

    @Then("a duplicate email error should be displayed")
    public void aDuplicateEmailErrorShouldBeDisplayed() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return !ctx.signupPage.getErrorMessage().isEmpty(); }
                    catch (Exception e) { return false; }
                });
    }

    @When("the user enters a password {string} that is too short")
    public void theUserEntersAPasswordThatIsTooShort(String password) {
        ctx.signupPage.enterName("Test User");
        ctx.signupPage.enterPhone("9999999999");
        ctx.signupPage.enterEmail("short_pw_test@example.com");
        ctx.signupPage.enterPassword(password);
        ctx.signupPage.clickSignupButton();
    }

    @Then("a password length error should be displayed")
    public void aPasswordLengthErrorShouldBeDisplayed() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return !ctx.signupPage.getErrorMessage().isEmpty(); }
                    catch (Exception e) { return false; }
                });
    }

    @When("the user enters an invalid phone number {string}")
    public void theUserEntersAnInvalidPhoneNumber(String phone) {
        ctx.signupPage.enterName("Test User");
        ctx.signupPage.enterPhone(phone);
        ctx.signupPage.enterEmail("phone_test@example.com");
        ctx.signupPage.enterPassword("Valid@1234");
        ctx.signupPage.clickSignupButton();
    }

    @Then("a phone number error should be displayed")
    public void aPhoneNumberErrorShouldBeDisplayed() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return !ctx.signupPage.getErrorMessage().isEmpty(); }
                    catch (Exception e) { return false; }
                });
    }

    @When("the user enters the name {string}")
    public void theUserEntersTheName(String name) {
        ctx.signupPage.enterName(name);
        ctx.signupPage.enterPhone("9876543210");
        ctx.signupPage.enterEmail("name_test@example.com");
        ctx.signupPage.enterPassword("Valid@1234");
        ctx.signupPage.clickSignupButton();
    }

    @Then("a name validation error should be displayed")
    public void aNameValidationErrorShouldBeDisplayed() {
        new WebDriverWait(ctx.driver, Duration.ofSeconds(45))
                .until(d -> {
                    try { return !ctx.signupPage.getErrorMessage().isEmpty(); }
                    catch (Exception e) { return false; }
                });
    }
}
