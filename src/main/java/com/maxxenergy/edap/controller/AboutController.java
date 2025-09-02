package com.maxxenergy.edap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main controller for serving HTML pages and static content.
 * Handles all the public-facing web pages of the EDAP application.
 */
@Controller
public class AboutController {

    private static final Logger logger = LoggerFactory.getLogger(AboutController.class);

    @GetMapping("/")
    public String redirectToHome() {
        logger.debug("Root path accessed, redirecting to home");
        return "redirect:/home";
    }

    @GetMapping(value = "/home", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String homePage() {
        logger.debug("Serving home page");
        return getPageTemplate("Home · MAXX Energy EDAP", "home", getHomeContent());
    }

    @GetMapping(value = "/about", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String aboutPage() {
        logger.debug("Serving about page");
        return getPageTemplate("About · MAXX Energy EDAP", "about", getAboutContent());
    }

    @GetMapping(value = "/blog", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String blogPage() {
        logger.debug("Serving blog page");
        return getPageTemplate("Blog · MAXX Energy EDAP", "blog", getBlogContent());
    }

    @GetMapping(value = "/data", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String dataPage() {
        logger.debug("Serving data dashboard page");
        return getPageTemplate("Data Dashboard · MAXX Energy EDAP", "data", getDataContent());
    }

    @GetMapping(value = "/user", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String userPage() {
        logger.debug("Serving user profile page");
        return getPageTemplate("User Profile · MAXX Energy EDAP", "user", getUserContent());
    }

    @GetMapping(value = "/contact", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String contactPage() {
        logger.debug("Serving contact page");
        return getPageTemplate("Contact Us · MAXX Energy EDAP", "contact", getContactContent());
    }

    @GetMapping(value = "/faq", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String faqPage() {
        logger.debug("Serving FAQ page");
        return getPageTemplate("FAQ · MAXX Energy EDAP", "faq", getFaqContent());
    }

    @GetMapping(value = "/data-input-info", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String dataInputInfoPage() {
        logger.debug("Serving data input info page");
        return getPageTemplate("Data Input · MAXX Energy EDAP", "data-input", getDataInputInfoContent());
    }

    @GetMapping(value = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String health() {
        logger.debug("Health check requested");
        return "OK";
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

    private String getAboutContent() {
        return """
            <section class="hero">
              <div class="wrap hero-grid">
                <div>
                  <h1>About the Enterprise Data Access Portal</h1>
                  <p class="lead">
                    EDAP gives MAXX Energy stakeholders on-demand, trustworthy access to solar plant
                    generation and revenue data. Public insights for everyone, secure detail for authorized roles.
                  </p>
                  <div class="cta">
                    <a class="btn primary" href="/register">Create Account</a>
                    <a class="btn" href="/contact">Get in touch</a>
                    <a class="btn" href="#mission">Our mission</a>
                  </div>
                </div>
                <div class="logo-hero panel">
                  <div style="width:200px;height:120px;background:var(--brand);border-radius:12px;display:flex;align-items:center;justify-content:center;color:white;font-weight:bold;font-size:32px;">MAXX Energy</div>
                </div>
              </div>
            </section>

            <main class="wrap">
              <section id="mission">
                <div class="panel">
                  <h2>Our Mission</h2>
                  <p class="muted">Deliver a secure, human-friendly portal that exposes the right energy data to the right users at the right time.</p>
                  <div class="grid2" style="margin-top:14px">
                    <div>
                      <ul>
                        <li>Public data viewable without login</li>
                        <li>Private data secured with authentication & role-based authorization</li>
                        <li>Clear visualizations with filters and drilldowns</li>
                        <li>Well-defined APIs between Application, Data, and Security</li>
                      </ul>
                    </div>
                    <div class="card">
                      <strong>Definition of Done</strong>
                      <ul>
                        <li>Accessible page, responsive on mobile/desktop</li>
                        <li>Contact info visible</li>
                        <li>Team + history present</li>
                      </ul>
                    </div>
                  </div>
                </div>
              </section>

              <section id="history">
                <div class="panel">
                  <h2>Our History</h2>
                  <div class="timeline">
                    <div class="tl"><strong>2019</strong> — Concept for a unified energy data portal.</div>
                    <div class="tl"><strong>2022</strong> — Pilot with internal stakeholders.</div>
                    <div class="tl"><strong>2024</strong> — Role-based access model refined.</div>
                    <div class="tl"><strong>2025</strong> — Cohort-led build: public + private views with 8–10 visualizations.</div>
                  </div>
                </div>
              </section>

              <section id="team">
                <div class="cards">
                  <div class="card">
                    <h3>Agile Coach / Scrum Master</h3>
                    <p class="muted">Leads planning, standups, and manages dependencies across DevOps, Data, Security.</p>
                  </div>
                  <div class="card">
                    <h3>DevOps</h3>
                    <p class="muted">Builds the web app, APIs, and UI—ensuring smooth, responsive access to data.</p>
                  </div>
                  <div class="card">
                    <h3>Data & Security</h3>
                    <p class="muted">Embeds visualizations with filters/drilldowns and protects private data via authz/authn.</p>
                  </div>
                </div>
              </section>
            </main>
            """;
    }

    private String getBlogContent() {
        return """
            <section class="hero-small">
              <div class="wrap">
                <h1>MAXX Energy Blog</h1>
                <p class="lead">Latest insights, updates, and industry news from our energy experts.</p>
              </div>
            </section>

            <main class="wrap">
              <section class="blog-posts">
                <div class="blog-grid">
                  <article class="blog-card">
                    <div class="blog-meta">
                      <span class="date">January 15, 2025</span>
                      <span class="category">Technology</span>
                    </div>
                    <h3>Enhanced Data Visualization Features</h3>
                    <p class="muted">We've rolled out new interactive charts and real-time monitoring capabilities to help you better understand your solar performance data.</p>
                    <a href="#" class="read-more">Read more →</a>
                  </article>

                  <article class="blog-card">
                    <div class="blog-meta">
                      <span class="date">January 10, 2025</span>
                      <span class="category">Industry News</span>
                    </div>
                    <h3>Solar Energy Trends for 2025</h3>
                    <p class="muted">Exploring the latest developments in solar technology and what they mean for renewable energy adoption this year.</p>
                    <a href="#" class="read-more">Read more →</a>
                  </article>

                  <article class="blog-card">
                    <div class="blog-meta">
                      <span class="date">January 5, 2025</span>
                      <span class="category">Platform Updates</span>
                    </div>
                    <h3>New Security Features Deployed</h3>
                    <p class="muted">Learn about our latest security enhancements and improved role-based access controls for enterprise users.</p>
                    <a href="#" class="read-more">Read more →</a>
                  </article>

                  <article class="blog-card">
                    <div class="blog-meta">
                      <span class="date">December 28, 2024</span>
                      <span class="category">Case Study</span>
                    </div>
                    <h3>Customer Success Story: 40% Efficiency Increase</h3>
                    <p class="muted">How one of our enterprise clients used EDAP data insights to significantly improve their solar farm performance.</p>
                    <a href="#" class="read-more">Read more →</a>
                  </article>
                </div>
              </section>
            </main>
            """;
    }

    private String getDataContent() {
        return """
            <section class="hero-small">
              <div class="wrap">
                <h1>Solar Data Dashboard</h1>
                <p class="lead">Real-time monitoring and analytics for all solar installations.</p>
              </div>
            </section>

            <main class="wrap">
              <section class="data-dashboard">
                <div class="dashboard-header">
                  <h2>Live Performance Data</h2>
                  <div class="refresh-indicator">
                    <span class="status-dot"></span>
                    <span>Live</span>
                  </div>
                </div>

                <div id="solarData" class="loading">Loading solar data...</div>

                <div class="data-controls">
                  <div class="control-group">
                    <label>Time Range:</label>
                    <select class="control-select">
                      <option>Last 24 Hours</option>
                      <option>Last 7 Days</option>
                      <option>Last 30 Days</option>
                      <option>Custom Range</option>
                    </select>
                  </div>

                  <div class="control-group">
                    <label>Plant Filter:</label>
                    <select class="control-select">
                      <option>All Plants</option>
                      <option>Public Plants Only</option>
                      <option>High Performance</option>
                    </select>
                  </div>
                </div>
              </section>
            </main>

            """ + getDataPageScript();
    }

    private String getUserContent() {
        return """
            <section class="hero-small">
              <div class="wrap">
                <h1>User Profile</h1>
                <p class="lead">Manage your account settings and access permissions.</p>
              </div>
            </section>

            <main class="wrap">
              <section class="user-profile">
                <div class="grid2">
                  <div class="panel">
                    <h2>Profile Information</h2>
                    <div class="profile-section">
                      <div class="avatar">
                        <div class="avatar-circle">JD</div>
                      </div>
                      <div class="profile-info">
                        <h3>John Doe</h3>
                        <p class="muted">john.doe@maxxenergy.com</p>
                        <p class="role-badge">Manager</p>
                      </div>
                    </div>

                    <div class="info-grid">
                      <div class="info-item">
                        <label>Department:</label>
                        <span>Operations</span>
                      </div>
                      <div class="info-item">
                        <label>Location:</label>
                        <span>New York, NY</span>
                      </div>
                      <div class="info-item">
                        <label>Joined:</label>
                        <span>March 2024</span>
                      </div>
                      <div class="info-item">
