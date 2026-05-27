package com.cts.connectease.tests;

import com.cts.connectease.base.BaseTest;
import com.cts.connectease.constants.AppConstants;
import com.cts.connectease.dataprovider.TestDataProvider;
import com.cts.connectease.pages.SignupPage;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * Test class for Signup functionality
 * URL: https://connect-ease-nu.vercel.app/signup
 * Actual form fields: Full Name | Email | Password | Phone | Role (Customer/Vendor)
 * There is NO confirm-password field on this form.
 */
public class SignupPageTest extends BaseTest {

    private SignupPage signupPage;

    @BeforeMethod
    public void setUpSignupPage() {
        signupPage = new SignupPage(driver);
        signupPage.navigateToSignup();
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TC_SIGNUP_00 — Debug: print all input fields
    // ─────────────────────────────────────────────────────────────────────────
    @Test(priority = 0, description = "DEBUG — Print all input fields to console")
    public void debugPrintAllInputFields() {
        signupPage.debugPrintInputFields();
        System.out.println("✔ TC_SIGNUP_00: Debug info printed above.");
        Assert.assertTrue(signupPage.isSignupPageDisplayed(),
                "Signup page should have at least one visible input");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TC_SIGNUP_01 — Page load
    // ─────────────────────────────────────────────────────────────────────────
    @Test(priority = 1, description = "Verify signup page loads successfully")
    public void testSignupPageIsDisplayed() {
        Assert.assertTrue(signupPage.isSignupPageDisplayed(),
                "Signup page should display at least one visible input field");
        System.out.println("✔ TC_SIGNUP_01 PASSED: Signup page loaded — URL: "
                + signupPage.getCurrentUrl());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TC_SIGNUP_02 — Register the CUSTOMER test user used by LoginPageTest
    // First run → 201 redirects away from /signup (PASS)
    // Re-run    → "already exists" error on /signup (also PASS — user exists)
    // ─────────────────────────────────────────────────────────────────────────
    @Test(priority = 2, description = "Register Customer test user (nav@test.com) for login tests")
    public void testRegisterCustomerTestUser() {
        signupPage.signup(
                "Nav Customer",
                AppConstants.CUSTOMER_EMAIL,
                AppConstants.CUSTOMER_PASSWORD,
                AppConstants.SIGNUP_PHONE
        );

        boolean redirected   = signupPage.isSignupSuccessful();
        String  errorText    = signupPage.getErrorMessage().toLowerCase();
        boolean alreadyExists = errorText.contains("already") || errorText.contains("exists")
                || errorText.contains("registered");

        Assert.assertTrue(redirected || alreadyExists,
                "Expected redirect (new user) or 'already exists' error (re-run). "
                + "Error shown: '" + errorText + "'  URL: " + signupPage.getCurrentUrl());

        if (redirected) {
            System.out.println("✔ TC_SIGNUP_02 PASSED: Customer test user registered → "
                    + signupPage.getCurrentUrl());
        } else {
            System.out.println("✔ TC_SIGNUP_02 PASSED: Customer test user already exists — login tests can proceed");
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TC_SIGNUP_NEG — Data-driven negative validation (replaces TC_SIGNUP_04/05/06/08)
    //
    // Test data from Excel: src/test/resources/testdata/ConnectEase_TestData.xlsx
    //                       Sheet: SignupNegativeData
    //
    // Rows covered:
    //   TC_SIGNUP_NEG_01 — Empty name
    //   TC_SIGNUP_NEG_02 — Non-numeric / invalid phone
    //   TC_SIGNUP_NEG_03 — Malformed email format
    //   TC_SIGNUP_NEG_04 — Password too short
    //
    // @BeforeMethod navigates to /signup before EACH DataProvider row automatically.
    // ─────────────────────────────────────────────────────────────────────────
    @Test(priority = 4,
          description = "TC_SIGNUP_NEG — Data-driven: empty name / invalid phone / invalid email / weak password",
          dataProvider = "signupNegativeData",
          dataProviderClass = TestDataProvider.class)
    public void testSignupNegativeValidations(String testCaseId, String name, String phone,
                                              String email, String password, String description) {
        System.out.println("▶ " + testCaseId + ": " + description);

        signupPage.signup(name, email, password, phone);

        boolean blocked = signupPage.getCurrentUrl().contains("signup")
                || !signupPage.getErrorMessage().isEmpty();
        Assert.assertTrue(blocked,
                "[" + testCaseId + "] Form should be blocked for invalid input — " + description
                + " | URL: " + signupPage.getCurrentUrl());
        System.out.println("✔ " + testCaseId + " PASSED: Signup correctly blocked — " + description);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TC_SIGNUP_07 — All fields empty
    // ─────────────────────────────────────────────────────────────────────────
    @Test(priority = 7, description = "Verify empty form submission is blocked")
    public void testSignupWithAllFieldsEmpty() {
        signupPage.clickSignupButton();

        boolean blocked = signupPage.getCurrentUrl().contains("signup")
                || !signupPage.getErrorMessage().isEmpty();
        Assert.assertTrue(blocked, "Empty form submission should be blocked");
        System.out.println("✔ TC_SIGNUP_07 PASSED: Empty form blocked");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // TC_SIGNUP_09 — Register the VENDOR test user used by LoginPageTest
    // Same idempotent logic as TC_SIGNUP_02: first run creates, re-run is OK
    // ─────────────────────────────────────────────────────────────────────────
    @Test(priority = 9, description = "Register Vendor test user (navya1.vendor@test.com) for login tests")
    public void testRegisterVendorTestUser() {
        signupPage.signupAsVendor(
                "Navya Vendor",
                AppConstants.VENDOR_EMAIL,      // navya1.vendor@test.com
                AppConstants.VENDOR_PASSWORD,   // vendor123
                "9123456780"
        );

        boolean redirected   = signupPage.isSignupSuccessful();
        String  errorText    = signupPage.getErrorMessage().toLowerCase();
        boolean alreadyExists = errorText.contains("already") || errorText.contains("exists")
                || errorText.contains("registered");

        Assert.assertTrue(redirected || alreadyExists,
                "Expected redirect (new vendor) or 'already exists' error (re-run). "
                + "Error: '" + errorText + "'  URL: " + signupPage.getCurrentUrl());

        if (redirected) {
            System.out.println("✔ TC_SIGNUP_09 PASSED: Vendor test user registered → "
                    + signupPage.getCurrentUrl());
        } else {
            System.out.println("✔ TC_SIGNUP_09 PASSED: Vendor test user already exists — vendor login tests can proceed");
        }
    }
}
