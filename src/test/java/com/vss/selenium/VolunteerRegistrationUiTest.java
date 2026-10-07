package com.vss.selenium;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Journey 1 – Volunteer registration (VSS-1).
 * Covers valid registration and duplicate-e-mail rejection.
 */
class VolunteerRegistrationUiTest extends SeleniumBase {

    @Test
    void volunteerCanRegisterAndDuplicateIsRejected() {
        String email = uniqueEmail("uireg");

        // Valid registration lands on the slots page with a success message.
        registerVolunteer("UI Volunteer", email);
        assertTrue(driver().getCurrentUrl().contains("/slots"),
                "Expected redirect to /slots after registration, got: " + driver().getCurrentUrl());
        assertTrue(visible(By.id("success-box")).getText().contains("Registration successful"),
                "Success message not shown after registration.");

        // Duplicate e-mail stays on the form with an error.
        open("/register");
        visible(By.id("fullName")).sendKeys("UI Volunteer");
        driver().findElement(By.id("email")).sendKeys(email);
        driver().findElement(By.id("phone")).sendKeys("9876543210");
        driver().findElement(By.id("btn-register")).click();
        assertTrue(visible(By.id("error-box")).getText().contains("already registered"),
                "Duplicate e-mail was not rejected.");
    }
}
