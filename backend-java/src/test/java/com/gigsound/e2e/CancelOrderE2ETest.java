package com.gigsound.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

class CancelOrderE2ETest extends AbstractE2ETest {

    @Test
    @DisplayName("Logged-in user can cancel one of their orders from My Tickets")
    void cancelOrder_success() {
        loginAsFirstUser();
        clickButtonWithText("My Tickets");

        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> !findButtonsByText("Cancel Order").isEmpty()
                        || d.findElement(By.tagName("body")).getText().contains("No Tickets Yet"));

        List<WebElement> cancelButtons = findButtonsByText("Cancel Order");
        if (cancelButtons.isEmpty()) {
            org.junit.jupiter.api.Assumptions.abort(
                    "User has no orders to cancel — run PurchaseTicketE2ETest first");
        }

        int before = cancelButtons.size();
        cancelButtons.get(0).click();

        new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(d -> findButtonsByText("Cancel Order").size() < before
                        || d.findElement(By.tagName("body")).getText().contains("No Tickets Yet"));

        assertTrue(findButtonsByText("Cancel Order").size() < before
                        || pageContainsText("No Tickets Yet"),
                "The cancelled order should disappear from My Tickets");
    }
}
