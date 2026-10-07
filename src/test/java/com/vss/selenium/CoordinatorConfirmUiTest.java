package com.vss.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Journey 4 – Coordinator confirmation (VSS-4).
 * A pending booking is confirmed on the coordinator dashboard.
 */
class CoordinatorConfirmUiTest extends SeleniumBase {

    @Test
    void coordinatorCanConfirmAPendingBooking() {
        String email = uniqueEmail("uiconfirm");
        registerVolunteer("UI Confirmee", email);
        String ref = referenceFromUrl(bookFirstAvailableSlot(email));

        open("/coordinator");
        visible(By.id("all-bookings-table"));
        driver().findElement(By.xpath(
                "//td[@class='booking-ref' and normalize-space(text())='" + ref + "']"
                        + "/following-sibling::td//button[contains(@class,'btn-confirm')]")).click();

        open("/status?ref=" + ref);
        assertTrue(visible(By.id("bookings-table")).getText().contains("CONFIRMED"),
                "Booking " + ref + " was not confirmed.");
    }
}
