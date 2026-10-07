package com.vss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the Volunteer Scheduling System (VSS).
 * Runs with embedded Tomcat via {@code java -jar vss.war}.
 */
@SpringBootApplication
public class VolunteerSchedulingApplication {

    public static void main(String[] args) {
        SpringApplication.run(VolunteerSchedulingApplication.class, args);
    }
}
