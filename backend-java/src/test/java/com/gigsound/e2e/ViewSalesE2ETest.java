package com.gigsound.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class ViewSalesE2ETest extends AbstractE2ETest {

    @Test
    @DisplayName("Admin can view sales for an event in the Sales Dashboard")
    void viewSales_success() {
        loginAsFirstUser();
        clickButtonWithText("Admin");
        clickButtonWithText("Sales Dashboard");

        List<WebElement> eventButtons = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(d -> {
                    List<WebElement> btns = d.findElements(
                            By.xpath("//button[contains(normalize-space(.), '-')]"));
                    return btns.isEmpty() ? null : btns;
                });
        eventButtons.get(0).click();

        clickButtonWithText("Load Sales Data");

        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.textToBePresentInElementLocated(
                        By.tagName("body"), "Total Sales"));

        assertTrue(pageContainsText("Total Sales"), "Sales card should appear");
        assertTrue(pageContainsText("Total Revenue"), "Revenue card should appear");
    }
}
