package com.screenvault.screenvaultAPI.authentication;

import com.screenvault.screenvaultAPI.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseBody> register(
            @RequestBody User request
    ) {
        RegisterResponseBody responseBody = authenticationService.register(request);

        // Username taken, email already used
        if (responseBody.user() == null) return ResponseEntity.badRequest().body(responseBody);
        // Account created
        return ResponseEntity.ok(responseBody);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseBody> login(
            @RequestHeader("Authorization") String basicAuthorizationHeader
    ) {
        return null;
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<String> refreshToken(
            @RequestBody String token
    ) {
        return null;
    }
}
