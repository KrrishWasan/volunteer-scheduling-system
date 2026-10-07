package com.vss.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Journey 5 – Booking cancellation (VSS-5).
 * A volunteer cancels a booking and the status becomes CANCELLED.
 */
class CancellationUiTest extends SeleniumBase {

    @Test
    void volunteerCanCancelABooking() {
        String email = uniqueEmail("uicancel");
        registerVolunteer("UI Canceller", email);
        String ref = referenceFromUrl(bookFirstAvailableSlot(email));

        open("/status?ref=" + ref);
        visible(By.id("bookings-table"));
        driver().findElement(By.cssSelector(".btn-cancel")).click();

        open("/status?ref=" + ref);
        assertTrue(visible(By.id("bookings-table")).getText().contains("CANCELLED"),
                "Booking " + ref + " was not cancelled.");
    }
}
