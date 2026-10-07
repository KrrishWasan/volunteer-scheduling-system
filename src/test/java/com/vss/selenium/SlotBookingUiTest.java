package com.vss.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Journey 2 – Slot booking request (VSS-2, VSS-3).
 * A registered volunteer books the first available slot and receives a
 * PENDING booking with a reference number.
 */
class SlotBookingUiTest extends SeleniumBase {

    @Test
    void volunteerCanRequestASlotBooking() {
        String email = uniqueEmail("uibook");
        registerVolunteer("UI Booker", email);

        String statusUrl = bookFirstAvailableSlot(email);
        assertTrue(statusUrl.contains("ref=VSS-"),
                "Expected a booking reference in the URL, got: " + statusUrl);

        String body = visible(By.id("bookings-table")).getText();
        assertTrue(body.contains("PENDING"), "New booking should be PENDING, table shows: " + body);
    }
}
