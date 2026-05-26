package com.cts.connectease.bdd.stepdefs.common;

import com.cts.connectease.bdd.context.TestContext;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Hooks {

    private static final String SCREENSHOT_DIR = "test-output/bdd-screenshots";

    private final TestContext ctx;

    public Hooks(TestContext ctx) {
        this.ctx = ctx;
    }

    @Before(order = 1)
    public void initBrowser(Scenario scenario) {
        System.out.println("\n─────────────────────────────────────────────");
        System.out.println("▶ SCENARIO: " + scenario.getName());
        System.out.println("  Tags: " + scenario.getSourceTagNames());
        System.out.println("─────────────────────────────────────────────");
        ctx.initDriver();
    }

    @After(order = 1)
    public void tearDown(Scenario scenario) {
        if (scenario.isFailed() && ctx.driver != null) {
            try {
                byte[] screenshot = ((TakesScreenshot) ctx.driver)
                        .getScreenshotAs(OutputType.BYTES);

                // 1. Embed in Cucumber HTML report
                scenario.attach(screenshot, "image/png",
                        "Screenshot on failure: " + scenario.getName());

                // 2. Save as dated PNG file
                String ts = System.getProperty("bdd.run.timestamp",
                        new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()));
                String safeName = scenario.getName()
                        .replaceAll("[^a-zA-Z0-9_\\-]", "_")
                        .replaceAll("_{2,}", "_");
                safeName = safeName.substring(0, Math.min(60, safeName.length()));
                String filename = safeName + "_" + ts + ".png";
                File screenshotFile = new File(SCREENSHOT_DIR, filename);
                screenshotFile.getParentFile().mkdirs();
                Files.write(Paths.get(screenshotFile.getPath()), screenshot);

                System.out.println("📸 Screenshot saved: " + screenshotFile.getPath());
            } catch (IOException | IllegalStateException e) {
                System.out.println("⚠ Screenshot capture failed: " + e.getMessage());
            }
        }

        System.out.println("◀ SCENARIO " + (scenario.isFailed() ? "FAILED ✗" : "PASSED ✓")
                + ": " + scenario.getName());
        System.out.println("─────────────────────────────────────────────\n");

        ctx.quitDriver();
    }

    @AfterStep
    public void visualPause(Scenario scenario) {
        if ("true".equalsIgnoreCase(System.getProperty("visualPause", "true"))
                && !scenario.isFailed()) {
            TestContext.pause(500);
        }
    }
}
