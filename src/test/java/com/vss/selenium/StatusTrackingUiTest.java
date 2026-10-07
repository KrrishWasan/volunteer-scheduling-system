package com.vss.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Journey 3 – Booking status tracking (VSS-6), by reference and by e-mail.
 */
class StatusTrackingUiTest extends SeleniumBase {

    @Test
    void volunteerCanTrackBookingByReferenceAndEmail() {
        String email = uniqueEmail("uitrack");
        registerVolunteer("UI Tracker", email);
        String ref = referenceFromUrl(bookFirstAvailableSlot(email));

        // Track by reference.
        open("/status?ref=" + ref);
        assertTrue(visible(By.id("bookings-table")).getText().contains(ref),
                "Booking " + ref + " not found by reference.");

        // Track by e-mail.
        open("/status");
        visible(By.id("email")).sendKeys(email);
        driver().findElement(By.id("btn-track-email")).click();
        assertTrue(visible(By.id("bookings-table")).getText().contains(ref),
                "Booking " + ref + " not listed for e-mail " + email + ".");
    }
}
