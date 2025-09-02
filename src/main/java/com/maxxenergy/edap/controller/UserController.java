package com.maxxenergy.edap.controller;

import com.maxxenergy.edap.service.PageTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping(value = "/user", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String userPage() {
        logger.debug("Serving user profile page");
        return PageTemplateService.getPageTemplate("User Profile · MAXX Energy EDAP", "user", getUserContent());
    }

    private String getUserContent() {
        return """
            <section class="hero-small">
              <div class="wrap">
                <h1>User Profile</h1>
                <p class="lead">Manage your account and preferences.</p>
              </div>
            </section>
            <main class="wrap">
              <div class="panel">
                <h2>Profile Information</h2>
                <p>User profile functionality coming soon.</p>
                <a href="/register" class="btn primary">Create Account</a>
              </div>
            </main>
            """;
    }
}