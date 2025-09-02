package com.maxxenergy.edap.controller;

import com.maxxenergy.edap.service.PageTemplateService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class DataController {

    private static final Logger logger = LoggerFactory.getLogger(DataController.class);

    @GetMapping(value = "/data", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String dataPage() {
        logger.debug("Serving data dashboard page");
        return PageTemplateService.getPageTemplate("Data Dashboard · MAXX Energy EDAP", "data", getDataContent());
    }

    @GetMapping(value = "/data-input-info", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String dataInputInfoPage() {
        logger.debug("Serving data input info page");
        return PageTemplateService.getPageTemplate("Data Input · MAXX Energy EDAP", "data-input", getDataInputInfoContent());
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

    private String getDataInputInfoContent() {
        return """
                <section class="hero-small">
                  <div class="wrap">
                    <h1>Data Input Information</h1>
                    <p class="lead">Learn how to input solar data into the EDAP system.</p>
                  </div>
                </section>
                <main class="wrap">
                  <div class="panel">
                    <h2>Data Input Guidelines</h2>
                    <p>Use the data input form to submit solar plant performance data.</p>
                    <p>All required fields must be completed for successful submission.</p>
                    <a href="/data-input" class="btn primary">Go to Data Input</a>
                  </div>
                </main>
                """;
    }

    private String getDataPageScript() {
        return """
                <script>
                    // Load solar data
                    fetch('/api/public/data')
                        .then(response => response.json())
                        .then(data => {
                            const container = document.getElementById('solarData');
                            if (container) {
                                container.innerHTML = `
                                    <div class="panel">
                                        <h3>${data.plantName}</h3>
                                        <p>Current Generation: ${data.generation} MW</p>
                                        <p>Total Revenue: ${data.revenue.toLocaleString()}</p>
                                    </div>
                                `;
                                container.classList.remove('loading');
                            }
                        })
                        .catch(error => {
                            const container = document.getElementById('solarData');
                            if (container) {
                                container.innerHTML = '<div class="panel"><p>Error loading data</p></div>';
                                container.classList.remove('loading');
                            }
                        });
                </script>
                """;
    }
}