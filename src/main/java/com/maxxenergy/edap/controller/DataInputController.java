package com.maxxenergy.edap.controller;

import com.maxxenergy.edap.model.SolarDataEntry;
import com.maxxenergy.edap.service.SolarDataEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller for solar data input functionality.
 * Provides both HTML interface and API endpoints for data entry.
 */
@Controller
@RequestMapping("/data-input")
public class DataInputController {

    private static final Logger logger = LoggerFactory.getLogger(DataInputController.class);

    @Autowired
    private SolarDataEntryService dataEntryService;

    /**
     * Show data input page (HTML interface)
     */
    @GetMapping
    public ResponseEntity<String> showDataInputPage() {
        logger.debug("Serving data input page");
        return ResponseEntity.ok()
                .header("Content-Type", "text/html")
                .body(generateDataInputPage());
    }

    /**
     * Submit new data entry (API endpoint)
     */
    @PostMapping("/api/submit")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> submitData(@RequestBody SolarDataEntry entry) {
        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Submitting data entry for plant: {}", entry.getPlantName());

            // In a real application, you'd get the user ID from the session/JWT token
            // For demo purposes, using a placeholder user ID
            if (entry.getUserId() == null || entry.getUserId().trim().isEmpty()) {
                entry.setUserId("demo-user-123");
            }

            // Validate and save the entry
            SolarDataEntry saved = dataEntryService.saveDataEntry(entry);

            response.put("success", true);
            response.put("message", "Data saved successfully");
            response.put("id", saved.getId());
            response.put("timestamp", saved.getTimestamp());
            response.put("summary", saved.getSummary());

            logger.info("Data entry saved successfully: {}", saved.getId());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Data validation failed: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("type", "validation_error");
            return ResponseEntity.badRequest().body(response);

        } catch (Exception e) {
            logger.error("Error saving data entry: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Internal server error occurred while saving data");
            response.put("type", "server_error");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * Get user's data entries for visualization (API endpoint)
     */
    @GetMapping("/api/user-data")
    @ResponseBody
    public ResponseEntity<List<SolarDataEntry>> getUserData(
            @RequestParam(required = false, defaultValue = "demo-user-123") String userId) {
        try {
            logger.debug("Fetching user data for: {}", userId);
            List<SolarDataEntry> entries = dataEntryService.getRecentUserEntries(userId, 10);
            logger.debug("Found {} entries for user", entries.size());
            return ResponseEntity.ok(entries);

        } catch (Exception e) {
            logger.error("Error fetching user data: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    /**
     * Get user's data statistics (API endpoint)
     */
    @GetMapping("/api/user-stats")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getUserStatistics(
            @RequestParam(required = false, defaultValue = "demo-user-123") String userId) {
        try {
            logger.debug("Fetching user statistics for: {}", userId);
            Map<String, Object> stats = dataEntryService.getUserStatistics(userId);
            return ResponseEntity.ok(stats);

        } catch (Exception e) {
            logger.error("Error fetching user statistics: {}", e.getMessage(), e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Unable to fetch statistics");
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Delete a data entry (API endpoint)
     */
    @DeleteMapping("/api/entry/{entryId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> deleteEntry(
            @PathVariable String entryId,
            @RequestParam(required = false, defaultValue = "demo-user-123") String userId) {

        Map<String, Object> response = new HashMap<>();

        try {
            logger.info("Deleting entry {} for user {}", entryId, userId);
            dataEntryService.deleteDataEntry(entryId, userId);

            response.put("success", true);
            response.put("message", "Entry deleted successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error deleting entry: {}", e.getMessage());
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Generate the HTML page for data input interface
     */
    private String generateDataInputPage() {
        return """
        <!doctype html>
        <html lang="en">
        <head>
            <meta charset="utf-8"/>
            <meta name="viewport" content="width=device-width, initial-scale=1"/>
            <title>Data Input · MAXX Energy EDAP</title>
            <script src="https://cdnjs.cloudflare.com/ajax/libs/Chart.js/3.9.1/chart.min.js"></script>
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
                    padding: 20px;
                }

                .hero-small {
                    background: var(--card);
                    border-bottom: 1px solid var(--line);
                    padding: 40px 0;
                    margin-bottom: 30px;
                }

                .hero-small h1 {
                    margin: 0 0 10px;
                    font-size: 2.2em;
                }

                .lead {
                    color: var(--muted);
                    font-size: 1.1em;
                    margin: 0;
                }

                .grid2 {
                    display: grid;
                    grid-template-columns: 1fr 1fr;
                    gap: 30px;
                    margin-top: 30px;
                }

                .panel {
                    background: var(--card);
                    border: 1px solid var(--line);
                    border-radius: 18px;
                    padding: 25px;
                }

                .form-group {
                    margin-bottom: 20px;
                }

                .form-group label {
                    display: block;
                    margin-bottom: 8px;
                    font-weight: 600;
                    color: var(--ink);
                }

                .form-input {
                    width: 100%;
                    padding: 12px 15px;
                    border: 1px solid var(--line);
                    border-radius: 12px;
                    background: #0d1017;
                    color: var(--ink);
                    font-size: 15px;
                    box-sizing: border-box;
                }

                .form-input:focus {
                    outline: none;
                    border-color: var(--brand);
                }

                .btn {
                    padding: 12px 20px;
                    border-radius: 12px;
                    border: 1px solid var(--line);
                    background: var(--card);
                    color: var(--ink);
                    cursor: pointer;
                    font-size: 15px;
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

                .btn:disabled {
                    opacity: 0.5;
                    cursor: not-allowed;
                    transform: none;
                }

                .chart-container {
                    position: relative;
                    height: 400px;
                    margin-top: 20px;
                }

                .loading {
                    text-align: center;
                    color: var(--muted);
                    padding: 20px;
                }

                .alert {
                    padding: 15px;
                    border-radius: 12px;
                    margin-bottom: 20px;
                }

                .alert.success {
                    background: #1a3d2e;
                    border: 1px solid #2d6a4f;
                    color: #a7f3d0;
                }

                .alert.error {
                    background: #2a0f12;
                    border: 1px solid #522;
                    color: #f8caca;
                }

                .nav-link {
                    display: inline-block;
                    margin: 10px 10px 10px 0;
                    padding: 8px 12px;
                    color: var(--muted);
                    text-decoration: none;
                    border: 1px solid var(--line);
                    border-radius: 8px;
                    transition: all 0.2s;
                }

                .nav-link:hover {
                    color: var(--ink);
                    border-color: var(--brand);
                }

                .input-help {
                    font-size: 13px;
                    color: var(--muted);
                    margin-top: 4px;
                }

                @media (max-width: 768px) {
                    .grid2 {
                        grid-template-columns: 1fr;
                        gap: 20px;
                    }

                    .wrap {
                        padding: 15px;
                    }
                }
            </style>
        </head>
        <body>
            <section class="hero-small">
                <div class="wrap">
                    <div style="margin-bottom: 15px;">
                        <a href="/home" class="nav-link">Home</a>
                        <a href="/data" class="nav-link">Public Data</a>
                        <a href="/about" class="nav-link">About</a>
                        <a href="/contact" class="nav-link">Contact</a>
                    </div>
                    <h1>Solar Data Input</h1>
                    <p class="lead">Input your solar plant data and visualize performance metrics with interactive charts.</p>
                </div>
            </section>

            <main class="wrap">
                <div class="grid2">
                    <div class="panel">
                        <h2>Input Solar Data</h2>
                        <div id="alertContainer"></div>

                        <form id="dataForm">
                            <div class="form-group">
                                <label for="plantName">Plant Name *</label>
                                <input type="text" id="plantName" class="form-input" required>
                                <div class="input-help">Enter the name of your solar plant</div>
                            </div>

                            <div class="form-group">
                                <label for="generation">Current Generation (MW) *</label>
                                <input type="number" id="generation" class="form-input" step="0.1" min="0" max="10000" required>
                                <div class="input-help">Current power output in megawatts</div>
                            </div>

                            <div class="form-group">
                                <label for="capacity">Plant Capacity (MW) *</label>
                                <input type="number" id="capacity" class="form-input" step="0.1" min="0.1" max="10000" required>
                                <div class="input-help">Maximum generation capacity</div>
                            </div>

                            <div class="form-group">
                                <label for="efficiency">Efficiency (%) *</label>
                                <input type="number" id="efficiency" class="form-input" step="0.1" min="0" max="100" required>
                                <div class="input-help">Current efficiency percentage</div>
                            </div>

                            <div class="form-group">
                                <label for="temperature">Temperature (°C)</label>
                                <input type="number" id="temperature" class="form-input" step="0.1" min="-50" max="70" placeholder="25">
                                <div class="input-help">Ambient temperature (optional)</div>
                            </div>

                            <div class="form-group">
                                <label for="irradiance">Solar Irradiance (W/m²)</label>
                                <input type="number" id="irradiance" class="form-input" step="1" min="0" max="1500" placeholder="1000">
                                <div class="input-help">Solar irradiance level (optional)</div>
                            </div>

                            <div class="form-group">
                                <label for="revenue">Revenue Generated ($)</label>
                                <input type="number" id="revenue" class="form-input" step="0.01" min="0" placeholder="0.00">
                                <div class="input-help">Revenue for this period (optional)</div>
                            </div>

                            <button type="submit" class="btn primary" id="submitBtn">Submit Data</button>
                        </form>
                    </div>

                    <div class="panel">
                        <h2>Performance Visualization</h2>
                        <div class="chart-container">
                            <canvas id="performanceChart"></canvas>
                        </div>
                        <button type="button" class="btn" onclick="refreshChart()" style="margin-top: 15px;">
                            Refresh Chart
                        </button>
                    </div>
                </div>
            </main>

            <script>
                let chart = null;

                // Initialize chart
                function initChart() {
                    const ctx = document.getElementById('performanceChart').getContext('2d');
                    chart = new Chart(ctx, {
                        type: 'line',
                        data: {
                            labels: [],
                            datasets: [{
                                label: 'Generation (MW)',
                                data: [],
                                borderColor: '#e22323',
                                backgroundColor: 'rgba(226, 35, 35, 0.1)',
                                tension: 0.4,
                                fill: false
                            }, {
                                label: 'Efficiency (%)',
                                data: [],
                                borderColor: '#2563eb',
                                backgroundColor: 'rgba(37, 99, 235, 0.1)',
                                tension: 0.4,
                                fill: false,
                                yAxisID: 'y1'
                            }]
                        },
                        options: {
                            responsive: true,
                            maintainAspectRatio: false,
                            plugins: {
                                legend: {
                                    labels: {
                                        color: '#e8eaf0'
                                    }
                                },
                                title: {
                                    display: true,
                                    text: 'Solar Plant Performance Trends',
                                    color: '#e8eaf0'
                                }
                            },
                            scales: {
                                x: {
                                    ticks: {
                                        color: '#99a1b3'
                                    },
                                    grid: {
                                        color: '#1f2330'
                                    }
                                },
                                y: {
                                    type: 'linear',
                                    display: true,
                                    position: 'left',
                                    title: {
                                        display: true,
                                        text: 'Generation (MW)',
                                        color: '#99a1b3'
                                    },
                                    ticks: {
                                        color: '#99a1b3'
                                    },
                                    grid: {
                                        color: '#1f2330'
                                    }
                                },
                                y1: {
                                    type: 'linear',
                                    display: true,
                                    position: 'right',
                                    title: {
                                        display: true,
                                        text: 'Efficiency (%)',
                                        color: '#99a1b3'
                                    },
                                    ticks: {
                                        color: '#99a1b3'
                                    },
                                    grid: {
                                        drawOnChartArea: false
                                    }
                                }
                            }
                        }
                    });
                }

                // Load user data and update chart
                async function loadUserData() {
                    try {
                        const response = await fetch('/data-input/api/user-data');
                        if (!response.ok) {
                            throw new Error(`HTTP ${response.status}: ${response.statusText}`);
                        }

                        const data = await response.json();
                        updateChart(data);
                    } catch (error) {
                        console.error('Error loading user data:', error);
                        showAlert('Failed to load chart data: ' + error.message, 'error');
                    }
                }

                // Update chart with data
                function updateChart(data) {
                    if (!chart || !data || data.length === 0) {
                        return;
                    }

                    // Sort by timestamp and take last 10 entries
                    const sortedData = data.sort((a, b) => new Date(a.timestamp) - new Date(b.timestamp)).slice(-10);

                    const labels = sortedData.map(entry => {
                        const date = new Date(entry.timestamp);
                        return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'});
                    });

                    const generationData = sortedData.map(entry => entry.generation || 0);
                    const efficiencyData = sortedData.map(entry => entry.efficiency || 0);

                    chart.data.labels = labels;
                    chart.data.datasets[0].data = generationData;
                    chart.data.datasets[1].data = efficiencyData;
                    chart.update();
                }

                // Show alert message
                function showAlert(message, type) {
                    const container = document.getElementById('alertContainer');
                    container.innerHTML = `<div class="alert ${type}">${message}</div>`;
                    setTimeout(() => {
                        container.innerHTML = '';
                    }, 5000);
                }

                // Handle form submission
                document.getElementById('dataForm').addEventListener('submit', async function(e) {
                    e.preventDefault();

                    const submitBtn = document.getElementById('submitBtn');
                    submitBtn.disabled = true;
                    submitBtn.textContent = 'Submitting...';

                    const formData = {
                        plantName: document.getElementById('plantName').value,
                        generation: parseFloat(document.getElementById('generation').value),
                        capacity: parseFloat(document.getElementById('capacity').value),
                        efficiency: parseFloat(document.getElementById('efficiency').value),
                        temperature: parseFloat(document.getElementById('temperature').value) || 25.0,
                        irradiance: parseFloat(document.getElementById('irradiance').value) || 1000.0,
                        revenue: parseFloat(document.getElementById('revenue').value) || 0.0
                    };

                    try {
                        const response = await fetch('/data-input/api/submit', {
                            method: 'POST',
                            headers: {
                                'Content-Type': 'application/json',
                            },
                            body: JSON.stringify(formData)
                        });

                        const result = await response.json();

                        if (result.success) {
                            showAlert('Data submitted successfully!', 'success');
                            document.getElementById('dataForm').reset();
                            setTimeout(() => loadUserData(), 1000); // Refresh chart after 1 second
                        } else {
                            showAlert(result.error || 'Failed to submit data', 'error');
                        }
                    } catch (error) {
                        console.error('Error submitting data:', error);
                        showAlert('Network error: Failed to submit data', 'error');
                    } finally {
                        submitBtn.disabled = false;
                        submitBtn.textContent = 'Submit Data';
                    }
                });

                // Refresh chart function
                function refreshChart() {
                    loadUserData();
                }

                // Initialize on page load
                document.addEventListener('DOMContentLoaded', function() {
                    initChart();
                    loadUserData();
                });
            </script>
        </body>
        </html>
        """;
    }
}