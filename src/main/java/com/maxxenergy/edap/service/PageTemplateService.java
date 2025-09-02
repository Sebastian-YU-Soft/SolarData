package com.maxxenergy.edap.service;

import org.springframework.stereotype.Service;

@Service
public class PageTemplateService {

    /**
     * Generates the complete HTML page template with the provided content
     *
     * @param title The page title
     * @param section The section identifier (used for styling/navigation)
     * @param content The main content HTML
     * @return Complete HTML page as string
     */
    public static String getPageTemplate(String title, String section, String content) {
        return String.format("""
            <!doctype html>
            <html lang="en">
            <head>
                <meta charset="utf-8"/>
                <meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>%s</title>
                <style>
                    :root{--bg:#0b0c10; --card:#111217; --ink:#e8eaf0; --muted:#99a1b3; --line:#1f2330; --brand:#e22323; --brand2:#8b1111;}
                    body{margin:0;background:linear-gradient(180deg,#0b0c10 0%%, #0e1117 100%%);color:var(--ink);font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
                    .wrap{max-width:1200px;margin:0 auto;padding:20px}
                    .hero{background:var(--card);border-bottom:1px solid var(--line);padding:40px 0}
                    .hero-small{background:var(--card);border-bottom:1px solid var(--line);padding:30px 0}
                    .hero-grid{display:grid;grid-template-columns:1fr auto;gap:40px;align-items:center}
                    .panel{background:var(--card);border:1px solid var(--line);border-radius:18px;padding:25px}
                    .btn{display:inline-block;padding:12px 20px;border-radius:12px;border:1px solid var(--line);background:var(--card);color:var(--ink);text-decoration:none;margin:5px 10px 5px 0;transition:all 0.2s}
                    .btn.primary{background:linear-gradient(180deg,var(--brand),var(--brand2));border:0;color:white}
                    .btn:hover{transform:translateY(-1px)}
                    .cards{display:grid;grid-template-columns:repeat(auto-fit,minmax(300px,1fr));gap:20px;margin:20px 0}
                    .card{background:var(--card);border:1px solid var(--line);border-radius:18px;padding:20px}
                    .muted{color:var(--muted)}
                    .lead{font-size:1.1em;color:var(--muted);margin:10px 0}
                    h1{margin:0 0 20px;font-size:2.5em}
                    h2{margin:0 0 15px}
                    h3{margin:0 0 10px}
                    .blog-grid{display:grid;grid-template-columns:repeat(auto-fit,minmax(350px,1fr));gap:25px}
                    .blog-card{background:var(--card);border:1px solid var(--line);border-radius:18px;padding:25px;transition:transform 0.2s}
                    .blog-card:hover{transform:translateY(-2px)}
                    .blog-meta{display:flex;gap:15px;margin-bottom:15px;font-size:0.9em;color:var(--muted)}
                    .date{background:var(--line);padding:4px 8px;border-radius:6px}
                    .category{background:var(--brand);padding:4px 8px;border-radius:6px;color:white}
                    .read-more{color:var(--brand);text-decoration:none;font-weight:500}
                    .read-more:hover{text-decoration:underline}
                    .dashboard-header{display:flex;justify-content:space-between;align-items:center;margin-bottom:20px}
                    .refresh-indicator{display:flex;align-items:center;gap:8px;color:var(--muted)}
                    .status-dot{width:8px;height:8px;background:var(--brand);border-radius:50%;animation:pulse 2s infinite}
                    @keyframes pulse{0%,100%{opacity:1} 50%{opacity:0.5}}
                    .data-controls{display:flex;gap:20px;margin-top:20px}
                    .control-group{display:flex;flex-direction:column;gap:5px}
                    .control-select{background:var(--card);color:var(--ink);border:1px solid var(--line);border-radius:8px;padding:8px 12px}
                    .loading{text-align:center;color:var(--muted);padding:40px}
                    .timeline{margin:20px 0}
                    .tl{padding:10px 0;border-left:2px solid var(--line);padding-left:20px;margin-left:10px}
                </style>
            </head>
            <body>
                %s
                <script>
                    // Auto-refresh data every 30 seconds for dynamic content
                    if (document.getElementById('homeGeneration')) {
                        setInterval(function() {
                            fetch('/api/public/data')
                                .then(response => response.json())
                                .then(data => {
                                    if (data.generation) {
                                        document.getElementById('homeGeneration').textContent = data.generation.toFixed(1) + ' MW';
                                    }
                                    if (data.revenue) {
                                        document.getElementById('homeRevenue').textContent = '$' + data.revenue.toLocaleString();
                                    }
                                })
                                .catch(error => console.log('Error loading data:', error));
                        }, 30000);
                    }
                </script>
            </body>
            </html>
            """, title, content);
    }
}