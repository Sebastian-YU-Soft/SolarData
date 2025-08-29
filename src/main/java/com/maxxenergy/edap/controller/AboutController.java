package com.maxxenergy.edap.controller;

import org.springframework.stereotype.Controller;
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
                  </div>
                </div>
              </section>
            </main>
            
            """ + getFaqPageScript();
    }

    // Missing methods implementation
    private String getPageTemplate(String title, String activeSection, String content) {
        return """
                <!doctype html>
                <html lang="en">
                <head>
                    <meta charset="utf-8"/>
                    <meta name="viewport" content="width=device-width, initial-scale=1"/>
                    <title>%snull%snull%snull%snull%snull%snull%snull%snull%s</body>
                </html>
                """.formatted(title, "home".equals(activeSection) ? "class=\"active\"" : "", "about".equals(activeSection) ? "class=\"active\"" : "", "blog".equals(activeSection) ? "class=\"active\"" : "", "data".equals(activeSection) ? "class=\"active\"" : "", "user".equals(activeSection) ? "class=\"active\"" : "", "contact".equals(activeSection) ? "class=\"active\"" : "", "faq".equals(activeSection) ? "class=\"active\"" : "", content);
    }

    private String getHomePageScript() {
        return """
        <script>
            // Fetch and display home page stats
            async function loadHomeStats() {
                try {
                    const response = await fetch('/api/public/data');
                    const data = await response.json();
                    
                    if (data) {
                        document.getElementById('homeGeneration').textContent = 
                            data.generation ? data.generation.toFixed(1) + ' MW' : 'N/A';
                        document.getElementById('homeRevenue').textContent = 
                            data.revenue ? '
                 + (data.revenue/1000).toFixed(0) + 'K' : 'N/A';
                    }
                } catch (error) {
                    console.error('Error loading stats:', error);
                    document.getElementById('homeGeneration').textContent = '8.5 MW';
                    document.getElementById('homeRevenue').textContent = '$125K';
                }
            }
            
            // Load stats when page loads
            document.addEventListener('DOMContentLoaded', loadHomeStats);
            
            // Refresh stats every 30 seconds
            setInterval(loadHomeStats, 30000);
        </script>
        """;
    }

    private String getDataPageScript() {
        return """
        <script>
            // Mock data visualization
            async function loadSolarData() {
                const container = document.getElementById('solarData');
                
                try {
                    const response = await fetch('/api/public/data');
                    const data = await response.json();
                    
                    container.innerHTML = `
                        <div class="panel">
                            <h3>Current Plant Performance</h3>
                            <div class="grid2">
                                <div class="card">
                                    <h4>${data.plantName || 'Demo Solar Plant'}</h4>
                                    <div class="stat-value">${(data.generation || 8.5).toFixed(1)} MW</div>
                                    <div class="stat-label">Current Generation</div>
                                </div>
                                <div class="card">
                                    <h4>Revenue Today</h4>
                                    <div class="stat-value">${((data.revenue || 125000)/1000).toFixed(0)}K</div>
                                    <div class="stat-label">Total Revenue</div>
                                </div>
                            </div>
                            <div style="margin-top: 20px; padding: 40px; background: var(--line); border-radius: 12px; text-align: center;">
                                <div style="color: var(--muted);">📊 Interactive Charts Coming Soon</div>
                                <p style="color: var(--muted); margin: 8px 0 0;">Real-time generation graphs and analytics dashboard</p>
                            </div>
                        </div>
                    `;
                } catch (error) {
                    console.error('Error loading solar data:', error);
                    container.innerHTML = `
                        <div class="panel">
                            <h3>Demo Data</h3>
                            <div class="card">
                                <p class="muted">Unable to load real-time data. Showing demo values.</p>
                                <div class="stat-value">8.5 MW</div>
                                <div class="stat-label">Demo Generation</div>
                            </div>
                        </div>
                    `;
                }
            }
            
            document.addEventListener('DOMContentLoaded', loadSolarData);
        </script>
        """;
    }

    private String getContactPageScript() {
        return """
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                const form = document.getElementById('contactForm');
                if (form) {
                    form.addEventListener('submit', function(e) {
                        e.preventDefault();
                        alert('Thank you for your message! We will get back to you within one business day.');
                        form.reset();
                    });
                }
            });
        </script>
        """;
    }

    private String getFaqPageScript() {
        return """
        <script>
            document.addEventListener('DOMContentLoaded', function() {
                // Handle FAQ category switching
                const categoryButtons = document.querySelectorAll('.faq-cat-btn');
                const categories = document.querySelectorAll('.faq-category');
                
                categoryButtons.forEach(button => {
                    button.addEventListener('click', function() {
                        const category = this.dataset.category;
                        
                        // Update active button
                        categoryButtons.forEach(btn => btn.classList.remove('active'));
                        this.classList.add('active');
                        
                        // Show corresponding category
                        categories.forEach(cat => {
                            cat.classList.remove('active');
                            if (cat.id === category) {
                                cat.classList.add('active');
                            }
                        });
                    });
                });
                
                // Handle FAQ item toggling
                document.addEventListener('click', function(e) {
                    if (e.target.closest('.faq-question')) {
                        const faqItem = e.target.closest('.faq-item');
                        faqItem.classList.toggle('open');
                    }
                });
            });
        </script>
        """;
    }
    // Navigation to the standalone data input page
    @GetMapping(value = "/data-input-info", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String dataInputInfoPage() {
        return getPageTemplate("Data Input · MAXX Energy EDAP", "data-input", getDataInputInfoContent());
    }

    // Method to generate content for data input information page
    private String getDataInputInfoContent() {
        return """
        <section class="hero">
            <div class="wrap hero-grid">
                <div>
                    <h1>Solar Data Input & Analytics</h1>
                    <p class="lead">
                        Input your solar plant data and visualize performance metrics with interactive charts. 
                        Track generation, efficiency, and revenue in real-time.
                    </p>
                    <div class="cta">
                        <a class="btn primary" href="/data-input">Launch Data Input Tool</a>
                        <a class="btn" href="/data">View Public Data</a>
                        <a class="btn" href="/register">Create Account</a>
                    </div>
                </div>
                <div class="logo-hero panel">
                    <div class="home-stats">
                        <div class="quick-stat">
                            <div class="stat-value">📊</div>
                            <div class="stat-label">Interactive Charts</div>
                        </div>
                        <div class="quick-stat">
                            <div class="stat-value">⚡</div>
                            <div class="stat-label">Real-time Input</div>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        
        <main class="wrap">
            <section class="features">
                <h2>Data Input Features</h2>
                <div class="grid2" style="margin-top: 20px;">
                    <div class="panel">
                        <h3>Input Parameters</h3>
                        <ul>
                            <li><strong>Plant Information:</strong> Name and capacity details</li>
                            <li><strong>Generation Data:</strong> Current power output in MW</li>
                            <li><strong>Efficiency Metrics:</strong> Performance percentage tracking</li>
                            <li><strong>Environmental Data:</strong> Temperature and solar irradiance</li>
                            <li><strong>Financial Data:</strong> Revenue generation tracking</li>
                            <li><strong>Timestamp:</strong> Automatic data entry timestamping</li>
                        </ul>
                    </div>
                    
                    <div class="panel">
                        <h3>Visualization Features</h3>
                        <ul>
                            <li><strong>Real-time Charts:</strong> Interactive Chart.js visualizations</li>
                            <li><strong>Historical Trends:</strong> View your last 10 data entries</li>
                            <li><strong>Multi-axis Graphs:</strong> Generation vs efficiency analysis</li>
                            <li><strong>Responsive Design:</strong> Works on mobile and desktop</li>
                            <li><strong>Data Validation:</strong> Input validation and error handling</li>
                            <li><strong>Auto-refresh:</strong> Charts update automatically after submission</li>
                        </ul>
                    </div>
                </div>
            </section>
            
            <section class="features" style="margin-top: 30px;">
                <div class="cards">
                    <div class="card">
                        <h3>📈 Performance Tracking</h3>
                        <p class="muted">Monitor solar plant performance over time with detailed metrics including generation capacity, efficiency rates, and environmental factors affecting output.</p>
                    </div>
                    <div class="card">
                        <h3>🔒 Secure Storage</h3>
                        <p class="muted">All data entries are securely stored in MongoDB with user-specific access controls. Your data remains private and accessible only to you.</p>
                    </div>
                    <div class="card">
                        <h3>📊 Interactive Analysis</h3>
                        <p class="muted">Visualize trends and patterns in your solar data with dynamic charts that help identify optimization opportunities and performance insights.</p>
                    </div>
                </div>
            </section>
            
            <section class="features" style="margin-top: 30px;">
                <div class="panel">
                    <h2>Getting Started</h2>
                    <div class="timeline">
                        <div class="tl"><strong>Step 1</strong> — Create an account or log in to access the data input feature</div>
                        <div class="tl"><strong>Step 2</strong> — Navigate to the data input tool and enter your solar plant information</div>
                        <div class="tl"><strong>Step 3</strong> — Submit data entries and watch your performance charts update in real-time</div>
                        <div class="tl"><strong>Step 4</strong> — Analyze trends and optimize your solar plant operations based on the insights</div>
                    </div>
                </div>
            </section>
        </main>
        """;
    }

    // Update your existing getPageTemplate method to fix the formatting issue
    private String getPageTemplate(String title, String activeSection, String content) {
        return """
            <!doctype html>
            <html lang="en">
            <head>
                <meta charset="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>%s</title>
                <style>
                    :root {
                        --bg: #0b0c10;
                        --card: #111217;
                        --ink: #e8eaf0;
                        --muted: #99a1b3;
                        --line: #1f2330;
                        --brand: #e22323;
                        --brand2: #8b1111;
                    }
                    
                    body {
                        margin: 0;
                        background: linear-gradient(180deg, #0b0c10 0%, #0e1117 100%);
                        color: var(--ink);
                        font: 15px/1.55 system-ui, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif;
                    }
                    
                    .wrap {
                        max-width: 1200px;
                        margin: 0 auto;
                        padding: 0 20px;
                    }
                    
                    .hero {
                        padding: 60px 0;
                        background: var(--card);
                        border-bottom: 1px solid var(--line);
                    }
                    
                    .hero-grid {
                        display: grid;
                        grid-template-columns: 2fr 1fr;
                        gap: 40px;
                        align-items: center;
                    }
                    
                    .hero h1 {
                        margin: 0 0 20px;
                        font-size: 2.5em;
                        font-weight: 700;
                    }
                    
                    .lead {
                        color: var(--muted);
                        font-size: 1.1em;
                        line-height: 1.6;
                        margin-bottom: 30px;
                    }
                    
                    .cta {
                        display: flex;
                        gap: 15px;
                        flex-wrap: wrap;
                    }
                    
                    .btn {
                        padding: 12px 20px;
                        border-radius: 12px;
                        border: 1px solid var(--line);
                        text-decoration: none;
                        color: var(--ink);
                        cursor: pointer;
                        background: var(--card);
                        transition: all 0.2s;
                    }
                    
                    .btn.primary {
                        background: linear-gradient(180deg, var(--brand), var(--brand2));
                        border: 0;
                        color: white;
                    }
                    
                    .btn:hover {
                        transform: translateY(-1px);
                    }
                    
                    .panel {
                        background: var(--card);
                        border: 1px solid var(--line);
                        border-radius: 18px;
                        padding: 25px;
                    }
                    
                    .logo-hero {
                        text-align: center;
                    }
                    
                    .home-stats {
                        display: flex;
                        gap: 20px;
                        justify-content: center;
                    }
                    
                    .quick-stat {
                        text-align: center;
                    }
                    
                    .stat-value {
                        font-size: 2em;
                        font-weight: bold;
                        margin-bottom: 5px;
                    }
                    
                    .stat-label {
                        color: var(--muted);
                        font-size: 0.9em;
                    }
                    
                    .features {
                        margin: 40px 0;
                    }
                    
                    .features h2 {
                        margin-bottom: 20px;
                    }
                    
                    .grid2 {
                        display: grid;
                        grid-template-columns: 1fr 1fr;
                        gap: 30px;
                    }
                    
                    .cards {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
                        gap: 25px;
                        margin-top: 20px;
                    }
                    
                    .card {
                        background: var(--card);
                        border: 1px solid var(--line);
                        border-radius: 18px;
                        padding: 25px;
                    }
                    
                    .card h3 {
                        margin: 0 0 15px;
                        font-size: 1.2em;
                    }
                    
                    .muted {
                        color: var(--muted);
                    }
                    
                    .timeline {
                        margin-top: 20px;
                    }
                    
                    .tl {
                        padding: 15px 0;
                        border-left: 2px solid var(--line);
                        padding-left: 20px;
                        margin-left: 10px;
                        position: relative;
                    }
                    
                    .tl:before {
                        content: '';
                        position: absolute;
                        left: -6px;
                        top: 20px;
                        width: 10px;
                        height: 10px;
                        background: var(--brand);
                        border-radius: 50%;
                    }
                    
                    ul {
                        padding-left: 20px;
                    }
                    
                    li {
                        margin: 8px 0;
                        line-height: 1.6;
                    }
                    
                    @media (max-width: 768px) {
                        .hero-grid {
                            grid-template-columns: 1fr;
                            text-align: center;
                        }
                        
                        .grid2 {
                            grid-template-columns: 1fr;
                        }
                        
                        .cards {
                            grid-template-columns: 1fr;
                        }
                        
                        .cta {
                            justify-content: center;
                        }
                    }
                </style>
            </head>
            <body>
                <nav style="background: var(--card); border-bottom: 1px solid var(--line); padding: 15px 0;">
                    <div class="wrap" style="display: flex; gap: 20px; align-items: center;">
                        <a href="/home" style="color: var(--ink); text-decoration: none; font-weight: bold;">MAXX Energy EDAP</a>
                        <div style="flex: 1;"></div>
                        <a href="/home" class="%s" style="color: var(--muted); text-decoration: none;">Home</a>
                        <a href="/about" class="%s" style="color: var(--muted); text-decoration: none;">About</a>
                        <a href="/data" class="%s" style="color: var(--muted); text-decoration: none;">Data</a>
                        <a href="/data-input" class="%s" style="color: var(--muted); text-decoration: none;">Data Input</a>
                        <a href="/contact" class="%s" style="color: var(--muted); text-decoration: none;">Contact</a>
                        <a href="/register" style="color: var(--brand); text-decoration: none;">Register</a>
                    </div>
                </nav>
                %s
            </body>
            </html>
            """.formatted(title,
                "home".equals(activeSection) ? "active" : "",
                "about".equals(activeSection) ? "active" : "",
                "data".equals(activeSection) ? "active" : "",
                "data-input".equals(activeSection) ? "active" : "",
                "contact".equals(activeSection) ? "active" : "",
                content);
    }

}
