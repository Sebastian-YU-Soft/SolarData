package com.maxxenergy.edap;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;

@Controller
public class AboutController {

    @GetMapping("/")
    public String redirectToHome() {
        return "redirect:/home";
    }

    @GetMapping(value = "/home", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String homePage() {
        return getPageTemplate("Home · MAXX Energy EDAP", "home", getHomeContent());
    }

    @GetMapping(value = "/about", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String aboutPage() {
        return getPageTemplate("About · MAXX Energy EDAP", "about", getAboutContent());
    }

    @GetMapping(value = "/blog", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String blogPage() {
        return getPageTemplate("Blog · MAXX Energy EDAP", "blog", getBlogContent());
    }

    @GetMapping(value = "/data", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String dataPage() {
        return getPageTemplate("Data Dashboard · MAXX Energy EDAP", "data", getDataContent());
    }

    @GetMapping(value = "/user", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String userPage() {
        return getPageTemplate("User Profile · MAXX Energy EDAP", "user", getUserContent());
    }

    @GetMapping(value = "/contact", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String contactPage() {
        return getPageTemplate("Contact Us · MAXX Energy EDAP", "contact", getContactContent());
    }

    @GetMapping(value = "/faq", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String faqPage() {
        return getPageTemplate("FAQ · MAXX Energy EDAP", "faq", getFaqContent());
    }

    @GetMapping(value = "/health", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String health() {
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
                    <a class="btn primary" href="/data">View Data</a>
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
                    <a class="btn primary" href="/contact">Get in touch</a>
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
                        <label>Last Login:</label>
                        <span>Today, 9:34 AM</span>
                      </div>
                    </div>
                  </div>
                  
                  <div class="panel">
                    <h2>Access Permissions</h2>
                    <div class="permissions-list">
                      <div class="permission-item">
                        <span class="permission-name">Public Data Access</span>
                        <span class="permission-status enabled">✓ Enabled</span>
                      </div>
                      <div class="permission-item">
                        <span class="permission-name">Private Data Access</span>
                        <span class="permission-status enabled">✓ Enabled</span>
                      </div>
                      <div class="permission-item">
                        <span class="permission-name">Executive Dashboard</span>
                        <span class="permission-status disabled">✗ Disabled</span>
                      </div>
                      <div class="permission-item">
                        <span class="permission-name">Data Export</span>
                        <span class="permission-status enabled">✓ Enabled</span>
                      </div>
                    </div>
                    
                    <div class="actions">
                      <button class="btn primary">Update Profile</button>
                      <button class="btn">Change Password</button>
                    </div>
                  </div>
                </div>
              </section>
            </main>
            """;
    }

    private String getContactContent() {
        return """
            <section class="hero-small">
              <div class="wrap">
                <h1>Contact Us</h1>
                <p class="lead">Get in touch with our team. We're here to help!</p>
              </div>
            </section>
            
            <main class="wrap">
              <section class="contact-section">
                <div class="grid2">
                  <div class="panel">
                    <h2>Send us a Message</h2>
                    <form class="contact-form" id="contactForm">
                      <div class="form-group">
                        <label for="name">Full Name *</label>
                        <input type="text" id="name" name="name" required class="form-input">
                      </div>
                      
                      <div class="form-group">
                        <label for="email">Email Address *</label>
                        <input type="email" id="email" name="email" required class="form-input">
                      </div>
                      
                      <div class="form-group">
                        <label for="subject">Subject *</label>
                        <select id="subject" name="subject" required class="form-input">
                          <option value="">Select a topic...</option>
                          <option value="technical">Technical Support</option>
                          <option value="data">Data Access Request</option>
                          <option value="billing">Billing Question</option>
                          <option value="feedback">Feedback</option>
                          <option value="other">Other</option>
                        </select>
                      </div>
                      
                      <div class="form-group">
                        <label for="message">Message *</label>
                        <textarea id="message" name="message" rows="5" required class="form-input" placeholder="Please describe your question or concern..."></textarea>
                      </div>
                      
                      <div class="form-group">
                        <label class="checkbox-label">
                          <input type="checkbox" name="newsletter">
                          <span class="checkmark"></span>
                          Subscribe to our newsletter for updates
                        </label>
                      </div>
                      
                      <button type="submit" class="btn primary">Send Message</button>
                    </form>
                  </div>
                  
                  <div class="panel">
                    <h2>Get in Touch</h2>
                    <p class="muted">We usually respond within one business day.</p>
                    
                    <div class="contact-info">
                      <div class="contact-item">
                        <div class="contact-icon">📧</div>
                        <div>
                          <strong>Email</strong>
                          <p><a href="mailto:edap@maxxenergy.com">edap@maxxenergy.com</a></p>
                        </div>
                      </div>
                      
                      <div class="contact-item">
                        <div class="contact-icon">📞</div>
                        <div>
                          <strong>Phone</strong>
                          <p><a href="tel:+11234567890">+1 (123) 456-7890</a></p>
                        </div>
                      </div>
                      
                      <div class="contact-item">
                        <div class="contact-icon">📍</div>
                        <div>
                          <strong>Address</strong>
                          <p>123 Solar Way<br>New York, NY 10001</p>
                        </div>
                      </div>
                      
                      <div class="contact-item">
                        <div class="contact-icon">⏰</div>
                        <div>
                          <strong>Business Hours</strong>
                          <p>Monday - Friday<br>9:00 AM - 6:00 PM EST</p>
                        </div>
                      </div>
                    </div>
                    
                    <div class="social-links">
                      <h3>Follow Us</h3>
                      <div class="social-icons">
                        <a href="#" class="social-link">LinkedIn</a>
                        <a href="#" class="social-link">Twitter</a>
                        <a href="#" class="social-link">YouTube</a>
                      </div>
                    </div>
                  </div>
                </div>
              </section>
            </main>
            
            """ + getContactPageScript();
    }

    private String getFaqContent() {
        return """
            <section class="hero-small">
              <div class="wrap">
                <h1>Frequently Asked Questions</h1>
                <p class="lead">Find answers to common questions about MAXX Energy EDAP.</p>
              </div>
            </section>
            
            <main class="wrap">
              <section class="faq-section">
                <div class="faq-categories">
                  <button class="faq-cat-btn active" data-category="general">General</button>
                  <button class="faq-cat-btn" data-category="data">Data Access</button>
                  <button class="faq-cat-btn" data-category="technical">Technical</button>
                  <button class="faq-cat-btn" data-category="billing">Billing</button>
                </div>
                
                <div class="faq-content">
                  <div class="faq-category active" id="general">
                    <div class="faq-item">
                      <div class="faq-question">
                        <h3>What is MAXX Energy EDAP?</h3>
                        <span class="faq-toggle">+</span>
                      </div>
                      <div class="faq-answer">
                        <p>EDAP (Enterprise Data Access Portal) is a comprehensive platform that provides stakeholders with secure access to solar plant generation and revenue data. It offers both public insights and private detailed information based on user roles and permissions.</p>
                      </div>
                    </div>
                    
                    <div class="faq-item">
                      <div class="faq-question">
                        <h3>Who can use this platform?</h3>
                        <span class="faq-toggle">+</span>
                      </div>
                      <div class="faq-answer">
                        <p>EDAP is designed for MAXX Energy stakeholders including executives, directors, managers, and staff members. Public data is available to anyone, while private data requires appropriate authentication and authorization.</p>
                      </div>
                    </div>
                    
                    <div class="faq-item">
                      <div class="faq-question">
                        <h3>How often is the data updated?</h3>
                        <span class="faq-toggle">+</span>
                      </div>
                      <div class="faq-answer">
                        <p>Our system provides real-time data updates for solar generation metrics. Revenue data is typically updated daily, while performance analytics are refreshed every 15 minutes during peak operational hours.</p>
                      </div>
                    </div>
                  </div>
                  
                  <div class="faq-category" id="data">
                    <div class="faq-item">
                      <div class="faq-question">
                        <h3>What data can I access without logging in?</h3>
                        <span class="faq-toggle">+</span>
                      </div>
                      <div class="faq-answer">
                        <p>Public users can view aggregated generation data, basic performance metrics, and general system health information. This includes current power output, daily/monthly generation totals, and system availability status.</p>
                      </div>
                    </div>
                    
                    <div class="faq-item">
                      <div class="faq-question">
                        <h3>How do I get access to private data?</h3>
                        <span class="faq-toggle">+</span>
                      </div>
                      <div class="faq-answer">
                        <p>Private data access requires authentication through our secure login system. Contact your system administrator or reach out to our support team to request access credentials and appropriate role assignments.</p>
                      </div>
                    </div>
                  </div>
                  
                  <div class="faq-category" id="technical">
                    <div class="faq-item">
                      <div class="faq-question">
                        <h3>What browsers are supported?</h3>
                        <span class="faq-toggle">+</span>
                      </div>
                      <div class="faq-answer">
                        <p>EDAP supports all modern browsers including Chrome, Firefox, Safari, and Edge. We recommend using the latest version of your preferred browser for the best experience.</p>
                      </div>
                    </div>
                    
                    <div class="faq-item">
                      <div class="faq-question">
                        <h3>Is the platform mobile-friendly?</h3>
                        <span class="faq-toggle">+</span>
                      </div>
                      <div class="faq-answer">
                        <p>Yes! EDAP is fully responsive and optimized for mobile devices. You can access all features and data visualizations on smartphones and tablets.</p>
                      </div>
                    </div>
                  </div>
                  
                  <div class="faq-category" id="billing">
                    <div class="faq-item">
                      <div class="faq-question">
                        <h3>Is there a cost to use EDAP?</h3>
                        <span class="faq-toggle">+</span>
                      </div>
                      <div class="faq-answer">
                        <p>Public data access is free for all users. Premium features and private data access may require a subscription plan. Contact our sales team for detailed pricing information.</p>
                      </div>
                    </div>
                  </div>
                </div>
              </section>
            </main>
            
            """ + getFaqPageScript();
    }

    private String getHomePageScript() {
        return """
            <script>
              async function loadHomeData() {
                try {
                  const response = await fetch('/api/public/data');
                  const data = await response.json();
                  document.getElementById('homeGeneration').textContent = data.generation.toFixed(2) + ' MW';
                  document.getElementById('homeRevenue').textContent = '$' + data.revenue.toLocaleString();
                } catch (error) {
                  console.error('Error loading data:', error);
                  document.getElementById('homeGeneration').textContent = 'N/A';
                  document.getElementById('homeRevenue').textContent = 'N/A';
                }
              }
              document.addEventListener('DOMContentLoaded', loadHomeData);
            </script>
            """;
    }

    private String getDataPageScript() {
        return """
            <script>
              async function loadDashboardData() {
                try {
                  const response = await fetch('/api/public/data');
                  const data = await response.json();
                  
                  const container = document.getElementById('solarData');
                  container.innerHTML = `
                    <div class="dashboard-grid">
                      <div class="data-card featured">
                        <h3>${data.plantName}</h3>
                        <div class="data-grid">
                          <div class="stat">
                            <div class="stat-value">${data.generation.toFixed(2)} MW</div>
                            <div class="stat-label">Current Generation</div>
                          </div>
                          <div class="stat">
                            <div class="stat-value">$${data.revenue.toLocaleString()}</div>
                            <div class="stat-label">Total Revenue</div>
                          </div>
                          <div class="stat">
                            <div class="stat-value">${(data.generation / 10 * 100).toFixed(1)}%</div>
                            <div class="stat-label">Capacity Utilization</div>
                          </div>
                          <div class="stat">
                            <div class="stat-value">${(data.generation * 0.8).toFixed(1)} MW</div>
                            <div class="stat-label">Peak Today</div>
                          </div>
                        </div>
                      </div>
                      
                      <div class="data-card">
                        <h4>Performance Metrics</h4>
                        <div class="metric-list">
                          <div class="metric">
                            <span class="metric-label">System Efficiency</span>
                            <span class="metric-value good">98.5%</span>
                          </div>
                          <div class="metric">
                            <span class="metric-label">Uptime</span>
                            <span class="metric-value good">99.2%</span>
                          </div>
                          <div class="metric">
                            <span class="metric-label">Maintenance Status</span>
                            <span class="metric-value good">Optimal</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  `;
                } catch (error) {
                  console.error('Error loading solar data:', error);
                  document.getElementById('solarData').innerHTML = 
                    '<div class="data-card"><p class="muted">Unable to load solar data at this time.</p></div>';
                }
              }

              document.addEventListener('DOMContentLoaded', loadDashboardData);
              // Refresh data every 30 seconds
              setInterval(loadDashboardData, 30000);
            </script>
            """;
    }

    private String getContactPageScript() {
        return """
            <script>
              document.getElementById('contactForm').addEventListener('submit', function(e) {
                e.preventDefault();
                
                // Simulate form submission
                const submitBtn = e.target.querySelector('button[type="submit"]');
                const originalText = submitBtn.textContent;
                
                submitBtn.textContent = 'Sending...';
                submitBtn.disabled = true;
                
                setTimeout(() => {
                  alert('Thank you for your message! We will get back to you soon.');
                  e.target.reset();
                  submitBtn.textContent = originalText;
                  submitBtn.disabled = false;
                }, 2000);
              });
            </script>
            """;
    }

    private String getFaqPageScript() {
        return """
            <script>
              // FAQ Category switching
              document.querySelectorAll('.faq-cat-btn').forEach(btn => {
                btn.addEventListener('click', () => {
                  // Remove active class from all buttons and categories
                  document.querySelectorAll('.faq-cat-btn').forEach(b => b.classList.remove('active'));
                  document.querySelectorAll('.faq-category').forEach(c => c.classList.remove('active'));
                  
                  // Add active class to clicked button and corresponding category
                  btn.classList.add('active');
                  document.getElementById(btn.dataset.category).classList.add('active');
                });
              });
              
              // FAQ Item toggles
              document.querySelectorAll('.faq-item').forEach(item => {
                item.querySelector('.faq-question').addEventListener('click', () => {
                  const isOpen = item.classList.contains('open');
                  
                  // Close all other items
                  document.querySelectorAll('.faq-item').forEach(i => {
                    i.classList.remove('open');
                    i.querySelector('.faq-toggle').textContent = '+';
                  });
                  
                  // Toggle current item
                  if (!isOpen) {
                    item.classList.add('open');
                    item.querySelector('.faq-toggle').textContent = '−';
                  }
                });
              });
            </script>
            """;
    }

    /**
     * Generates the complete HTML page template with navigation and styling
     * @param title The page title
     * @param activePage The currently active page for navigation highlighting
     * @param content The main content to be inserted into the page
     * @return Complete HTML page as string
     */
    private String getPageTemplate(String title, String activePage, String content) {
        return getHtmlDoctype() +
                getHtmlHead(title) +
                getHtmlBody(activePage, content);
    }

    private String getHtmlDoctype() {
        return "<!doctype html>\n<html lang=\"en\">\n";
    }

    private String getHtmlHead(String title) {
        return """
            <head>
              <meta charset="utf-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1" />
              <title>""" + escapeHtml(title) + """
            </title>
              """ + getPageStyles() + """
            </head>
            """;
    }

    private String getHtmlBody(String activePage, String content) {
        return """
            <body>
              """ + getPageHeader(activePage) + """

              """ + content + """

              """ + getPageFooter() + """

              """ + getPageScripts() + """
            </body>
            </html>
            """;
    }

    private String getPageHeader(String activePage) {
        return """
            <header>
                <div class="wrap nav">
                  <div class="brand">
                    <div class="brand-logo">ME</div>
                    <span class="chip">MAXX Energy · EDAP</span>
                  </div>
                  
                  <div class="main-nav">
                    <nav class="nav-links" aria-label="Primary">
                      <a href="/home" """ + getActiveClass("home", activePage) + """>Home</a>
                      <a href="/about" """ + getActiveClass("about", activePage) + """>About</a>
                      <a href="/blog" """ + getActiveClass("blog", activePage) + """>Blog</a>
                      <a href="/data" """ + getActiveClass("data", activePage) + """>Data</a>
                      <a href="/user" """ + getActiveClass("user", activePage) + """>Profile</a>
                      <a href="/contact" """ + getActiveClass("contact", activePage) + """>Contact</a>
                      <a href="/faq" """ + getActiveClass("faq", activePage) + """>FAQ</a>
                    </nav>
                    
                    <div class="social-nav">
                      <a href="https://linkedin.com/company/maxxenergy" title="LinkedIn">in</a>
                      <a href="https://twitter.com/maxxenergy" title="Twitter">tw</a>
                      <a href="https://youtube.com/@maxxenergy" title="YouTube">yt</a>
                    </div>
                  </div>
                  
                  <button class="mobile-menu-btn" onclick="toggleMobileMenu()">☰</button>
                  
                  <div class="mobile-nav" id="mobileNav">
                    <nav class="nav-links" aria-label="Mobile Primary">
                      <a href="/home" """ + getActiveClass("home", activePage) + """>Home</a>
                      <a href="/about" """ + getActiveClass("about", activePage) + """>About</a>
                      <a href="/blog" """ + getActiveClass("blog", activePage) + """>Blog</a>
                      <a href="/data" """ + getActiveClass("data", activePage) + """>Data</a>
                      <a href="/user" """ + getActiveClass("user", activePage) + """>Profile</a>
                      <a href="/contact" """ + getActiveClass("contact", activePage) + """>Contact</a>
                      <a href="/faq" """ + getActiveClass("faq", activePage) + """>FAQ</a>
                    </nav>
                    <div class="social-nav">
                      <a href="https://linkedin.com/company/maxxenergy">LinkedIn</a>
                      <a href="https://twitter.com/maxxenergy">Twitter</a>
                      <a href="https://youtube.com/@maxxenergy">YouTube</a>
                    </div>
                  </div>
                </div>
              </header>
            """;
    }

    private String getPageFooter() {
        return "<footer>© 2025 MAXX Energy · Enterprise Data Access Portal</footer>";
    }

    private String getPageScripts() {
        return """
            <script>
                function toggleMobileMenu() {
                  const mobileNav = document.getElementById('mobileNav');
                  mobileNav.classList.toggle('open');
                }

                // Close mobile menu when clicking outside
                document.addEventListener('click', function(e) {
                  const mobileNav = document.getElementById('mobileNav');
                  const mobileBtn = document.querySelector('.mobile-menu-btn');
                  if (!mobileBtn.contains(e.target) && !mobileNav.contains(e.target)) {
                    mobileNav.classList.remove('open');
                  }
                });

                // Smooth scrolling for hash links
                document.querySelectorAll('a[href^="#"]').forEach(anchor => {
                  anchor.addEventListener('click', function (e) {
                    e.preventDefault();
                    const target = document.querySelector(this.getAttribute('href'));
                    if (target) {
                      target.scrollIntoView({ behavior: 'smooth' });
                    }
                  });
                });
            </script>
            """;
    }

    private String getPageStyles() {
        return """
            <style>
                :root{
                  --bg:#0b0c10; --card:#111217; --ink:#e8eaf0; --muted:#99a1b3; --line:#1f2330;
                  --brand:#e22323; --brand2:#8b1111; --accent:#2dd4bf; --success:#10b981; --warning:#f59e0b; --danger:#ef4444;
                }
                *{box-sizing:border-box}
                body{margin:0;background:linear-gradient(180deg,#0b0c10 0%, #0e1117 100%);color:var(--ink);
                     font: 15px/1.55 system-ui,-apple-system,Segoe UI,Roboto,Helvetica,Arial,sans-serif;min-height:100vh}
                a{color:inherit;text-decoration:none}
                a:hover{text-decoration:underline}
                .wrap{max-width:1200px;margin:0 auto;padding:0 20px}
                
                /* Navigation */
                header{position:sticky;top:0;z-index:40;background:#0c0f15cc;
                       backdrop-filter:saturate(160%) blur(8px);border-bottom:1px solid var(--line)}
                .nav{display:flex;align-items:center;justify-content:space-between;padding:14px 0}
                .brand{display:flex;align-items:center;gap:12px}
                .brand-logo{width:32px;height:32px;background:var(--brand);border-radius:8px;
                           display:flex;align-items:center;justify-content:center;color:white;font-weight:bold;font-size:14px}
                .chip{font-weight:700;letter-spacing:.2px}
                
                /* Main Navigation */
                .main-nav{display:flex;align-items:center;gap:24px}
                .nav-links{display:flex;align-items:center;gap:4px}
                .nav-links a{padding:8px 12px;border-radius:10px;text-decoration:none;color:var(--muted);
                            transition:all 0.2s ease;font-weight:500}
                .nav-links a:hover{background:#1a1f2b;color:var(--ink);text-decoration:none}
                .nav-links a.active{background:var(--brand);color:white}
                
                /* Social Links */
                .social-nav{display:flex;align-items:center;gap:8px;margin-left:16px;
                           padding-left:16px;border-left:1px solid var(--line)}
                .social-nav a{padding:6px 8px;border-radius:6px;color:var(--muted);font-size:14px;
                             transition:all 0.2s ease}
                .social-nav a:hover{background:#1a1f2b;color:var(--accent);text-decoration:none}
                
                /* Mobile Navigation */
                .mobile-menu-btn{display:none;background:none;border:none;color:var(--ink);
                                font-size:18px;padding:8px;cursor:pointer}
                .mobile-nav{display:none;position:absolute;top:100%;left:0;right:0;
                           background:var(--card);border-bottom:1px solid var(--line);padding:20px}
                .mobile-nav .nav-links{flex-direction:column;align-items:stretch;gap:8px}
                .mobile-nav .nav-links a{padding:12px;text-align:center}
                .mobile-nav .social-nav{justify-content:center;margin:16px 0 0 0;
                                       padding:16px 0 0 0;border-left:none;border-top:1px solid var(--line)}
                
                @media (max-width:768px){
                  .nav-links, .social-nav{display:none}
                  .mobile-menu-btn{display:block}
                  .mobile-nav.open{display:block}
                }
                
                /* Hero Sections */
                .hero{border-bottom:1px solid var(--line);padding:56px 0}
                .hero-small{border-bottom:1px solid var(--line);padding:40px 0}
                .hero-grid{display:grid;grid-template-columns:1.2fr .8fr;gap:28px;align-items:center}
                @media (max-width:920px){.hero-grid{grid-template-columns:1fr}}
                h1{font-size:44px;line-height:1.1;margin:0}
                .hero-small h1{font-size:36px}
                .lead{margin-top:12px;color:var(--muted);font-size:18px}
                .cta{margin-top:18px;display:flex;gap:12px;flex-wrap:wrap}
                .btn{padding:12px 16px;border-radius:14px;border:1px solid var(--line);text-decoration:none;
                     cursor:pointer;background:var(--card);color:var(--ink);transition:all 0.2s ease}
                .btn:hover{background:var(--line);text-decoration:none}
                .btn.primary{background:linear-gradient(180deg,var(--brand),var(--brand2));border:0;color:white}
                .btn.primary:hover{background:linear-gradient(180deg,var(--brand2),var(--brand))}
                
                /* Layout */
                .panel{background:var(--card);border:1px solid var(--line);border-radius:18px;padding:18px}
                section{padding:42px 0}
                h2{font-size:26px;margin:0 0 10px}
                h3{font-size:20px;margin:0 0 8px}
                .grid2{display:grid;grid-template-columns:1fr 1fr;gap:24px}
                @media (max-width:920px){.grid2{grid-template-columns:1fr}}
                .cards{display:grid;grid-template-columns:repeat(auto-fit,minmax(280px,1fr));gap:16px}
                .card{background:var(--card);border:1px solid var(--line);border-radius:16px;padding:16px}
                .muted{color:var(--muted)}
                
                /* Home Page Styles */
                .home-stats{display:grid;grid-template-columns:1fr 1fr;gap:16px;width:100%}
                .quick-stat{text-align:center;padding:16px;background:#0a0e14;border-radius:12px}
                .features{padding:24px 0}
                
                /* Data Dashboard Styles */
                .dashboard-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:24px}
                .refresh-indicator{display:flex;align-items:center;gap:8px;color:var(--success)}
                .status-dot{width:8px;height:8px;background:var(--success);border-radius:50%;
                           animation:pulse 2s infinite}
                @keyframes pulse{0%,100%{opacity:1}50%{opacity:0.5}}
                
                .dashboard-grid{display:grid;grid-template-columns:2fr 1fr;gap:24px}
                @media (max-width:920px){.dashboard-grid{grid-template-columns:1fr}}
                .data-card{background:var(--card);border:1px solid var(--line);border-radius:16px;padding:20px}
                .data-card.featured{border:2px solid var(--accent)}
                .data-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(150px,1fr));gap:16px;margin-top:16px}
                .stat{text-align:center;padding:16px;background:#0a0e14;border-radius:12px}
                .stat-value{font-size:24px;font-weight:bold;color:var(--accent)}
                .stat-label{color:var(--muted);margin-top:4px;font-size:14px}
                
                .data-controls{display:flex;gap:20px;margin-top:24px;flex-wrap:wrap}
                .control-group{display:flex;flex-direction:column;gap:8px}
                .control-group label{font-weight:600;font-size:14px}
                .control-select{padding:8px 12px;border:1px solid var(--line);background:var(--card);
                               color:var(--ink);border-radius:8px}
                
                .metric-list{margin-top:16px}
                .metric{display:flex;justify-content:space-between;padding:8px 0;
                       border-bottom:1px solid var(--line)}
                .metric:last-child{border-bottom:none}
                .metric-value{font-weight:600}
                .metric-value.good{color:var(--success)}
                .metric-value.warning{color:var(--warning)}
                .metric-value.danger{color:var(--danger)}
                
                /* Blog Styles */
                .blog-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(320px,1fr));gap:20px}
                .blog-card{background:var(--card);border:1px solid var(--line);border-radius:16px;
                          padding:20px;transition:transform 0.2s ease}
                .blog-card:hover{transform:translateY(-2px)}
                .blog-meta{display:flex;gap:12px;margin-bottom:12px;font-size:14px}
                .date{color:var(--muted)}
                .category{color:var(--accent);font-weight:600}
                .read-more{color:var(--accent);font-weight:600;margin-top:12px;display:inline-block}
                
                /* User Profile Styles */
                .profile-section{display:flex;align-items:center;gap:16px;margin-bottom:20px}
                .avatar-circle{width:60px;height:60px;border-radius:50%;background:var(--brand);
                              display:flex;align-items:center;justify-content:center;color:white;
                              font-weight:bold;font-size:20px}
                .profile-info h3{margin:0}
                .role-badge{display:inline-block;padding:4px 8px;background:var(--accent);
                           color:var(--bg);border-radius:6px;font-size:12px;font-weight:600;margin-top:8px}
                
                .info-grid{display:grid;grid-template-columns:repeat(2,1fr);gap:12px;margin-top:16px}
                .info-item{display:flex;justify-content:space-between;padding:8px 0;
                          border-bottom:1px solid var(--line)}
                .info-item:last-child{border-bottom:none}
                .info-item label{font-weight:600}
                
                .permissions-list{margin:16px 0}
                .permission-item{display:flex;justify-content:space-between;align-items:center;
                                padding:12px 0;border-bottom:1px solid var(--line)}
                .permission-item:last-child{border-bottom:none}
                .permission-status.enabled{color:var(--success)}
                .permission-status.disabled{color:var(--danger)}
                .actions{margin-top:20px;display:flex;gap:12px}
                
                /* Contact Form Styles */
                .contact-form{display:flex;flex-direction:column;gap:16px}
                .form-group{display:flex;flex-direction:column;gap:6px}
                .form-group label{font-weight:600}
                .form-input{padding:12px;border:1px solid var(--line);background:var(--bg);
                           color:var(--ink);border-radius:8px;font-size:15px}
                .form-input:focus{outline:none;border-color:var(--accent)}
                textarea.form-input{resize:vertical;min-height:120px}
                
                .checkbox-label{display:flex;align-items:center;gap:8px;cursor:pointer}
                .checkbox-label input[type="checkbox"]{margin:0}
                
                .contact-info{margin-top:20px}
                .contact-item{display:flex;align-items:flex-start;gap:12px;margin-bottom:20px}
                .contact-icon{font-size:20px;margin-top:2px}
                .contact-item strong{display:block;margin-bottom:4px}
                .contact-item p{margin:0;color:var(--muted)}
                .contact-item a{color:var(--accent)}
                
                .social-links{margin-top:24px;padding-top:20px;border-top:1px solid var(--line)}
                .social-icons{display:flex;gap:12px;margin-top:12px}
                .social-link{padding:8px 16px;background:var(--card);border:1px solid var(--line);
                            border-radius:8px;color:var(--accent);font-weight:600;text-decoration:none}
                .social-link:hover{background:var(--line)}
                
                /* FAQ Styles */
                .faq-categories{display:flex;gap:12px;margin-bottom:32px;flex-wrap:wrap}
                .faq-cat-btn{padding:10px 20px;border:1px solid var(--line);background:var(--card);
                            color:var(--muted);border-radius:10px;cursor:pointer;font-weight:600}
                .faq-cat-btn:hover{background:var(--line);color:var(--ink)}
                .faq-cat-btn.active{background:var(--brand);color:white;border-color:var(--brand)}
                
                .faq-category{display:none}
                .faq-category.active{display:block}
                .faq-item{border:1px solid var(--line);border-radius:12px;margin-bottom:12px;
                         background:var(--card);overflow:hidden}
                .faq-question{display:flex;justify-content:space-between;align-items:center;
                             padding:16px 20px;cursor:pointer;background:var(--card)}
                .faq-question:hover{background:var(--line)}
                .faq-question h3{margin:0;font-size:16px}
                .faq-toggle{font-size:20px;font-weight:bold;color:var(--accent)}
                .faq-answer{padding:0 20px;max-height:0;overflow:hidden;transition:all 0.3s ease}
                .faq-item.open .faq-answer{padding:16px 20px;max-height:300px}
                
                .loading{color:var(--muted);text-align:center;padding:20px}
                
                footer{border-top:1px solid var(--line);color:var(--muted);text-align:center;padding:26px 0;margin-top:40px}
                
                /* Utility classes */
                .timeline{border-left:2px solid var(--line);padding-left:16px;margin-top:8px}
                .tl{position:relative;margin:14px 0}
                .tl::before{content:"";position:absolute;left:-11px;top:6px;width:10px;height:10px;background:var(--accent);border-radius:50%}
                dl{display:grid;grid-template-columns:140px 1fr;gap:6px 18px}
                ul{margin:8px 0 0 20px}
                
                @media (max-width:768px){
                  h1{font-size:32px}
                  .hero-small h1{font-size:28px}
                  .cta{flex-direction:column}
                  .btn{text-align:center}
                  .dashboard-grid, .grid2, .home-stats{grid-template-columns:1fr}
                  .data-grid{grid-template-columns:repeat(2,1fr)}
                  .data-controls{flex-direction:column}
                  .blog-grid{grid-template-columns:1fr}
                  .info-grid{grid-template-columns:1fr}
                  .faq-categories{justify-content:center}
                  .actions{flex-direction:column}
                }
            </style>
            """;
    }

    private String getActiveClass(String page, String activePage) {
        return page.equals(activePage) ? "class=\"active\"" : "";
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}