package com.gigsound.e2e;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

class PurchaseTicketE2ETest extends AbstractE2ETest {

    @Test
    @DisplayName("Logged-in user can open the purchase dialog and submit a payment")
    void purchaseTicket_dialogFlow() {
        loginAsFirstUser();
        clickButtonWithText("Events");

        WebElement buyButton = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("(//button[contains(normalize-space(.), 'Buy Tickets')])[1]")));
        buyButton.click();

        WebElement quantityInput = new WebDriverWait(driver, Duration.ofSeconds(15))
                .until(ExpectedConditions.visibilityOfElementLocated(
                        By.cssSelector("input[type='number']")));
        quantityInput.clear();
        quantityInput.sendKeys("2");

        WebElement payButton = new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//button[contains(normalize-space(.), 'Pay with PayPal')]")));

        assertTrue(payButton.isEnabled(), "Pay button must be enabled before clicking");
        assertTrue(pageContainsText("Purchase Tickets"), "Dialog header should be visible");

        payButton.click();
    }
}
