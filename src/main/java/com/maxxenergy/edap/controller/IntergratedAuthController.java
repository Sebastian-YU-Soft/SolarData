package com.maxxenergy.edap.controller;

import com.maxxenergy.edap.model.User;
import com.maxxenergy.edap.service.UserService;
import com.maxxenergy.edap.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

/**
 * Fixed integrated authentication controller with corrected HTML forms.
 */
@Controller
@RequestMapping("/auth")
public class IntegratedAuthController {

    private static final Logger logger = LoggerFactory.getLogger(IntegratedAuthController.class);
    private static final String SESSION_COOKIE = "edap_session";

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    // ===== LOGIN FUNCTIONALITY =====

    /**
     * Show login form
     */
    @GetMapping("/login")
    public ResponseEntity<String> showLoginForm(HttpServletRequest request) {
        logger.debug("Showing login form");

        // If already logged in, redirect to members area
        String email = getAuthenticatedEmail(request);
        if (email != null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/auth/members")
                    .build();
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(generateLoginForm(null, null));
    }

    /**
     * Process login form submission
     */
    @PostMapping("/login")
    public ResponseEntity<String> processLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpServletRequest request,
            HttpServletResponse response) {

        logger.info("Processing login for email: {}", email);

        try {
            String normalizedEmail = email != null ? email.toLowerCase().trim() : "";

            if (normalizedEmail.isEmpty() || password == null || password.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateLoginForm(email, "Both fields are required."));
            }

            // Authenticate user
            User user = userService.authenticateUser(normalizedEmail, password);
            if (user == null) {
                logger.warn("Failed login attempt for: {}", normalizedEmail);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateLoginForm(email, "Invalid email or password."));
            }

            // Create session
            String sessionToken = sessionService.createSession(user.getEmail());
            setSessionCookie(response, sessionToken, 8 * 60 * 60); // 8 hours

            // Update last login
            user.updateLastLogin();
            userService.updateUser(user);

            logger.info("User logged in successfully: {}", user.getEmail());

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/auth/members")
                    .build();

        } catch (Exception e) {
            logger.error("Login error: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateLoginForm(email, "An error occurred during login. Please try again."));
        }
    }

    // ===== LOGOUT FUNCTIONALITY =====

    /**
     * Handle logout
     */
    @GetMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        logger.debug("Processing logout");

        String sessionToken = getSessionToken(request);
        if (sessionToken != null) {
            sessionService.invalidateSession(sessionToken);
        }

        clearSessionCookie(response);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", "/home")
                .build();
    }

    // ===== MEMBERS AREA =====

    /**
     * Protected members area
     */
    @GetMapping("/members")
    public ResponseEntity<String> membersArea(HttpServletRequest request) {
        logger.debug("Accessing members area");

        String email = getAuthenticatedEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/auth/login")
                    .build();
        }

        try {
            User user = userService.findByEmail(email).orElse(null);
            String name = user != null ? user.getName() : email;

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateMembersPage(name, email, user != null ? user.getRole() : "staff"));

        } catch (Exception e) {
            logger.error("Error loading members area: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateErrorPage("Unable to load members area"));
        }
    }

    // ===== PASSWORD RESET =====

    /**
     * Show forgot password form
     */
    @GetMapping("/forgot-password")
    public ResponseEntity<String> showForgotPasswordForm() {
        logger.debug("Showing forgot password form");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(generateForgotPasswordForm(null, null));
    }

    /**
     * Process forgot password form
     */
    @PostMapping("/forgot-password")
    public ResponseEntity<String> processForgotPassword(@RequestParam String email) {
        logger.info("Processing forgot password request for: {}", email);

        try {
            String normalizedEmail = email != null ? email.toLowerCase().trim() : "";

            if (normalizedEmail.isEmpty() || !isValidEmail(normalizedEmail)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateForgotPasswordForm(email, "Please enter a valid email address."));
            }

            // Generate reset token if user exists
            if (userService.findByEmail(normalizedEmail).isPresent()) {
                String resetToken = sessionService.createPasswordResetToken(normalizedEmail);
                String resetLink = "http://localhost:8080/auth/reset-password?token=" + resetToken;

                // In production, send email. For demo, log to console
                logger.info("=== Password reset link for {} ===", normalizedEmail);
                logger.info(resetLink);
            }

            // Always show success message (prevent user enumeration)
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateInfoPage(
                            "Check your email",
                            "If an account exists for " + escapeHtml(email) + ", a password reset link has been sent.",
                            "Back to Login", "/auth/login"));

        } catch (Exception e) {
            logger.error("Error processing forgot password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateForgotPasswordForm(email, "An error occurred. Please try again."));
        }
    }

    /**
     * Show reset password form
     */
    @GetMapping("/reset-password")
    public ResponseEntity<String> showResetPasswordForm(@RequestParam String token) {
        logger.debug("Showing reset password form");

        if (token == null || !sessionService.isValidResetToken(token)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateInfoPage(
                            "Invalid or expired link",
                            "Your password reset link is invalid or has expired. Please request a new one.",
                            "Request new link", "/auth/forgot-password"));
        }

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(generateResetPasswordForm(token, null));
    }

    /**
     * Process reset password form
     */
    @PostMapping("/reset-password")
    public ResponseEntity<String> processResetPassword(
            @RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirm) {

        logger.info("Processing password reset");

        try {
            if (token == null || !sessionService.isValidResetToken(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateInfoPage(
                                "Invalid or expired link",
                                "Your password reset link is invalid or has expired.",
                                "Request new link", "/auth/forgot-password"));
            }

            if (password == null || password.isEmpty() || confirm == null || confirm.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateResetPasswordForm(token, "Both password fields are required."));
            }

            if (!password.equals(confirm)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateResetPasswordForm(token, "Passwords do not match."));
            }

            // Validate password strength
            String passwordError = validatePasswordStrength(password);
            if (passwordError != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateResetPasswordForm(token, passwordError));
            }

            // Reset password
            String email = sessionService.getEmailFromResetToken(token);
            if (email != null) {
                userService.resetPassword(email, password);
                sessionService.invalidateResetToken(token);

                logger.info("Password reset successfully for: {}", email);

                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateInfoPage(
                                "Password updated",
                                "Your password has been updated successfully. You can now log in with your new password.",
                                "Go to login", "/auth/login"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateInfoPage(
                                "Error",
                                "Unable to reset password. Please try again.",
                                "Request new link", "/auth/forgot-password"));
            }

        } catch (Exception e) {
            logger.error("Error resetting password: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateResetPasswordForm(token, "An error occurred. Please try again."));
        }
    }

    // ===== UTILITY METHODS =====

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

    private void setSessionCookie(HttpServletResponse response, String token, int maxAgeSeconds) {
        Cookie cookie = new Cookie(SESSION_COOKIE, token);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAgeSeconds);
        response.addCookie(cookie);
    }

    private void clearSessionCookie(HttpServletResponse response) {
        Cookie cookie = new Cookie(SESSION_COOKIE, "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");
    }

    private String validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^A-Za-z\\d].*");

        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            return "Password must contain uppercase, lowercase, number, and special character.";
        }

        return null;
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    // ===== HTML GENERATION METHODS =====

    private String generateLoginForm(String email, String error) {
        return """
                <!doctype html><html lang="en"><head>
                  <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
                  <title>Log in · EDAP</title>
                  <style>
                    :root{--bg:#0b0c10; --card:#111217; --ink:#e8eaf0; --muted:#99a1b3; --line:#1f2330; --brand:#e22323; --brand2:#8b1111;}
                    body{margin:0;background:linear-gradient(180deg,#0b0c10,#0e1117);color:var(--ink);
                         font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
                    .wrap{max-width:520px;margin:48px auto;padding:0 18px}
                    .card{background:var(--card);border:1px solid var(--line);border-radius:18px;padding:22px}
                    label{display:block;margin:12px 0 6px;font-weight:600}
                    input{width:100%;padding:12px;border-radius:12px;border:1px solid var(--line);background:#0d1017;color:var(--ink);box-sizing:border-box}
                    input:focus{outline:none;border-color:var(--brand)}
                    .btn{padding:12px 16px;border-radius:14px;border:1px solid var(--line);text-decoration:none;color:var(--ink);cursor:pointer;background:var(--card);font-size:15px}
                    .btn.primary{background:linear-gradient(180deg,var(--brand),var(--brand2));border:0;color:white}
                    .btn:hover{transform:translateY(-1px)}
                    .error{background:#2a0f12;border:1px solid #522;color:#f8caca;padding:10px;border-radius:12px;margin:10px 0}
                    .actions{margin-top:16px;display:flex;gap:10px;flex-wrap:wrap}
                    .muted{color:var(--muted)}
                    .nav-links{margin-bottom:20px}
                    .nav-link{display:inline-block;margin-right:15px;color:var(--muted);text-decoration:none;padding:8px 12px;border:1px solid var(--line);border-radius:8px;transition:all 0.2s}
                    .nav-link:hover{color:var(--ink);border-color:var(--brand)}
                  </style></head><body>
                  <div class="wrap">
                    <div class="nav-links">
                      <a href="/home" class="nav-link">Home</a>
                      <a href="/about" class="nav-link">About</a>
                      <a href="/data" class="nav-link">Data</a>
                      <a href="/register" class="nav-link">Register</a>
                    </div>
                    <div class="card">
                      <h1>Log in to EDAP</h1>
                      <p class="muted">Use your email and password to access member-only features.</p>
                """ + (error != null ? "<div class=\"error\">" + escapeHtml(error) + "</div>" : "") + """
                      <form method="POST" action="/auth/login">
                        <label for="email">Email</label>
                        <input id="email" type="email" name="email" value=\"""" + escapeHtml(email != null ? email : "") + """\" required />

                        <label for="password">Password</label>
                        <input id="password" type="password" name="password" required />

                        <div class="actions">
                          <button class="btn primary" type="submit">Log in</button>
                          <a class="btn" href="/auth/forgot-password">Forgot password?</a>
                          <a class="btn" href="/register">Create account</a>
                        </div>
                      </form>
                    </div>
                  </div>
                </body></html>
                """;
    }

    private String generateMembersPage(String name, String email, String role) {
        return """
                <!doctype html><html lang="en"><head>
                  <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
                  <title>Members Area · EDAP</title>
                  <style>
                    :root{--bg:#0b0c10; --card:#111217; --ink:#e8eaf0; --muted:#99a1b3; --line:#1f2330; --brand:#e22323;}
                    body{margin:0;background:linear-gradient(180deg,#0b0c10,#0e1117);color:var(--ink);
                         font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
                    .wrap{max-width:900px;margin:48px auto;padding:0 18px}
                    .card{background:var(--card);border:1px solid var(--line);border-radius:18px;padding:22px}
                    h1{margin:0 0 10px}
                    .muted{color:var(--muted)}
                    .grid{display:grid;grid-template-columns:1fr 1fr;gap:16px;margin-top:12px}
                    @media (max-width:920px){.grid{grid-template-columns:1fr}}
                    a.btn{display:inline-block;margin-top:12px;padding:10px 14px;border-radius:12px;border:1px solid var(--line);color:var(--ink);text-decoration:none}
                    a.btn:hover{background:#1a1f2b}
                    ul{margin:8px 0 0 20px}
                    .role-badge{background:var(--brand);color:white;padding:4px 8px;border-radius:6px;font-size:12px;font-weight:600;text-transform:uppercase}
                  </style></head><body>
                  <div class="wrap">
                    <div class="card">
                      <h1>Welcome, """ + escapeHtml(name) + """!</h1>
                      <p class="muted">You are signed in as <strong>""" + escapeHtml(email) + """</strong>
                      <span class="role-badge">""" + escapeHtml(role) + """</span></p>
                      <div class="grid">
                        <div>
                          <h3>Member Features</h3>
                          <ul>
                            <li><a href="/data-input">Input Solar Data</a></li>
                            <li><a href="/data">View Dashboard</a></li>
                            <li>Private energy KPIs</li>
                            <li>Revenue drilldowns</li>
                            <li>Download CSVs</li>
                          </ul>
                          <a class="btn" href="/home">Back to Home</a>
                          <a class="btn" href="/profile">My Profile</a>
                          <a class="btn" href="/auth/logout">Log out</a>
                        </div>
                        <div class="card">
                          <h3>Account Information</h3>
                          <ul>
                            <li>Name: """ + escapeHtml(name) + """</li>
                            <li>Email: """ + escapeHtml(email) + """</li>
                            <li>Role: """ + escapeHtml(role) + """</li>
                          </ul>
                        </div>
                      </div>
                    </div>
                  </div>
                </body></html>
                """;
    }

    private String generateForgotPasswordForm(String email, String error) {
        return """
                <!doctype html><html lang="en"><head>
                  <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
                  <title>Forgot Password · EDAP</title>
                  <style>
                    :root{--bg:#0b0c10; --card:#111217; --ink:#e8eaf0; --muted:#99a1b3; --line:#1f2330; --brand:#e22323; --brand2:#8b1111;}
                    body{margin:0;background:linear-gradient(180deg,#0b0c10,#0e1117);color:var(--ink);
                         font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
                    .wrap{max-width:520px;margin:48px auto;padding:0 18px}
                    .card{background:var(--card);border:1px solid var(--line);border-radius:18px;padding:22px}
                    label{display:block;margin:12px 0 6px;font-weight:600}
                    input{width:100%;padding:12px;border-radius:12px;border:1px solid var(--line);background:#0d1017;color:var(--ink);box-sizing:border-box}
                    .btn{padding:12px 16px;border-radius:14px;border:1px solid var(--line);text-decoration:none;color:var(--ink);cursor:pointer;background:var(--card);font-size:15px}
                    .btn.primary{background:linear-gradient(180deg,var(--brand),var(--brand2));border:0;color:white}
                    .error{background:#2a0f12;border:1px solid #522;color:#f8caca;padding:10px;border-radius:12px;margin:10px 0}
                    .actions{margin-top:16px;display:flex;gap:10px;flex-wrap:wrap}
                    .muted{color:var(--muted)}
                  </style></head><body>
                  <div class="wrap">
                    <div class="card">
                      <h1>Forgot your password?</h1>
                      <p class="muted">Enter your email and we'll send you a reset link.</p>
                """ + (error != null ? "<div class=\"error\">" + escapeHtml(error) + "</div>" : "") + """
                      <form method="POST" action="/auth/forgot-password">
                        <label for="email">Email</label>
                        <input id="email" type="email" name="email" value=\"""" + escapeHtml(email != null ? email : "") + """\" required />
                        <div class="actions">
                          <button class="btn primary" type="submit">Send reset link</button>
                          <a class="btn" href="/auth/login">Back to login</a>
                        </div>
                      </form>
                    </div>
                  </div>
                </body></html>
                """;
    }

    private String generateResetPasswordForm(String token, String error) {
        return """
                <!doctype html><html lang="en"><head>
                  <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
                  <title>Reset Password · EDAP</title>
                  <style>
                    :root{--bg:#0b0c10; --card:#111217; --ink:#e8eaf0; --muted:#99a1b3; --line:#1f2330; --brand:#e22323; --brand2:#8b1111;}
                    body{margin:0;background:linear-gradient(180deg,#0b0c10,#0e1117);color:var(--ink);
                         font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
                    .wrap{max-width:520px;margin:48px auto;padding:0 18px}
                    .card{background:var(--card);border:1px solid var(--line);border-radius:18px;padding:22px}
                    label{display:block;margin:12px 0 6px;font-weight:600}
                    input{width:100%;padding:12px;border-radius:12px;border:1px solid var(--line);background:#0d1017;color:var(--ink);box-sizing:border-box}
                    .btn{padding:12px 16px;border-radius:14px;border:1px solid var(--line);text-decoration:none;color:var(--ink);cursor:pointer;background:var(--card);font-size:15px}
                    .btn.primary{background:linear-gradient(180deg,var(--brand),var(--brand2));border:0;color:white}
                    .error{background:#2a0f12;border:1px solid #522;color:#f8caca;padding:10px;border-radius:12px;margin:10px 0}
                    .actions{margin-top:16px;display:flex;gap:10px;flex-wrap:wrap}
                    .hint{margin-top:10px;color:var(--muted);font-size:13px}
                  </style></head><body>
                  <div class="wrap">
                    <div class="card">
                      <h1>Set a new password</h1>
                """ + (error != null ? "<div class=\"error\">" + escapeHtml(error) + "</div>" : "") + """
                      <form method="POST" action="/auth/reset-password">
                        <input type="hidden" name="token" value=\"""" + escapeHtml(token) + """\" />
                        <label for="password">New password</label>
                        <input id="password" type="password" name="password" minlength="8" required />
                        <div class="hint">Use 8+ characters with upper, lower, number, and symbol.</div>
                        <label for="confirm">Confirm new password</label>
                        <input id="confirm" type="password" name="confirm" minlength="8" required />
                        <div class="actions">
                          <button class="btn primary" type="submit">Update password</button>
                          <a class="btn" href="/auth/login">Cancel</a>
                        </div>
                      </form>
                    </div>
                  </div>
                </body></html>
                """;
    }

    private String generateInfoPage(String title, String message, String ctaText, String ctaHref) {
        return """
                <!doctype html><html lang="en"><head>
                <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>""" + escapeHtml(title) + """ · EDAP</title>
                <style>
                  body{margin:0;background:#0e1117;color:#e8eaf0;font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
                  .wrap{max-width:720px;margin:40px auto;padding:0 16px}
                  .card{background:#111217;border:1px solid #1f2330;border-radius:16px;padding:22px}
                  a.btn{display:inline-block;margin-top:12px;padding:10px 14px;border-radius:12px;border:1px solid #1f2330;color:#e8eaf0;text-decoration:none}
                  a.btn:hover{background:#1a1f2b}
                  .muted{color:#99a1b3}
                </style></head><body>
                <div class="wrap">
                  <div class="card">
                    <h2>""" + escapeHtml(title) + """</h2>
                    <p class="muted">""" + message + """</p>
                    <a class="btn" href=\"""" + escapeHtml(ctaHref) + """\">""" + escapeHtml(ctaText) + """</a>
                  </div>
                </div>
                </body></html>
                """;
    }

    private String generateErrorPage(String message) {
        return generateInfoPage("Error", message, "Back to Home", "/home");
    }
}