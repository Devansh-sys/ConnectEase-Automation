package com.cts.connectease.bdd.stepdefs.api;

import com.cts.connectease.bdd.context.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.config.SSLConfig;
import io.restassured.response.Response;
import org.testng.Assert;

public class AuthApiStepDefs {

    private final TestContext ctx;
    private Response lastResponse;

    /** 90-second timeout + relaxed SSL — handles Vercel cold starts and corporate SSL proxy. */
    private static final RestAssuredConfig TIMEOUT_CONFIG = RestAssuredConfig.config()
            .httpClient(HttpClientConfig.httpClientConfig()
                    .setParam("http.connection.timeout", 90_000)
                    .setParam("http.socket.timeout",     90_000))
            .sslConfig(SSLConfig.sslConfig().relaxedHTTPSValidation());

    public AuthApiStepDefs(TestContext ctx) { this.ctx = ctx; }

    @Given("the Auth API is available at {string}")
    public void theAuthApiIsAvailable(String baseUrl) {
        RestAssured.baseURI = baseUrl;
    }

    @When("the client sends a POST to {string} with new user credentials")
    public void theClientPostsWithNewUserCredentials(String endpoint) {
        // Use a timestamped email so this always registers a brand-new account (201 every run)
        String uniqueEmail = "bddtest." + System.currentTimeMillis() + "@example.com";
        lastResponse = RestAssured
                .given()
                .config(TIMEOUT_CONFIG)
                .contentType("application/json")
                .body("{\"fullName\":\"BDD Test User\",\"email\":\"" + uniqueEmail
                        + "\",\"password\":\"Test@12345\",\"phoneNo\":\"9999999999\",\"role\":\"customer\"}")
                .post(endpoint);
    }

    @When("the client sends a POST to {string} with valid credentials")
    public void theClientPostsWithValidCredentials(String endpoint) {
        // Uses ctx.CUSTOMER_EMAIL — a pre-existing account — to test duplicate-email rejection (400)
        lastResponse = RestAssured
                .given()
                .config(TIMEOUT_CONFIG)
                .contentType("application/json")
                .body("{\"fullName\":\"Test User\",\"email\":\"" + ctx.CUSTOMER_EMAIL
                        + "\",\"password\":\"" + ctx.CUSTOMER_PASSWORD
                        + "\",\"phoneNo\":\"9999999999\",\"role\":\"customer\"}")
                .post(endpoint);
    }

    @When("the client sends a POST to {string} with invalid credentials")
    public void theClientPostsWithInvalidCredentials(String endpoint) {
        lastResponse = RestAssured
                .given()
                .config(TIMEOUT_CONFIG)
                .contentType("application/json")
                .body("{\"email\":\"wrong@example.com\",\"password\":\"wrongpass\"}")
                .post(endpoint);
    }

    @When("the client sends a POST to {string} with missing fields")
    public void theClientPostsWithMissingFields(String endpoint) {
        lastResponse = RestAssured
                .given()
                .config(TIMEOUT_CONFIG)
                .contentType("application/json")
                .body("{}")
                .post(endpoint);
    }

    @Then("the API response status should be {int}")
    public void theApiResponseStatusShouldBe(int statusCode) {
        Assert.assertEquals(lastResponse.statusCode(), statusCode,
                "Expected status " + statusCode + " but got " + lastResponse.statusCode());
    }

    @Then("the response body should contain a uid")
    public void theResponseBodyShouldContainAUid() {
        // Signin returns {"status":"success","uid":"...","role":"..."} — JWT is a cookie, not a body field
        Assert.assertNotNull(lastResponse.jsonPath().getString("uid"),
                "Expected uid in signin response body");
    }

    @Then("the response body should contain an error message")
    public void theResponseBodyShouldContainAnErrorMessage() {
        String body = lastResponse.asString();
        Assert.assertTrue(body.contains("error") || body.contains("message"),
                "Expected error message in response body");
    }
}
