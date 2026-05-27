package com.cts.connectease.dataprovider;

import com.cts.connectease.utils.ExcelDataReader;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Centralised TestNG {@code @DataProvider} class for ConnectEase automation.
 *
 * <p>All test data lives in a single Excel workbook:
 * <pre>
 *   src/test/resources/testdata/ConnectEase_TestData.xlsx
 * </pre>
 *
 * <p>The workbook is <b>auto-generated on first run</b> if it does not exist.
 * To add or modify test data, open the Excel file directly — no code changes needed.
 *
 * <h3>Sheets</h3>
 * <table>
 *   <tr><th>Sheet</th><th>Columns</th><th>Used by</th></tr>
 *   <tr>
 *     <td>LoginNegativeData</td>
 *     <td>TestCaseId | Email | Password | Description</td>
 *     <td>LoginPageTest.testWrongCredentialsShowsError()</td>
 *   </tr>
 *   <tr>
 *     <td>SignupNegativeData</td>
 *     <td>TestCaseId | Name | Phone | Email | Password | Description</td>
 *     <td>SignupPageTest.testSignupNegativeValidations()</td>
 *   </tr>
 *   <tr>
 *     <td>AuthNegativeData</td>
 *     <td>TestCaseId | FullName | Phone | Password | MissingField | Description</td>
 *     <td>AuthApiTest.signupNegativeScenarios()</td>
 *   </tr>
 * </table>
 */
public class TestDataProvider {

    /** Path to the Excel workbook — relative to the Maven project root. */
    static final String EXCEL_PATH =
        "src/test/resources/testdata/ConnectEase_TestData.xlsx";

    /**
     * Auto-create the Excel workbook on first run.
     * Subsequent runs reuse the existing file, so any manual edits are preserved.
     */
    static {
        File file = new File(EXCEL_PATH);
        if (!file.exists()) {
            file.getParentFile().mkdirs();   // create testdata/ directory if needed
            generateExcelFile(file);
        } else {
            System.out.println("[TestDataProvider] Using existing Excel test data: "
                + file.getAbsolutePath());
        }
    }

    // ── DataProvider methods ──────────────────────────────────────────────────

    /**
     * Negative login scenarios: unknown user, valid-format-but-not-registered,
     * and correct user with wrong password.
     *
     * <p>Sheet: {@code LoginNegativeData}
     * <br>Columns: TestCaseId | Email | Password | Description
     */
    @DataProvider(name = "loginNegativeData")
    public static Object[][] loginNegativeData() {
        return new ExcelDataReader(EXCEL_PATH).getSheetData("LoginNegativeData");
    }

    /**
     * Signup form negative validation scenarios: empty name, invalid phone,
     * invalid email format, and password too short.
     *
     * <p>Sheet: {@code SignupNegativeData}
     * <br>Columns: TestCaseId | Name | Phone | Email | Password | Description
     */
    @DataProvider(name = "signupNegativeData")
    public static Object[][] signupNegativeData() {
        return new ExcelDataReader(EXCEL_PATH).getSheetData("SignupNegativeData");
    }

    /**
     * Auth API negative scenarios: duplicate email signup and missing required fields.
     *
     * <p>Sheet: {@code AuthNegativeData}
     * <br>Columns: TestCaseId | FullName | Phone | Password | MissingField | Description
     * <br>{@code MissingField} values: {@code "none"} (duplicate email), {@code "email"}, {@code "name"}
     */
    @DataProvider(name = "authNegativeData")
    public static Object[][] authNegativeData() {
        return new ExcelDataReader(EXCEL_PATH).getSheetData("AuthNegativeData");
    }

    // ── Excel file generator ──────────────────────────────────────────────────

    /**
     * Programmatically creates the Excel workbook with all three sheets pre-populated.
     * Called once — only when the file does not exist.
     */
    private static void generateExcelFile(File file) {
        System.out.println("[TestDataProvider] Generating Excel test data file: "
            + file.getAbsolutePath());

        try (Workbook wb = new XSSFWorkbook()) {

            // ── Sheet 1: LoginNegativeData ────────────────────────────────────
            // Used by LoginPageTest.testWrongCredentialsShowsError()
            // Each row = one set of bad credentials that must trigger an error message.
            Sheet loginSheet = wb.createSheet("LoginNegativeData");
            addRow(loginSheet, 0,
                "TestCaseId", "Email", "Password", "Description");
            addRow(loginSheet, 1,
                "TC_LOGIN_NEG_01",
                "wrong@email.com", "wrongpassword",
                "Completely unknown user — must show login error");
            addRow(loginSheet, 2,
                "TC_LOGIN_NEG_02",
                "notregistered@test.com", "Test@1234",
                "Valid email format but account not registered — must show login error");
            addRow(loginSheet, 3,
                "TC_LOGIN_NEG_03",
                "vishal.user@gmail.com", "wrongpass",
                "Registered user with wrong password — must show login error");

            // ── Sheet 2: SignupNegativeData ───────────────────────────────────
            // Used by SignupPageTest.testSignupNegativeValidations()
            // Each row = one invalid input combination; form must block submission.
            Sheet signupSheet = wb.createSheet("SignupNegativeData");
            addRow(signupSheet, 0,
                "TestCaseId", "Name", "Phone", "Email", "Password", "Description");
            addRow(signupSheet, 1,
                "TC_SIGNUP_NEG_01",
                "",                        // empty name — validation trigger
                "9999999999",
                "neg1@test.com",
                "Test@1234",
                "Empty name — form must block submission");
            addRow(signupSheet, 2,
                "TC_SIGNUP_NEG_02",
                "Test User",
                "abc",                     // non-numeric phone — validation trigger
                "neg2@test.com",
                "Test@1234",
                "Non-numeric phone — form must block submission");
            addRow(signupSheet, 3,
                "TC_SIGNUP_NEG_03",
                "Test User",
                "9999999999",
                "not-an-email@@bad",       // malformed email — validation trigger
                "Test@1234",
                "Invalid email format — form must block submission");
            addRow(signupSheet, 4,
                "TC_SIGNUP_NEG_04",
                "Test User",
                "9999999999",
                "neg4@test.com",
                "123",                     // too short — validation trigger
                "Password too short — form must block submission");

            // ── Sheet 3: AuthNegativeData ─────────────────────────────────────
            // Used by AuthApiTest.signupNegativeScenarios()
            // MissingField: "none" → duplicate-email test; "email"/"name" → missing-field test.
            Sheet authSheet = wb.createSheet("AuthNegativeData");
            addRow(authSheet, 0,
                "TestCaseId", "FullName", "Phone", "Password", "MissingField", "Description");
            addRow(authSheet, 1,
                "TC_AUTH_NEG_01",
                "Duplicate User", "9000000001", "anyPass123",
                "none",                    // send ApiConstants.CUSTOMER_EMAIL (already exists)
                "Duplicate email signup — expects 400 with 'Email already exists'");
            addRow(authSheet, 2,
                "TC_AUTH_NEG_02",
                "Missing Email User", "9000000002", "Test@123",
                "email",                   // email field omitted from request body
                "Email field omitted — expects 400/403/500 (CE-DEF-001)");
            addRow(authSheet, 3,
                "TC_AUTH_NEG_03",
                "Missing Name User", "9000000003", "Test@456",
                "name",                    // fullName field omitted from request body
                "Name field omitted — expects 400/403/500 (CE-DEF-001)");

            // Write workbook to disk
            try (FileOutputStream fos = new FileOutputStream(file)) {
                wb.write(fos);
            }

            System.out.println("[TestDataProvider] ✔ Excel test data created successfully.");
            System.out.println("[TestDataProvider]   Sheets: LoginNegativeData (3 rows), "
                + "SignupNegativeData (4 rows), AuthNegativeData (3 rows)");

        } catch (IOException e) {
            throw new RuntimeException(
                "[TestDataProvider] Failed to generate Excel test data file: "
                + file.getAbsolutePath(), e);
        }
    }

    /** Convenience helper — creates a row and populates each cell with a string value. */
    private static void addRow(Sheet sheet, int rowIndex, String... values) {
        Row row = sheet.createRow(rowIndex);
        for (int i = 0; i < values.length; i++) {
            row.createCell(i).setCellValue(values[i]);
        }
    }
}
