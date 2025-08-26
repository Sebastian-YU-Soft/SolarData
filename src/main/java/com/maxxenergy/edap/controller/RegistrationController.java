package com.maxxenergy.edap.controller;

import com.maxxenergy.edap.model.User;
import com.maxxenergy.edap.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<String> showRegistrationForm() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(generateRegistrationForm(null, null, null, null));
    }

    @PostMapping
    public ResponseEntity<String> processRegistration(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {

        try {
            User user = userService.registerUser(name, email, password);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateSuccessPage(user.getName(), user.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.TEXT_HTML)
                    .body(generateRegistrationForm(name, email, "", e.getMessage()));
        }
    }

    @PostMapping("/api")
    public ResponseEntity<Map<String, Object>> processRegistrationApi(
            @RequestBody Map<String, String> request) {

        Map<String, Object> response = new HashMap<>();

        try {
            String name = request.get("name");
            String email = request.get("email");
            String password = request.get("password");

            User user = userService.registerUser(name, email, password);

            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("userId", user.getId());

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

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
            label{display:block;margin:12px 0 6px}
            input{width:100%;padding:12px 12px;border-radius:12px;border:1px solid var(--line);background:#0d1017;color:var(--ink)}
            .actions{margin-top:16px;display:flex;gap:10px}
            .btn{padding:12px 16px;border-radius:14px;border:1px solid var(--line);text-decoration:none;color:var(--ink);cursor:pointer;background:var(--card)}
            .btn.primary{background:linear-gradient(180deg,var(--brand),var(--brand2));border:0;color:white}
            .muted{color:var(--muted)}
            .error{background:#2a0f12;border:1px solid #522;color:#f8caca;padding:10px;border-radius:12px;margin:10px 0}
          </style>
        </head><body>
          <div class="wrap">
            <div class="card">
              <h1>Create your account</h1>
              <p class="muted">Access member-only features with a free account.</p>
              {{ERROR}}
              <form method="POST" action="/register">
                <label for="name">Full name</label>
                <input id="name" name="name" value="{{NAME}}" required />

                <label for="email">Email</label>
                <input id="email" type="email" name="email" value="{{EMAIL}}" required />

                <label for="password">Password</label>
                <input id="password" type="password" name="password" value="{{PASSWORD}}" minlength="8" required />

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

    private String generateSuccessPage(String name, String email) {
        String html = """
        <!doctype html><html lang="en"><head>
        <meta charset="utf-8"/><meta name="viewport" content="width=device-width, initial-scale=1"/>
        <title>Registration complete</title>
        <style>
          body{margin:0;background:#0e1117;color:#e8eaf0;font:15px/1.55 system-ui,Segoe UI,Roboto,Helvetica,Arial,sans-serif}
          .wrap{max-width:720px;margin:40px auto;padding:0 16px}
          .card{background:#111217;border:1px solid #1f2330;border-radius:16px;padding:22px}
          a.btn{display:inline-block;margin-top:12px;padding:10px 14px;border-radius:12px;border:1px solid #1f2330;color:#e8eaf0;text-decoration:none}
          a.btn:hover{background:#1a1f2b}
        </style></head><body>
        <div class="wrap">
          <div class="card">
            <h2>You're registered</h2>
            <p>Thanks, {{NAME}}. Your account <strong>{{EMAIL}}</strong> has been created.</p>
            <p>Next step: sign in to access member-only features.</p>
            <a class="btn" href="/home">Back to Home</a>
          </div>
        </div>
        </body></html>
        """;

        html = html.replace("{{NAME}}", escapeHtml(name));
        html = html.replace("{{EMAIL}}", escapeHtml(email));
        return html;
    }

    private String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}