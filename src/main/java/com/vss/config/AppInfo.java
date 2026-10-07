package com.vss.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Environment-specific settings. {@code app.env} is the parameterised
 * environment setting supplied by Jenkins / Docker / Ansible (APP_ENV).
 */
@Component("appInfo")
public class AppInfo {

    private final String env;
    private final String version;
    private final String name;

    public AppInfo(@Value("${app.env:dev}") String env,
                   @Value("${app.version:1.0.0}") String version,
                   @Value("${app.name:Volunteer Scheduling System}") String name) {
        this.env = env;
        this.version = version;
        this.name = name;
    }

    public String getEnv() {
        return env;
    }

    public String getVersion() {
        return version;
    }

    public String getName() {
        return name;
    }
}
