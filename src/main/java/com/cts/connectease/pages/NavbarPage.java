package com.cts.connectease.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class NavbarPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    private final By signInBtn      = By.cssSelector("a.btn-signin");
    private final By getStartedBtn  = By.cssSelector("a.btn-signup");
    private final By chatsLink      = By.cssSelector("a[routerlink='/chats']");
    private final By avatarBtn      = By.cssSelector(".avatar-btn");
    private final By signOutBtn     = By.cssSelector("button.logout");
    private final By logoContainer  = By.cssSelector(".logo-container");

    public NavbarPage(WebDriver driver) {
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

    // ── Visibility checks ─────────────────────────────────────────────────────

    public boolean isSignInVisible() {
        return !driver.findElements(signInBtn).isEmpty();
    }

    public boolean isGetStartedVisible() {
        return !driver.findElements(getStartedBtn).isEmpty();
    }

    public boolean isChatsLinkVisible() {
        return !driver.findElements(chatsLink).isEmpty();
    }

    public boolean isAvatarVisible() {
        return !driver.findElements(avatarBtn).isEmpty();
    }

    // ── Actions ───────────────────────────────────────────────────────────────

    public void clickAvatar() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(avatarBtn));
        highlight(el);
        el.click();
    }

    public void clickSignOut() {
        clickAvatar();
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(signOutBtn));
        highlight(el);
        el.click();
    }

    public void clickSignIn() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(signInBtn));
        highlight(el);
        el.click();
    }

    public void clickLogo() {
        WebElement el = wait.until(ExpectedConditions.elementToBeClickable(logoContainer));
        highlight(el);
        el.click();
    }
}
