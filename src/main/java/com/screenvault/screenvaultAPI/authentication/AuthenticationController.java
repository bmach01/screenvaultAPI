package com.screenvault.screenvaultAPI.authentication;

import com.screenvault.screenvaultAPI.jwt.JwtType;
import com.screenvault.screenvaultAPI.user.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseBody> register(
            @RequestBody User request
    ) {
        User user = null;
        try {
            user = authenticationService.register(request);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new LoginResponseBody(e.getMessage(), false));
        }
        return ResponseEntity.ok(new LoginResponseBody("Account successfully registered.", true));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseBody> login(
            @RequestHeader("Authorization") String basicAuthorizationHeader,
            HttpServletResponse response
    ) {
        TokensDTO data = null;
        try {
            data = authenticationService.login(basicAuthorizationHeader);
        } catch (BadCredentialsException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new LoginResponseBody(e.getMessage(), false)
            );
        }

        Cookie tokenCookie = new Cookie(JwtType.ACCESS_TOKEN.name(), data.token());
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");

        Cookie refreshCookie = new Cookie(JwtType.REFRESH_TOKEN.name(), data.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/authentication/refreshToken");

        response.addCookie(tokenCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(new LoginResponseBody("Successfully authenticated.", true));
    }

    @GetMapping("/refreshToken")
    public ResponseEntity<LoginResponseBody> refreshToken(
            // JwtType.REFRESH_TOKEN.name() <-- annotation can't use variable value
            @CookieValue("REFRESH_TOKEN") String refreshToken,
            HttpServletResponse response
    ) {
        String newToken = null;
        try {
            newToken = authenticationService.refreshToken(refreshToken);
        } catch (BadCredentialsException e) {
            return ResponseEntity.notFound().build();
        }

        Cookie tokenCookie = new Cookie(JwtType.ACCESS_TOKEN.name(), newToken);
        tokenCookie.setPath("/");
        tokenCookie.setHttpOnly(true);
        response.addCookie(tokenCookie);

        return ResponseEntity.ok(new LoginResponseBody("Successfully refreshed token.", true));
    }
}
