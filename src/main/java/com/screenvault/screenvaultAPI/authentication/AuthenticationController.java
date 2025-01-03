package com.screenvault.screenvaultAPI.authentication;

import com.screenvault.screenvaultAPI.jwt.JwtType;
import com.screenvault.screenvaultAPI.user.User;
import com.screenvault.screenvaultAPI.user.UserUserView;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/authentication")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/noAuth/register")
    public ResponseEntity<AuthenticationResponseBody> register(
            @RequestBody User request
    ) {
        User user = null;
        try {
            user = authenticationService.register(request);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthenticationResponseBody(e.getMessage(), false));
        }
        return ResponseEntity.ok(
                new AuthenticationResponseBody("Successfully registered an account.", true)
        );
    }

    @PostMapping("/noAuth/login")
    public ResponseEntity<AuthenticationResponseBody> login(
            @RequestHeader("Authorization") String basicAuthorizationHeader,
            HttpServletResponse response
    ) {
        TokensDTO data = null;
        try {
            data = authenticationService.login(basicAuthorizationHeader);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.valueOf(401)).body(
                    new AuthenticationResponseBody(e.getMessage(), false)
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new AuthenticationResponseBody(e.getMessage(), false));
        }

        Cookie tokenCookie = new Cookie(JwtType.ACCESS_TOKEN.name(), data.token());
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setSecure(true);
        tokenCookie.setAttribute("SameSite", "None");

        Cookie refreshCookie = new Cookie(JwtType.REFRESH_TOKEN.name(), data.refreshToken());
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/authentication/noAuth/refreshToken");
        refreshCookie.setSecure(true);
        refreshCookie.setAttribute("SameSite", "None");

        response.addCookie(tokenCookie);
        response.addCookie(refreshCookie);

        return ResponseEntity.ok(new AuthenticationResponseBody("Successfully authenticated.", true));
    }

    @GetMapping("/noAuth/refreshToken")
    public ResponseEntity<AuthenticationResponseBody> refreshToken(
            // JwtType.REFRESH_TOKEN.name() <-- annotation can't use variable value
            @CookieValue("REFRESH_TOKEN") String refreshToken,
            HttpServletResponse response
    ) {
        String newToken = null;
        try {
            newToken = authenticationService.refreshToken(refreshToken);
        }
        catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new AuthenticationResponseBody("Invalid refresh token.", false)
            );
        }

        Cookie tokenCookie = new Cookie(JwtType.ACCESS_TOKEN.name(), newToken);
        tokenCookie.setPath("/");
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true);
        tokenCookie.setAttribute("SameSite", "None");

        response.addCookie(tokenCookie);

        return ResponseEntity.ok(
                new AuthenticationResponseBody("Successfully refreshed the token.", true)
        );
    }

    @GetMapping("/noAuth/whoAmI")
    public ResponseEntity<IdentityResponseBody> confirmIdentity(
            // JwtType.ACCESS_TOKEN.name() <-- annotation can't use variable value
            @CookieValue(name = "ACCESS_TOKEN", defaultValue = "") String token
    ) {
        return ResponseEntity.ok(
                new IdentityResponseBody(
                        "Successfully confirmed identity.",
                        true,
                        new UserUserView(authenticationService.getMyUser(token))
                )
        );
    }

    @DeleteMapping("/logout")
    public ResponseEntity<AuthenticationResponseBody> logout(
            HttpServletResponse response,
            Principal principal
    ) {
        try {
            authenticationService.logout(principal.getName(), response);
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new AuthenticationResponseBody(e.getMessage(), true)
            );
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.badRequest().body(
                    new AuthenticationResponseBody(e.getMessage(), true)
            );
        }

        return ResponseEntity.ok(new AuthenticationResponseBody("Successfully log out.", true));
    }
}
