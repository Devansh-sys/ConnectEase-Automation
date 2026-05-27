package com.cts.connectease.constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AppConstants {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = AppConstants.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in != null) props.load(in);
        } catch (IOException e) {
            throw new ExceptionInInitializerError("Cannot load config.properties: " + e.getMessage());
        }
    }

    private AppConstants() {
        // Utility class — no instantiation
    }

    // ── Config-driven credentials ─────────────────────────────────────────────
    public static final String CUSTOMER_EMAIL     = props.getProperty("customer.email");
    public static final String CUSTOMER_PASSWORD  = props.getProperty("customer.password");
    public static final String VENDOR_EMAIL       = props.getProperty("vendor.email");
    public static final String VENDOR_PASSWORD    = props.getProperty("vendor.password");

    // ── App ───────────────────────────────────────────────────────────────────
    public static final String BASE_URL = "https://connect-ease-nu.vercel.app";

    // ── Timeouts (seconds) ────────────────────────────────────────────────────
    public static final int IMPLICIT_WAIT     = 10;
    public static final int EXPLICIT_WAIT     = 15;
    public static final int PAGE_LOAD_TIMEOUT = 60;

    // ── Login credentials ─────────────────────────────────────────────────────
    public static final String VALID_EMAIL    = "navya@gmail.com";
    public static final String VALID_PASSWORD = "123";

    public static final String INVALID_EMAIL    = "wronguser@example.com";
    public static final String INVALID_PASSWORD = "WrongPass@000";

    // ── Signup test data ──────────────────────────────────────────────────────
    public static final String SIGNUP_NAME     = "Navya";
    public static final String SIGNUP_PHONE    = "9876543210";
    public static final String SIGNUP_PASSWORD = "navya@test.com";

    /**
     * A pre-registered email used to test the "duplicate email" scenario.
     * Falls back to CUSTOMER_EMAIL (from config.properties) which is guaranteed
     * to be registered. Override by setting signup.existing.email in config.properties.
     */
    public static final String EXISTING_EMAIL = resolveExistingEmail();

    private static String resolveExistingEmail() {
        String fromConfig = props.getProperty("signup.existing.email");
        if (fromConfig != null && !fromConfig.isEmpty()) return fromConfig;
        if (CUSTOMER_EMAIL != null && !CUSTOMER_EMAIL.isEmpty()) return CUSTOMER_EMAIL;
        return "existing_user@example.com";
    }
}


