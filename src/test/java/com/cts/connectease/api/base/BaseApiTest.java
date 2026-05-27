package com.cts.connectease.api.base;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeSuite;

import java.net.InetSocketAddress;
import java.net.Socket;

import static io.restassured.RestAssured.given;

/**
 * Base class for all ConnectEase backend API tests.
 *
 * ┌─────────────────────────────────────────────────────────────────────┐
 * │  PRE-REQUISITE: Start the Spring Boot backend BEFORE running tests  │
 * │  Default URL : http://localhost:8081                                │
 * │  Command     : mvn spring-boot:run   (or run from IntelliJ)        │
 * └─────────────────────────────────────────────────────────────────────┘
 *
 * If the server is not reachable, ALL tests are SKIPPED (not failed)
 * with a clear message — no cryptic ConnectException stack traces.
 *
 * Shared state (customerUid, vendorUid, cookies, testSid, etc.) is stored
 * as static fields so data flows naturally across ordered test classes without
 * needing external files or TestNG data-providers.
 *
 * Run order enforced via testng-api.xml:
 *   AuthApiTest → UserProfileApiTest → CategoriesLocationsApiTest
 *   → ServiceListingsApiTest → VendorDashboardApiTest
 *   → ServiceDetailApiTest → RatingsApiTest
 *   → CommunityForumApiTest → ChatApiTest → AIChatApiTest
 */
public class BaseApiTest {

    // ── Server reachability flag (set once in @BeforeSuite) ───────────────────
    private static volatile boolean serverAvailable = false;

    // ── Shared runtime state ──────────────────────────────────────────────────
    protected static volatile String customerUid;
    protected static volatile String vendorUid;
    protected static volatile String customerCookie;   // raw jwt cookie value
    protected static volatile String vendorCookie;
    protected static volatile String testSid;          // service ID
    protected static volatile String testRid;          // rating ID
    protected static volatile String testPostId;       // community post ID
    protected static volatile String testSessionId;    // chat session ID
    protected static volatile String testCategoryId;   // a real cid from GET /api/categories

    // ── Suite setup ───────────────────────────────────────────────────────────

    /**
     * Runs ONCE before the entire suite.
     * Sets up REST Assured and checks if the backend server is reachable.
     */
    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        RestAssured.baseURI = ApiConstants.BASE_URL;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        serverAvailable = isServerReachable("connectease-1.onrender.com", 443, 10000);

        if (serverAvailable) {
            System.out.println("\n✔ Backend server is reachable at " + ApiConstants.BASE_URL);
        } else {
            System.err.println("\n");
            System.err.println("╔══════════════════════════════════════════════════════════════╗");
            System.err.println("║         DEPLOYED BACKEND IS NOT REACHABLE                    ║");
            System.err.println("╠══════════════════════════════════════════════════════════════╣");
            System.err.println("║  URL    : " + ApiConstants.BASE_URL + "  ║");
            System.err.println("║  Fix    : Check network connectivity or Render.com status    ║");
            System.err.println("╚══════════════════════════════════════════════════════════════╝");
            System.err.println();
        }
    }

    /**
     * Runs ONCE before each test CLASS that extends BaseApiTest.
     * Throws SkipException (not a failure) if the backend is unreachable,
     * causing all tests in that class to be marked as SKIPPED cleanly.
     */
    @BeforeClass(alwaysRun = true)
    public void requireServerRunning() {
        if (!serverAvailable) {
            throw new SkipException(
                    "SKIPPED — deployed backend not reachable at " + ApiConstants.BASE_URL
                    + ". Check network connectivity or Render.com status.");
        }
    }

    // ── Request builders ──────────────────────────────────────────────────────

    /** Unauthenticated JSON request. */
    protected static RequestSpecification noAuth() {
        return given().contentType("application/json");
    }

    /** Authenticated JSON request using the Customer jwt cookie. */
    protected static RequestSpecification asCustomer() {
        if (customerCookie == null) {
            throw new SkipException(
                    "customerCookie is null — AuthApiTest.loginCustomer() must run first.");
        }
        return given().contentType("application/json").cookie("jwt", customerCookie);
    }

    /** Authenticated JSON request using the Vendor jwt cookie. */
    protected static RequestSpecification asVendor() {
        if (vendorCookie == null) {
            throw new SkipException(
                    "vendorCookie is null — AuthApiTest.loginVendor() must run first.");
        }
        return given().contentType("application/json").cookie("jwt", vendorCookie);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns the numeric status code of a response for readable assertions. */
    protected static int status(io.restassured.response.Response r) {
        return r.getStatusCode();
    }

    /**
     * Tries to open a TCP socket to host:port within timeoutMs.
     * Returns true if the connection succeeds (server is up), false otherwise.
     */
    private static boolean isServerReachable(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
