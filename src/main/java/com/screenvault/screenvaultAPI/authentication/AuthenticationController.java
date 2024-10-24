package com.screenvault.screenvaultAPI.authentication;

import com.screenvault.screenvaultAPI.user.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    @PostMapping("/register")
    public ResponseEntity<RegisterResponseBody> register(
            @RequestBody User request
    ) {
        return null;
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
