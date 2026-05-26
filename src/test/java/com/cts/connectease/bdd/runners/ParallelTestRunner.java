package com.cts.connectease.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.testng.annotations.DataProvider;

@CucumberOptions(
    features  = "src/test/resources/features",
    glue      = "com.cts.connectease.bdd",
    tags      = "@Smoke or @Regression",
    plugin    = {
        "pretty",
        "html:test-output/bdd-reports/parallel/cucumber-report.html",
        "json:test-output/bdd-reports/parallel/cucumber-report.json",
        "junit:test-output/bdd-reports/parallel/cucumber-report.xml",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome = true,
    dryRun     = false,
    publish    = false
)
public class ParallelTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {
        return super.scenarios();
    }
}
