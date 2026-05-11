package com.gigsound.e2e;

import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractE2ETest {

    protected static final String SELENIUM_URL =
            System.getenv().getOrDefault("SELENIUM_URL", "http://localhost:4444/wd/hub");
    protected static final String FRONTEND_URL =
            System.getenv().getOrDefault("FRONTEND_URL", "http://localhost:3000");

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeAll
    void startBrowser() throws Exception {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1400,900");
        driver = new RemoteWebDriver(URI.create(SELENIUM_URL).toURL(), options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterAll
    void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    protected void openHome() {
        driver.get(FRONTEND_URL);
    }

    protected WebElement loginAsFirstUser() {
        return loginAsFirstUser(3);
    }

    protected WebElement loginAsFirstUser(int attempts) {
        openHome();
        By userBtn = By.xpath("(//button[contains(., '@')])[1]");
        for (int i = 0; i < attempts; i++) {
            try {
                WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(8))
                        .until(ExpectedConditions.elementToBeClickable(userBtn));
                btn.click();
                return btn;
            } catch (Exception e) {
                if (i == attempts - 1) throw e;
                driver.navigate().refresh();
            }
        }
        throw new IllegalStateException("unreachable");
    }

    protected WebElement waitForButtonWithText(String text) {
        return wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[contains(normalize-space(.), '" + text + "')]")));
    }

    protected void clickButtonWithText(String text) {
        waitForButtonWithText(text).click();
    }

    protected boolean pageContainsText(String text) {
        try {
            wait.until((ExpectedCondition<Boolean>) d ->
                    d.findElement(By.tagName("body")).getText().contains(text));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected List<WebElement> findButtonsByText(String text) {
        return driver.findElements(
                By.xpath("//button[contains(normalize-space(.), '" + text + "')]"));
    }
}
