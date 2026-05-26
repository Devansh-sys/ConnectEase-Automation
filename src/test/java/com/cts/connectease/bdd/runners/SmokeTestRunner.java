package com.cts.connectease.bdd.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
    features  = "src/test/resources/features/smoke",
    glue      = "com.cts.connectease.bdd",
    tags      = "@Smoke",
    plugin    = {
        "pretty",
        "html:test-output/bdd-reports/smoke/cucumber-report.html",
        "json:test-output/bdd-reports/smoke/cucumber-report.json",
        "junit:test-output/bdd-reports/smoke/cucumber-report.xml",
        "com.aventstack.extentreports.cucumber.adapter.ExtentCucumberAdapter:"
    },
    monochrome = true,
    dryRun     = false,
    publish    = false
)
public class    SmokeTestRunner extends AbstractTestNGCucumberTests {}
