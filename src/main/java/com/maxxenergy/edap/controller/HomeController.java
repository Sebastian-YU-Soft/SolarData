package com.maxxenergy.edap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String redirectToHome() {
        logger.debug("Root path accessed, redirecting to home");
        return "redirect:/home";
    }

    @GetMapping(value = "/home", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String homePage() {
        logger.debug("Serving home page");
        return PageTemplateService.getPageTemplate("Home · MAXX Energy EDAP", "home", getHomeContent());
    }

    private String getHomeContent() {
        return """
                <section class="hero">
                  <div class="wrap hero-grid">
                    <div>
                      <h1>Welcome to MAXX Energy EDAP</h1>
                      <p class="lead">
                        Your comprehensive portal for solar energy data, insights, and management tools.
                        Access real-time generation data, revenue analytics, and system performance metrics.
                      </p>
                      <div class="cta">
                        <a class="btn primary" href="/register">Create Account</a>
                        <a class="btn" href="/data">View Data</a>
                        <a class="btn" href="/about">Learn More</a>
                      </div>
                    </div>
                    <div class="logo-hero panel">
                      <div class="home-stats">
                        <div class="quick-stat">
                          <div class="stat-value" id="homeGeneration">Loading...</div>
                          <div class="stat-label">Current Generation</div>
                        </div>
                        <div class="quick-stat">
                          <div class="stat-value" id="homeRevenue">Loading...</div>
                          <div class="stat-label">Total Revenue</div>
                        </div>
                      </div>
                    </div>
                  </div>
                </section>
                
                <main class="wrap">
                  <section class="features">
                    <div class="cards">
                      <div class="card">
                        <h3>📊 Real-time Data</h3>
                        <p class="muted">Monitor solar generation and performance metrics in real-time across all facilities.</p>
                      </div>
                      <div class="card">
                        <h3>🔒 Secure Access</h3>
                        <p class="muted">Role-based authentication ensures the right data reaches the right stakeholders.</p>
                      </div>
                      <div class="card">
                        <h3>📈 Analytics</h3>
                        <p class="muted">Comprehensive reporting and analytics tools for informed decision making.</p>
                      </div>
                    </div>
                  </section>
                </main>
                
                """ + getHomePageScript();
    }

    private String getHomePageScript() {
        return """
                <script>
                    // Load initial data
                    fetch('/api/public/data')
                        .then(response => response.json())
                        .then(data => {
                            document.getElementById('homeGeneration').textContent = data.generation.toFixed(1) + ' MW';
                            document.getElementById('homeRevenue').textContent = '$' + data.revenue.toLocaleString();
                        })
                        .catch(error => {
                            document.getElementById('homeGeneration').textContent = 'N/A';
                            document.getElementById('homeRevenue').textContent = 'N/A';
                        });
                </script>
                """;
    }
}