package com.vss.selenium;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Saves a PNG screenshot to {@code target/screenshots} whenever a Selenium
 * test fails. Registered via {@code @ExtendWith} on {@link SeleniumBase}.
 */
public class ScreenshotOnFailure implements TestWatcher {

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        WebDriver driver = SeleniumBase.getDriver();
        if (driver == null) {
            return;
        }
        try {
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);
            String name = context.getRequiredTestClass().getSimpleName()
                    + "-" + context.getRequiredTestMethod().getName() + ".png";
            Path target = dir.resolve(name);
            byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Files.write(target, png);
            System.out.println("[selenium] failure screenshot saved to " + target);
        } catch (IOException | RuntimeException e) {
            System.out.println("[selenium] could not save failure screenshot: " + e);
        }
    }

}
