package com.maxxenergy.edap.controller;

import com.maxxenergy.edap.service.PageTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class ContactController {

    private static final Logger logger = LoggerFactory.getLogger(ContactController.class);

    @GetMapping(value = "/contact", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String contactPage() {
        logger.debug("Serving contact page");
        return PageTemplateService.getPageTemplate("Contact Us · MAXX Energy EDAP", "contact", getContactContent());
    }

    @GetMapping(value = "/faq", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String faqPage() {
        logger.debug("Serving FAQ page");
        return PageTemplateService.getPageTemplate("FAQ · MAXX Energy EDAP", "faq", getFaqContent());
    }

    private String getContactContent() {
        return """
            <section class="hero-small">
              <div class="wrap">
                <h1>Contact Us</h1>
                <p class="lead">Get in touch with the MAXX Energy EDAP team.</p>
              </div>
            </section>
            <main class="wrap">
              <div class="panel">
                <h2>Contact Information</h2>
                <p>Email: support@maxxenergy.com</p>
                <p>Phone: (555) 123-4567</p>
                <p>Address: 123 Energy Lane, Solar City, SC 12345</p>
              </div>
            </main>
            """;
    }

    private String getFaqContent() {
        return """
            <section class="hero-small">
              <div class="wrap">
                <h1>Frequently Asked Questions</h1>
                <p class="lead">Common questions about the EDAP platform.</p>
              </div>
            </section>
            <main class="wrap">
              <div class="panel">
                <h3>How do I create an account?</h3>
                <p>Click the "Create Account" button and fill out the registration form.</p>
                <h3>How do I view solar data?</h3>
                <p>Visit the Data Dashboard to view real-time solar generation metrics.</p>
              </div>
            </main>
            """;
    }
}