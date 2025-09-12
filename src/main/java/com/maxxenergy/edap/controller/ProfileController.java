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
import javax.servlet.http.Cookie;
import java.util.Map;
import java.util.HashMap;

/**
 * Controller for user profile management.
 * Handles profile viewing, editing, and updating functionality.
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private static final Logger logger = LoggerFactory.getLogger(ProfileController.class);
    private static final String SESSION_COOKIE = "edap_session";

    @Autowired
    private UserService userService;

    @Autowired
    private SessionService sessionService;

    /**
     * Show user profile page
     */
    @GetMapping
    public ResponseEntity<String> showProfile(HttpServletRequest request) {
        logger.debug("Showing user profile");

        String email = getAuthenticatedEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/auth/login")
                    .body("");
        }

        try {
            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateErrorPage("User not found"));
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateProfilePage(user, null, null));

        } catch (Exception e) {
            logger.error("Error loading profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateErrorPage("Unable to load profile"));
        }
    }

    /**
     * Show edit profile form
     */
    @GetMapping("/edit")
    public ResponseEntity<String> showEditProfile(HttpServletRequest request) {
        logger.debug("Showing edit profile form");

        String email = getAuthenticatedEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/auth/login")
                    .body("");
        }

        try {
            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateErrorPage("User not found"));
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateEditProfilePage(user, null));

        } catch (Exception e) {
            logger.error("Error loading edit profile: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateErrorPage("Unable to load edit form"));
        }
    }

    /**
     * Process profile update
     */
    @PostMapping("/update")
    public ResponseEntity<String> updateProfile(
            @RequestParam String name,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            HttpServletRequest request) {

        logger.info("Processing profile update");

        String email = getAuthenticatedEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", "/auth/login")
                    .body("");
        }

        try {
            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateErrorPage("User not found"));
            }

            // Validate profile input
            String profileError = validateProfileInput(name, department, location);
            if (profileError != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.TEXT_HTML)
                        .body(generateEditProfilePage(user, profileError));
            }

            // Handle password change if provided
            if (isPasswordChangeRequired(currentPassword, newPassword, confirmPassword)) {
                String passwordError = handlePasswordChange(user, currentPassword, newPassword, confirmPassword);
                if (passwordError != null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .contentType(MediaType.TEXT_HTML)
                            .body(generateEditProfilePage(user, passwordError));
                }
            }

            // Update user profile
            user.setName(name.trim());
            user.setDepartment(department != null && !department.trim().isEmpty() ? department.trim() : null);
            user.setLocation(location != null && !location.trim().isEmpty() ? location.trim() : null);

            userService.updateUser(user);

            logger.info("Profile updated successfully for user: {}", email);

            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateProfilePage(user, "Profile updated successfully!", null));

        } catch (Exception e) {
            logger.error("Error updating profile: {}", e.getMessage());
            User user = userService.findByEmail(email).orElse(null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateEditProfilePage(user, "An error occurred while updating profile"));
        }
    }

    /**
     * API endpoint to get profile information
     */
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getProfileApi(HttpServletRequest request) {
        String email = getAuthenticatedEmail(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Not authenticated"));
        }

        try {
            User user = userService.findByEmail(email).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "User not found"));
            }

            Map<String, Object> profile = new HashMap<>();
            profile.put("name", user.getName());
            profile.put("email", user.getEmail());
            profile.put("role", user.getRole());
            profile.put("department", user.getDepartment());
            profile.put("location", user.getLocation());
            profile.put("createdAt", user.getCreatedAt());
            profile.put("lastLogin", user.getLastLogin());
            profile.put("isActive", user.isActive());

            return ResponseEntity.ok(profile);

        } catch (Exception e) {
            logger.error("Error getting profile API: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Internal server error"));
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

    private String validatePasswordStrength(String password) {
        if (password == null || password.length() < 8) {
            return "Password must be at least 8 characters long";
        }

        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^A-Za-z\\d].*");

        if (!hasUpper || !hasLower || !hasDigit || !hasSpecial) {
            return "Password must contain uppercase, lowercase, number, and special character";
        }

        return null;
    }

    private String validateProfileInput(String name, String department, String location) {
        if (name == null || name.trim().isEmpty()) {
            return "Name is required";
        }

        String trimmedName = name.trim();
        if (trimmedName.length() < 2) {
            return "Name must be at least 2 characters long";
        }
        if (trimmedName.length() > 100) {
            return "Name cannot exceed 100 characters";
        }

        // Check for invalid characters in name
        if (!trimmedName.matches("^[a-zA-Z\\s\\-\\.]+$")) {
            return "Name can only contain letters, spaces, hyphens, and periods";
        }

        // Validate department if provided
        if (department != null && !department.trim().isEmpty()) {
            if (department.trim().length() > 100) {
                return "Department cannot exceed 100 characters";
            }
        }

        // Validate location if provided
        if (location != null && !location.trim().isEmpty()) {
            if (location.trim().length() > 100) {
                return "Location cannot exceed 100 characters";
            }
        }

        return null; // Valid input
    }

    private boolean isPasswordChangeRequired(String currentPassword, String newPassword, String confirmPassword) {
        return (currentPassword != null && !currentPassword.trim().isEmpty()) ||
                (newPassword != null && !newPassword.trim().isEmpty()) ||
                (confirmPassword != null && !confirmPassword.trim().isEmpty());
    }

    private String handlePasswordChange(User user, String currentPassword, String newPassword, String confirmPassword) {
        // Verify current password
        User authUser = userService.authenticateUser(user.getEmail(), currentPassword);
        if (authUser == null) {
            return "Current password is incorrect";
        }

        if (newPassword == null || newPassword.isEmpty()) {
            return "New password is required";
        }

        if (!newPassword.equals(confirmPassword)) {
            return "New passwords do not match";
        }

        // Validate password strength
        String passwordError = validatePasswordStrength(newPassword);
        if (passwordError != null) {
            return passwordError;
        }

        try {
            // Update password
            userService.resetPassword(user.getEmail(), newPassword);
            return null; // Success
        } catch (Exception e) {
            logger.error("Error updating password: {}", e.getMessage());
            return "Failed to update password";
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

    // ===== HTML GENERATION METHODS =====

    private String generateProfilePage(User user, String successMessage, String errorMessage) {
        // Null safety checks
        if (user == null) {
            return generateErrorPage("User data not available");
        }

        String safeName = escapeHtml(user.getName() != null ? user.getName() : "Unknown User");
        String safeEmail = escapeHtml(user.getEmail() != null ? user.getEmail() : "No email");
        String safeRole = escapeHtml(user.getRole() != null ? user.getRole() : "staff");
        String safeDepartment = escapeHtml(user.getDepartment() != null ? user.getDepartment() : "Not specified");
        String safeLocation = escapeHtml(user.getLocation() != null ? user.getLocation() : "Not specified");
        String createdDate = user.getCreatedAt() != null ? user.getCreatedAt().toLocalDate().toString() : "Unknown";

        return """
                <!doctype html><html lang="en"><head>
                  <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
                  <title>My Profile · EDAP</title>
                  <style>
                    :root{--bg:#0b0c10; --card:#111217; --ink:#e8eaf0; --muted:#99a1b3; --line:#1f2330; --brand:#e22323; --brand2:#8b1111;}
                    body{margin:0;background:linear-gradient(180deg,#0b0c10,#0e1117);color:var(--ink);
                         font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
                    .wrap{max-width:800px;margin:48px auto;padding:0 18px}
                    .card{background:var(--card);border:1px solid var(--line);border-radius:18px;padding:25px}
                    h1{margin:0 0 20px}
                    .profile-grid{display:grid;grid-template-columns:1fr 1fr;gap:20px;margin:20px 0}
                    .profile-item{margin:15px 0}
                    .profile-label{font-weight:600;color:var(--muted);font-size:14px;margin-bottom:5px}
                    .profile-value{color:var(--ink);font-size:16px}
                    .btn{display:inline-block;padding:12px 20px;border-radius:12px;border:1px solid var(--line);background:var(--card);color:var(--ink);text-decoration:none;margin:10px 10px 0 0;transition:all 0.2s}
                    .btn.primary{background:linear-gradient(180deg,var(--brand),var(--brand2));border:0;color:white}
                    .btn:hover{transform:translateY(-1px)}
                    .success{background:#1a3d2e;border:1px solid #2d6a4f;color:#a7f3d0;padding:15px;border-radius:12px;margin:20px 0}
                    .error{background:#2a0f12;border:1px solid #522;color:#f8caca;padding:15px;border-radius:12px;margin:20px 0}
                    .nav-links{margin-bottom:20px}
                    .nav-link{display:inline-block;margin-right:15px;color:var(--muted);text-decoration:none;padding:8px 12px;border:1px solid var(--line);border-radius:8px;transition:all 0.2s}
                    .nav-link:hover{color:var(--ink);border-color:var(--brand)}
                    @media (max-width:768px){.profile-grid{grid-template-columns:1fr}}
                  </style></head><body>
                  <div class="wrap">
                    <div class="nav-links">
                      <a href="/home" class="nav-link">Home</a>
                      <a href="/auth/members" class="nav-link">Members Area</a>
                      <a href="/data" class="nav-link">Dashboard</a>
                      <a href="/data-input" class="nav-link">Data Input</a>
                    </div>
                    <div class="card">
                      <h1>My Profile</h1>
                """ + (successMessage != null ? "<div class=\"success\">" + escapeHtml(successMessage) + "</div>" : "") + """
                """ + (errorMessage != null ? "<div class=\"error\">" + escapeHtml(errorMessage) + "</div>" : "") + """
                      <div class="profile-grid">
                        <div>
                          <div class="profile-item">
                            <div class="profile-label">Full Name</div>
                            <div class="profile-value">""" + safeName + """</div>
                          </div>
                          <div class="profile-item">
                            <div class="profile-label">Email Address</div>
                            <div class="profile-value">""" + safeEmail + """</div>
                          </div>
                          <div class="profile-item">
                            <div class="profile-label">Role</div>
                            <div class="profile-value">""" + safeRole + """</div>
                          </div>
                        </div>
                        <div>
                          <div class="profile-item">
                            <div class="profile-label">Department</div>
                            <div class="profile-value">""" + safeDepartment + """</div>
                          </div>
                          <div class="profile-item">
                            <div class="profile-label">Location</div>
                            <div class="profile-value">""" + safeLocation + """</div>
                          </div>
                          <div class="profile-item">
                            <div class="profile-label">Account Created</div>
                            <div class="profile-value">""" + createdDate + """</div>
                          </div>
                        </div>
                      </div>
                      <div class="actions">
                        <a class="btn primary" href="/profile/edit">Edit Profile</a>
                        <a class="btn" href="/auth/members">Back to Members Area</a>
                      </div>
                    </div>
                  </div>
                </body></html>
                """;
    }

    private String generateEditProfilePage(User user, String errorMessage) {
        // Null safety checks
        if (user == null) {
            return generateErrorPage("User data not available");
        }

        String safeName = escapeHtml(user.getName() != null ? user.getName() : "");
        String safeDepartment = escapeHtml(user.getDepartment() != null ? user.getDepartment() : "");
        String safeLocation = escapeHtml(user.getLocation() != null ? user.getLocation() : "");

        return """
                <!doctype html><html lang="en"><head>
                  <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
                  <title>Edit Profile · EDAP</title>
                  <style>
                    :root{--bg:#0b0c10; --card:#111217; --ink:#e8eaf0; --muted:#99a1b3; --line:#1f2330; --brand:#e22323; --brand2:#8b1111;}
                    body{margin:0;background:linear-gradient(180deg,#0b0c10,#0e1117);color:var(--ink);
                         font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
                    .wrap{max-width:600px;margin:48px auto;padding:0 18px}
                    .card{background:var(--card);border:1px solid var(--line);border-radius:18px;padding:25px}
                    h1{margin:0 0 20px}
                    .form-group{margin-bottom:20px}
                    .form-group label{display:block;margin-bottom:8px;font-weight:600;color:var(--ink)}
                    .form-input{width:100%;padding:12px 15px;border:1px solid var(--line);border-radius:12px;background:#0d1017;color:var(--ink);font-size:15px;box-sizing:border-box}
                    .form-input:focus{outline:none;border-color:var(--brand)}
                    .btn{padding:12px 20px;border-radius:12px;border:1px solid var(--line);background:var(--card);color:var(--ink);cursor:pointer;font-size:15px;transition:all 0.2s;text-decoration:none;display:inline-block}
                    .btn.primary{background:linear-gradient(180deg,var(--brand),var(--brand2));border:0;color:white}
                    .btn:hover{transform:translateY(-1px)}
                    .btn:disabled{opacity:0.6;cursor:not-allowed;transform:none}
                    .error{background:#2a0f12;border:1px solid #522;color:#f8caca;padding:15px;border-radius:12px;margin:20px 0}
                    .section{border-top:1px solid var(--line);padding-top:20px;margin-top:30px}
                    .help-text{font-size:13px;color:var(--muted);margin-top:5px}
                    .actions{margin-top:25px;display:flex;gap:10px;flex-wrap:wrap}
                    .nav-links{margin-bottom:20px}
                    .nav-link{display:inline-block;margin-right:15px;color:var(--muted);text-decoration:none;padding:8px 12px;border:1px solid var(--line);border-radius:8px;transition:all 0.2s}
                    .nav-link:hover{color:var(--ink);border-color:var(--brand)}
                  </style></head><body>
                  <div class="wrap">
                    <div class="nav-links">
                      <a href="/profile" class="nav-link">View Profile</a>
                      <a href="/auth/members" class="nav-link">Members Area</a>
                    </div>
                    <div class="card">
                      <h1>Edit Profile</h1>
                """ + (errorMessage != null ? "<div class=\"error\">" + escapeHtml(errorMessage) + "</div>" : "") + """
                      <form method="POST" action="/profile/update" onsubmit="return validateForm()">
                        <div class="form-group">
                          <label for="name">Full Name *</label>
                          <input type="text" id="name" name="name" class="form-input" 
                                 value=\"""" + safeName + """\" 
                                 required maxlength="100" minlength="2">
                          <div class="help-text">Your full name as you'd like it to appear (2-100 characters)</div>
                        </div>

                        <div class="form-group">
                          <label for="department">Department</label>
                          <input type="text" id="department" name="department" class="form-input" 
                                 value=\"""" + safeDepartment + """\" 
                                 placeholder="e.g., Engineering, Operations, Finance" maxlength="100">
                          <div class="help-text">Your department or division (optional)</div>
                        </div>

                        <div class="form-group">
                          <label for="location">Location</label>
                          <input type="text" id="location" name="location" class="form-input" 
                                 value=\"""" + safeLocation + """\" 
                                 placeholder="e.g., New York, NY" maxlength="100">
                          <div class="help-text">Your primary work location (optional)</div>
                        </div>

                        <div class="section">
                          <h3>Change Password</h3>
                          <p style="color:var(--muted);font-size:14px;">Leave password fields empty if you don't want to change your password.</p>
                          
                          <div class="form-group">
                            <label for="currentPassword">Current Password</label>
                            <input type="password" id="currentPassword" name="currentPassword" class="form-input">
                          </div>

                          <div class="form-group">
                            <label for="newPassword">New Password</label>
                            <input type="password" id="newPassword" name="newPassword" class="form-input" 
                                   minlength="8" maxlength="128">
                            <div class="help-text">At least 8 characters with uppercase, lowercase, number, and special character</div>
                          </div>

                          <div class="form-group">
                            <label for="confirmPassword">Confirm New Password</label>
                            <input type="password" id="confirmPassword" name="confirmPassword" class="form-input" 
                                   minlength="8" maxlength="128">
                          </div>
                        </div>

                        <div class="actions">
                          <button type="submit" class="btn primary" id="submitBtn">Save Changes</button>
                          <a href="/profile" class="btn">Cancel</a>
                        </div>
                      </form>
                    </div>
                  </div>
                  
                  <script>
                    function validateForm() {
                      const name = document.getElementById('name').value.trim();
                      const currentPassword = document.getElementById('currentPassword').value;
                      const newPassword = document.getElementById('newPassword').value;
                      const confirmPassword = document.getElementById('confirmPassword').value;
                      
                      if (name.length < 2) {
                        alert('Name must be at least 2 characters long');
                        return false;
                      }
                      
                      if (currentPassword && !newPassword) {
                        alert('New password is required when current password is provided');
                        return false;
                      }
                      
                      if (newPassword && !currentPassword) {
                        alert('Current password is required to change password');
                        return false;
                      }
                      
                      if (newPassword && newPassword !== confirmPassword) {
                        alert('New passwords do not match');
                        return false;
                      }
                      
                      if (newPassword && newPassword.length < 8) {
                        alert('New password must be at least 8 characters long');
                        return false;
                      }
                      
                      // Disable submit button to prevent double submission
                      document.getElementById('submitBtn').disabled = true;
                      document.getElementById('submitBtn').textContent = 'Saving...';
                      
                      return true;
                    }
                  </script>
                </body></html>
                """;
    }

    private String generateErrorPage(String message) {
        String safeMessage = escapeHtml(message != null ? message : "An unknown error occurred");

        return """
                <!doctype html><html lang="en"><head>
                <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
                <title>Error · EDAP</title>
                <style>
                  body{margin:0;background:#0e1117;color:#e8eaf0;font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
                  .wrap{max-width:720px;margin:40px auto;padding:0 16px}
                  .card{background:#111217;border:1px solid #1f2330;border-radius:16px;padding:30px;text-align:center}
                  a.btn{display:inline-block;margin-top:20px;padding:12px 20px;border-radius:12px;border:1px solid #1f2330;color:#e8eaf0;text-decoration:none;background:#111217;transition:all 0.2s}
                  a.btn:hover{background:#1a1f2b;transform:translateY(-1px)}
                  .error-icon{font-size:48px;margin-bottom:20px}
                  h2{color:#f87171;margin:0 0 15px}
                  .muted{color:#99a1b3;margin:15px 0}
                </style></head><body>
                <div class="wrap">
                  <div class="card">
                    <div class="error-icon">⚠️</div>
                    <h2>Error</h2>
                    <p class="muted">""" + safeMessage + """</p>
                    <div>
                      <a class="btn" href="/home">Back to Home</a>
                      <a class="btn" href="/auth/login">Login</a>
                      <a class="btn" href="/profile">My Profile</a>
                    </div>
                  </div>
                </div>
                </body></html>
                """;
    }
}