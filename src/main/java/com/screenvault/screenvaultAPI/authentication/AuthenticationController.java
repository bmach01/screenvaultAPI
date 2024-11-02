package com.screenvault.screenvaultAPI.authentication;

import com.screenvault.screenvaultAPI.jwt.JwtType;
import com.screenvault.screenvaultAPI.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> login(
            @RequestHeader("Authorization") String basicAuthorizationHeader,
            HttpServletResponse response
    ) {
        TokensResponseDTO data = authenticationService.login(basicAuthorizationHeader);

        // Invalid credentials
        if (data.token() == null || data.refreshToken() == null) {
            return new ResponseEntity<>(data.message(), HttpStatus.UNAUTHORIZED);
        }

        Cookie tokenCookie = new Cookie(JwtType.TOKEN.name(), data.token());
        tokenCookie.setHttpOnly(true);

        Cookie refreshCookie = new Cookie(JwtType.REFRESH_TOKEN.name(), data.refreshToken());
        refreshCookie.setHttpOnly(true);

        response.addCookie(tokenCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(data.message());
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<String> refreshToken(
            // JwtType.REFRESH_TOKEN.name() <-- annotation can't use variable value
            @CookieValue("REFRESH_TOKEN") String refreshToken,
            HttpServletResponse response
    ) {
        TokensResponseDTO data = authenticationService.refreshToken(refreshToken);

        if (data.token() == null) return new ResponseEntity<>(data.message() , HttpStatus.UNAUTHORIZED);

        Cookie tokenCookie = new Cookie(JwtType.TOKEN.name(), data.token());
        tokenCookie.setHttpOnly(true);
        response.addCookie(tokenCookie);

        return  ResponseEntity.ok(data.message());
    }
}
