package com.cts.connectease.bdd.reports;

import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * BddSuiteListener — runs once before any scenario in the suite.
 * Cleans previous reports/screenshots and sets a datetime-stamped ExtentReport path.
 */
public class BddSuiteListener implements ISuiteListener {

    private static final String REPORTS_DIR     = "test-output/bdd-reports";
    private static final String SCREENSHOTS_DIR = "test-output/bdd-screenshots";

    @Override
    public void onStart(ISuite suite) {
        String timestamp  = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        System.setProperty("bdd.run.timestamp", timestamp);

        String reportPath = REPORTS_DIR + "/ConnectEase_BDD_Report_" + timestamp + ".html";
        System.setProperty("extent.reporter.spark.out", reportPath);

        int deletedReports     = cleanDirectory(new File(REPORTS_DIR));
        int deletedScreenshots = cleanDirectory(new File(SCREENSHOTS_DIR));

        System.out.println("\n[BddSuiteListener] Deleted " + deletedReports
                + " report(s) and " + deletedScreenshots + " screenshot(s).");
        System.out.println("[BddSuiteListener] Run timestamp : " + timestamp);
        System.out.println("[BddSuiteListener] ExtentReport  : " + reportPath);

        new File(REPORTS_DIR).mkdirs();
        new File(SCREENSHOTS_DIR).mkdirs();
    }

    @Override
    public void onFinish(ISuite suite) {}

    private static int cleanDirectory(File dir) {
        if (!dir.exists()) return 0;
        File[] entries = dir.listFiles();
        if (entries == null) return 0;
        int count = 0;
        for (File entry : entries) {
            if (entry.isDirectory()) {
                count += cleanDirectory(entry);
                entry.delete();
            } else if (entry.delete()) {
                count++;
            }
        }
        return count;
    }
}
