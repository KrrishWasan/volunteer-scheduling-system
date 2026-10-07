package com.vss.web;

import com.vss.config.AppInfo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;

@Controller
public class HomeController {

    private final AppInfo appInfo;

    public HomeController(AppInfo appInfo) {
        this.appInfo = appInfo;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    /** Lightweight version/environment endpoint used by pipelines and health checks. */
    @GetMapping("/api/version")
    @ResponseBody
    public Map<String, String> version() {
        Map<String, String> info = new LinkedHashMap<>();
        info.put("name", appInfo.getName());
        info.put("version", appInfo.getVersion());
        info.put("environment", appInfo.getEnv());
        return info;
    }
}
