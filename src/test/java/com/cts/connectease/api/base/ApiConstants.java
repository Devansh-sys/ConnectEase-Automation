package com.cts.connectease.api.base;

/**
 * Test data constants for backend API tests.
 * Source: ConnectEase_Backend_Testing_UPDATED.xlsx → TestCases sheet.
 */
public class ApiConstants {

    private ApiConstants() {}

    // ── Base URL ──────────────────────────────────────────────────────────────
    public static final String BASE_URL = "https://connectease-1.onrender.com";

    // ── Customer credentials ──────────────────────────────────────────────────
    // NOTE: Uses .apitest domain to avoid collisions with Selenium UI test users
    //       (UI tests register ravi@test.com / vamsi@test.com with a different password)
    public static final String CUSTOMER_FULL_NAME = "Ravi Kumar";
    public static final String CUSTOMER_EMAIL     = "ravi.apitest@test.com";
    public static final String CUSTOMER_PASSWORD  = "securePass123";
    public static final String CUSTOMER_PHONE     = "9876543210";

    // ── Vendor credentials ────────────────────────────────────────────────────
    public static final String VENDOR_FULL_NAME = "Vamsi Vendor";
    public static final String VENDOR_EMAIL     = "vamsi.apitest@test.com";
    public static final String VENDOR_PASSWORD  = "vendor123";
    public static final String VENDOR_PHONE     = "9123456789";

    // ── Negative-test data ────────────────────────────────────────────────────
    public static final String WRONG_PASSWORD  = "wrongPass";
    public static final String INVALID_UID     = "00000000-0000-0000-0000-000000000000";
    public static final String INVALID_SID     = "invalid-sid-000";

    // ── Password-change data ──────────────────────────────────────────────────
    public static final String OLD_PASSWORD    = "securePass123";
    public static final String NEW_PASSWORD    = "newSecurePass456";

    // ── Service creation data (Vendor) ────────────────────────────────────────
    public static final String SERVICE_NAME        = "SparkFix Electrical";
    public static final String SERVICE_DESCRIPTION = "Professional electrical repair and installation service";
    public static final double SERVICE_PRICE       = 750.0;
    public static final String SERVICE_CITY        = "Chennai";
    public static final String SERVICE_AREA        = "Egattur";

    // ── Community post data ───────────────────────────────────────────────────
    public static final String POST_TITLE       = "Need advice on finding electricians";
    public static final String POST_DESCRIPTION = "What is the best way to verify an electrician's credentials?";
    public static final String POST_CATEGORY    = "Electrician";
    public static final String POST_IMAGE_URL   = "https://example.com/images/electrician.jpg";
}
