package com.cts.connectease.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features  = "src/test/resources/features/regression",
    glue      = "com.cts.connectease.bdd",
    tags      = "@Regression",
    plugin    = {
        "pretty",
        "html:test-output/bdd-reports/regression/cucumber-report.html",
        "json:test-output/bdd-reports/regression/cucumber-report.json",
        "junit:test-output/bdd-reports/regression/cucumber-report.xml",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome = true,
    dryRun     = false,
    publish    = false
)
public class RegressionTestRunner extends AbstractTestNGCucumberTests {}
