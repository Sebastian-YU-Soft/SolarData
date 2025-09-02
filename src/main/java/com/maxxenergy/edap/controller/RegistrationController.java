package com.maxxenergy.edap.controller;

import com.maxxenergy.edap.model.User;
import com.maxxenergy.edap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller for user registration functionality.
 * Handles both HTML form submissions and API requests.
 */
@Controller
@RequestMapping("/register")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private UserService userService;

    /**
     * Show registration form (HTML)
     */
    @GetMapping
    public ResponseEntity<String> showRegistrationForm() {
        logger.debug("Showing registration form");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(generateRegistrationForm(null, null, null, null));
    }

    /**
     * Process registration form submission (HTML)
     */
    @PostMapping
    public ResponseEntity<String> processRegistration(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {

        logger.info("Processing registration for email: {}", email);

        try {
            User user = userService.registerUser(name, email, password);
            logger.info("User registered successfully: {} ({})", user.getName(), user.getEmail());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateSuccessPage(user.getName(), user.getEmail()));

        } catch (Exception e) {
            logger.warn("Registration failed for {}: {}", email, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateRegistrationForm(name, email, "", e.getMessage()));
        }
    }

    /**
     * Process registration via API (JSON)
     */
    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> processRegistrationApi(
            @RequestBody Map<String, String> request) {

        Map<String, Object> response = new HashMap<>();

        try {
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");

            logger.info("API registration attempt for email: {}", email);

            User user = userService.registerUser(name, email, password);

            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());
            response.put("name", user.getName());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());

            logger.info("API registration successful for: {}", user.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            logger.warn("API registration failed: {}", e.getMessage());

            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("timestamp", System.currentTimeMillis());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Check if email is available (API endpoint for frontend validation)
     */
    @GetMapping("/api/check-email")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> checkEmailAvailability(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        try { boolean isAvailable = !userService.findByEmail(email).isPresent();
            response.put("available", isAvailable);
            response.put("email", email);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("Error checking email availability: {}", e.getMessage());
            response.put("error", "Unable to check email availability");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Generate HTML registration form
     */
    private String generateRegistrationForm(String name, String email, String password, String error) {
        String html = """
        <!doctype html>
        <html lang="en"><head>
          <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
          <title>Create account · EDAP</title>
          <style>
            :root{--bg:#0b0c10; --card:#111217; --ink:#e8eaf0; --muted:#99a1b3; --line:#1f2330; --brand:#e22323; --brand2:#8b1111;}
            body{margin:0;background:linear-gradient(180deg,#0b0c10 0%, #0e1117 100%);color:var(--ink);
                 font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
            .wrap{max-width:520px;margin:48px auto;padding:0 18px}
            .card{background:var(--card);border:1px solid var(--line);border-radius:18px;padding:22px}
            h1{margin:0 0 10px}
            label{display:block;margin:12px 0 6px; font-weight:600}
            input{width:100%;padding:12px 12px;border-radius:12px;border:1px solid var(--line);background:#0d1017;color:var(--ink);box-sizing:border-box}
            input:focus{outline:none;border-color:var(--brand)}
            .actions{margin-top:16px;display:flex;gap:10px}
            .btn{padding:12px 16px;border-radius:14px;border:1px solid var(--line);text-decoration:none;color:var(--ink);cursor:pointer;background:var(--card);font-size:15px}
            .btn.primary{background:linear-gradient(180deg,var(--brand),var(--brand2));border:0;color:white}
            .btn:hover{transform:translateY(-1px)}
            .muted{color:var(--muted)}
            .error{background:#2a0f12;border:1px solid #522;color:#f8caca;padding:10px;border-radius:12px;margin:10px 0}
            .nav-links{margin-bottom:20px}
            .nav-link{display:inline-block;margin-right:15px;color:var(--muted);text-decoration:none;padding:8px 12px;border:1px solid var(--line);border-radius:8px;transition:all 0.2s}
            .nav-link:hover{color:var(--ink);border-color:var(--brand)}
          </style>
        </head><body>
          <div class="wrap">
            <div class="nav-links">
              <a href="/home" class="nav-link">Home</a>
              <a href="/about" class="nav-link">About</a>
              <a href="/data" class="nav-link">Data</a>
              <a href="/contact" class="nav-link">Contact</a>
            </div>
            <div class="card">
              <h1>Create your account</h1>
              <p class="muted">Access member-only features with a free account.</p>
              {{ERROR}}
              <form method="POST" action="/register">
                <label for="name">Full name *</label>
                <input id="name" name="name" value="{{NAME}}" required />

                <label for="email">Email address *</label>
                <input id="email" type="email" name="email" value="{{EMAIL}}" required />

                <label for="password">Password *</label>
                <input id="password" type="password" name="password" value="{{PASSWORD}}" minlength="8" required />
                <div class="muted" style="font-size:13px;margin-top:4px">At least 8 characters with letters and numbers</div>

                <div class="actions">
                  <button class="btn primary" type="submit">Create account</button>
                  <a class="btn" href="/home">Cancel</a>
                </div>
              </form>
            </div>
          </div>
        </body></html>
        """;

        html = html.replace("{{ERROR}}", error != null ? "<div class=\"error\">" + escapeHtml(error) + "</div>" : "");
        html = html.replace("{{NAME}}", escapeHtml(name != null ? name : ""));
        html = html.replace("{{EMAIL}}", escapeHtml(email != null ? email : ""));
        html = html.replace("{{PASSWORD}}", escapeHtml(password != null ? password : ""));
        return html;
    }

    /**
     * Generate HTML success page
     */
    private String generateSuccessPage(String name, String email) {
        String html = """
        <!doctype html><html lang="en"><head>
        <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
        <title>Registration complete · EDAP</title>
        <style>
          body{margin:0;background:linear-gradient(180deg,#0b0c10 0%, #0e1117 100%);color:#e8eaf0;font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
          .wrap{max-width:720px;margin:40px auto;padding:0 16px}
          .card{background:#111217;border:1px solid #1f2330;border-radius:16px;padding:30px}
          .success-icon{font-size:48px;text-align:center;margin-bottom:20px}
          h2{margin:0 0 15px;text-align:center;color:#4ade80}
          p{margin:10px 0;line-height:1.6}
          a.btn{display:inline-block;margin:20px 10px 0 0;padding:12px 20px;border-radius:12px;border:1px solid #1f2330;color:#e8eaf0;text-decoration:none;background:#111217;transition:all 0.2s}
          a.btn:hover{background:#1a1f2b;transform:translateY(-1px)}
          a.btn.primary{background:linear-gradient(180deg,#e22323,#8b1111);border:0;color:white}
          .actions{text-align:center;margin-top:25px}
        </style></head><body>
        <div class="wrap">
          <div class="card">
            <div class="success-icon">✅</div>
            <h2>Account Created Successfully!</h2>
            <p>Welcome, <strong>{{NAME}}</strong>! Your account with email <strong>{{EMAIL}}</strong> has been created.</p>
            <p>You can now access member-only features including:</p>
            <ul style="margin:15px 0;padding-left:20px">
              <li>Personal data input and visualization tools</li>
              <li>Advanced analytics and reporting</li>
              <li>Secure data storage and management</li>
              <li>Role-based access to private datasets</li>
            </ul>
            <p>Next step: Start exploring the platform or input your first solar data entry.</p>
            <div class="actions">
              <a class="btn primary" href="/data-input">Start Data Input</a>
              <a class="btn" href="/data">View Dashboard</a>
              <a class="btn" href="/home">Back to Home</a>
            </div>
          </div>
        </div>
        </body></html>
        """;

        html = html.replace("{{NAME}}", escapeHtml(name));
        html = html.replace("{{EMAIL}}", escapeHtml(email));
        return html;
    }

    /**
     * Escape HTML special characters to prevent XSS
     */
    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
