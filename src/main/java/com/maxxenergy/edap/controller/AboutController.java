package com.maxxenergy.edap.controller;

import com.maxxenergy.edap.service.PageTemplateService;
import com.maxxenergy.edap.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;

@Controller
public class AboutController {

    private static final Logger logger = LoggerFactory.getLogger(AboutController.class);
    private static final String SESSION_COOKIE = "edap_session";

    @Autowired
    private SessionService sessionService;

    @GetMapping(value = "/about", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String aboutPage(HttpServletRequest request) {
        logger.debug("Serving about page");

        String authenticatedEmail = getAuthenticatedEmail(request);
        String authStatus = generateAuthStatus(authenticatedEmail);

        return PageTemplateService.getPageTemplate("About · MAXX Energy EDAP", "about", getAboutContent(authStatus));
    }

    private String getAuthenticatedEmail(HttpServletRequest request) {
        String sessionToken = getSessionToken(request);
        return sessionToken != null ? sessionService.getEmailFromSession(sessionToken) : null;
    }

    private String getSessionToken(HttpServletRequest request) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (SESSION_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String generateAuthStatus(String email) {
        if (email == null) {
            return """
                <a class="btn" href="/auth/login">Log in</a> 
                <a class="btn primary" href="/register">Register</a>
                """;
        } else {
            return """
                <span class="muted">Signed in as """ + escapeHtml(email) + """</span> 
                <a class="btn" href="/auth/members">Members</a>
                <a class="btn" href="/profile">Profile</a>
                <a class="btn" href="/auth/logout">Log out</a>
                """;
        }
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String getAboutContent(String authStatus) {
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
                        """ + authStatus + """
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

                  <section id="contact">
                    <div class="panel">
                      <h2>Contact Us</h2>
                      <p class="muted">We usually respond within one business day.</p>
                      <dl style="margin-top:10px">
                        <dt>Email</dt><dd><a href="mailto:edap@maxxenergy.com">edap@maxxenergy.com</a></dd>
                        <dt>Phone</dt><dd><a href="tel:+11234567890">+1 (123) 456-7890</a></dd>
                        <dt>Address</dt><dd>123 Solar Way, New York, NY 10001</dd>
                        <dt>Social</dt><dd><a href="#">LinkedIn</a> · <a href="#">YouTube</a></dd>
                      </dl>
                    </div>
                  </section>
                </main>
                """;
    }
}