package com.cts.connectease.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By emailInput    = By.cssSelector("input[type='email']");
    private final By passwordInput = By.cssSelector("input[type='password']");
    private final By signInButton  = By.cssSelector("button[type='submit']");
    // Primary error locator; expanded fallbacks used in isErrorMessageDisplayed/getErrorMessage
    private final By errorBox      = By.cssSelector(".error-box");
    private final By signUpLink    = By.cssSelector("a[routerlink='/signup']");

    // Broad error locators — covers toast, inline validation, alert boxes
    private static final org.openqa.selenium.By[] ERROR_LOCATORS = {
        org.openqa.selenium.By.cssSelector(".error-box"),
        org.openqa.selenium.By.cssSelector("[class*='error']"),
        org.openqa.selenium.By.cssSelector("[class*='Error']"),
        org.openqa.selenium.By.cssSelector("[role='alert']"),
        org.openqa.selenium.By.cssSelector(".alert-danger"),
        org.openqa.selenium.By.cssSelector("[class*='toast']"),
        org.openqa.selenium.By.cssSelector("[class*='notification']"),
        org.openqa.selenium.By.cssSelector("[class*='invalid']"),
        org.openqa.selenium.By.cssSelector("[class*='warning']"),
        org.openqa.selenium.By.xpath(
            "//*[contains(@class,'error') or contains(@class,'Error') or @role='alert']")
    };

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    // ── Visual helper ─────────────────────────────────────────────────────────

    private void highlight(WebElement el) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(
                "arguments[0].style.outline='3px solid #f59e0b';" +
                "arguments[0].style.backgroundColor='#fef9c3';", el);
            js.executeScript(
                "arguments[0].style.outline='';" +
                "arguments[0].style.backgroundColor='';", el);
        } catch (Exception ignored) {}
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void navigateTo(String baseUrl) {
        driver.get(baseUrl + "/login");
        wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
    }

    public void enterEmail(String email) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(emailInput));
        highlight(field);
        field.clear();
        field.sendKeys(email);
    }

    public void enterPassword(String password) {
        WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(passwordInput));
        highlight(field);
        field.clear();
        field.sendKeys(password);
    }

    public void clickSignIn() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(signInButton));
        highlight(btn);
        btn.click();
    }

    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickSignIn();
    }

    // ── Verifications ─────────────────────────────────────────────────────────

    public boolean isErrorMessageDisplayed() {
        // Try primary locator first (fast path)
        try {
            if (wait.until(ExpectedConditions.visibilityOfElementLocated(errorBox)).isDisplayed())
                return true;
        } catch (Exception ignored) {}
        // Fall back through broad locators
        for (org.openqa.selenium.By loc : ERROR_LOCATORS) {
            try {
                java.util.List<WebElement> els = driver.findElements(loc);
                for (WebElement el : els) {
                    if (el.isDisplayed() && !el.getText().trim().isEmpty()) return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    public String getErrorMessage() {
        // Try primary locator first
        try {
            WebElement box = wait.until(ExpectedConditions.visibilityOfElementLocated(errorBox));
            String t = box.getText().trim();
            if (!t.isEmpty()) return t;
        } catch (Exception ignored) {}
        // Fall back through broad locators
        for (org.openqa.selenium.By loc : ERROR_LOCATORS) {
            try {
                java.util.List<WebElement> els = driver.findElements(loc);
                for (WebElement el : els) {
                    if (el.isDisplayed()) {
                        String t = el.getText().trim();
                        if (!t.isEmpty()) return t;
                    }
                }
            } catch (Exception ignored) {}
        }
        return "";
    }

    public boolean isSignInButtonDisabled() {
        String disabled = driver.findElement(signInButton).getAttribute("disabled");
        return disabled != null;
    }

    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }

    public boolean isSignUpLinkPresent() {
        return !driver.findElements(signUpLink).isEmpty();
    }
}
