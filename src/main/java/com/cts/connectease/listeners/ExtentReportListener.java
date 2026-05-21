package com.cts.connectease.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * TestNG listener that generates an ExtentReports HTML report.
 *
 * Output: test-output/reports/ConnectEase_Report_<timestamp>.html
 *
 * Each test method gets:
 *   PASS  — green tick + description
 *   FAIL  — red cross + exception message + inline screenshot
 *   SKIP  — orange skip + reason
 */
public class ExtentReportListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> testNode = new ThreadLocal<>();

    // ── Lifecycle ─────────────────────────────────────────────────────────────

    @Override
    public void onStart(ITestContext context) {
        String timestamp  = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        String reportDir  = "test-output/reports/";
        String reportFile = reportDir + "ConnectEase_Report_" + timestamp + ".html";

        // Ensure the reports directory exists
        new File(reportDir).mkdirs();

        ExtentSparkReporter spark = new ExtentSparkReporter(reportFile);
        spark.config().setDocumentTitle("ConnectEase Frontend Automation Report");
        spark.config().setReportName("ConnectEase UI Test Suite");
        spark.config().setTheme(Theme.DARK);
        spark.config().setTimeStampFormat("dd MMM yyyy HH:mm:ss");
        spark.config().setCss(
            ".badge-primary { background-color: #7b68ee !important; }" +
            ".test-status.pass { background-color: #00c853 !important; }" +
            "body { font-family: 'Segoe UI', Arial, sans-serif; }"
        );

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Application",  "ConnectEase");
        extent.setSystemInfo("Environment",  "Staging (Vercel)");
        extent.setSystemInfo("Base URL",     "https://connect-ease-nu.vercel.app");
        extent.setSystemInfo("Browser",      "Chrome (latest)");
        extent.setSystemInfo("Tester",       "Automation Suite");
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("OS",           System.getProperty("os.name"));
        extent.setSystemInfo("Suite",        context.getSuite().getName());
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
    }

    // ── Per-test hooks ────────────────────────────────────────────────────────

    @Override
    public void onTestStart(ITestResult result) {
        String moduleName = getModuleName(result.getTestClass().getName());
        ExtentTest test = extent
                .createTest(result.getMethod().getMethodName(),
                        result.getMethod().getDescription())
                .assignCategory(moduleName);
        testNode.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        testNode.get().log(Status.PASS,
                "<b>PASSED</b> — " + result.getMethod().getMethodName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest test = testNode.get();
        test.log(Status.FAIL,
                "<b>FAILED</b> — " + result.getThrowable().getMessage());

        // Attach screenshot on failure
        try {
            WebDriver driver = getDriver(result);
            if (driver instanceof TakesScreenshot) {
                byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
                String screenshotDir  = "test-output/screenshots/";
                String screenshotName = result.getMethod().getMethodName()
                        + "_" + new SimpleDateFormat("HHmmss").format(new Date()) + ".png";
                String screenshotPath = screenshotDir + screenshotName;

                new File(screenshotDir).mkdirs();
                Files.write(Paths.get(screenshotPath), screenshot);

                test.addScreenCaptureFromPath("../screenshots/" + screenshotName,
                        "Failure screenshot — " + result.getMethod().getMethodName());
            }
        } catch (IOException | IllegalStateException e) {
            test.log(Status.WARNING, "Screenshot could not be captured: " + e.getMessage());
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        String reason = result.getThrowable() != null
                ? result.getThrowable().getMessage()
                : "No reason provided";
        testNode.get().log(Status.SKIP, "<b>SKIPPED</b> — " + reason);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Resolves the WebDriver instance from the test class instance.
     * Works because all test classes extend BaseTest which exposes a
     * protected WebDriver field named "driver".
     */
    private WebDriver getDriver(ITestResult result) {
        try {
            Object instance = result.getInstance();
            java.lang.reflect.Field f = instance.getClass().getSuperclass().getDeclaredField("driver");
            f.setAccessible(true);
            return (WebDriver) f.get(instance);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Maps a fully-qualified test class name to a short module label
     * used for the ExtentReports category tag.
     */
    private String getModuleName(String className) {
        if (className.contains("Signup"))          return "Authentication — Signup";
        if (className.contains("Login"))           return "Authentication — Login";
        if (className.contains("Home"))            return "Home Page";
        if (className.contains("ServiceListings")) return "Service Listings";
        if (className.contains("ServiceDetail"))   return "Service Detail";
        if (className.contains("Reviews"))         return "Reviews & Ratings";
        if (className.contains("VendorDashboard")) return "Vendor Dashboard";
        if (className.contains("Community"))       return "Community Forum";
        if (className.contains("Chat") && !className.contains("AI")) return "Real-Time Chat";
        if (className.contains("AIChat"))          return "AI Chat Assistant";
        if (className.contains("Profile"))         return "User Profile";
        return "Other";
    }
}
