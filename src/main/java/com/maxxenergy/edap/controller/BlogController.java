package com.maxxenergy.edap.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class BlogController {

    private static final Logger logger = LoggerFactory.getLogger(BlogController.class);

    @GetMapping(value = "/blog", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String blogPage() {
        logger.debug("Serving blog page");
        return PageTemplateService.getPageTemplate("Blog · MAXX Energy EDAP", "blog", getBlogContent());
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
}