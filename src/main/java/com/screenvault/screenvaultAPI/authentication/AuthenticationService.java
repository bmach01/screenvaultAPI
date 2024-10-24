package com.screenvault.screenvaultAPI.authentication;


import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.user.User;
import com.screenvault.screenvaultAPI.user.UserRepository;
import com.screenvault.screenvaultAPI.user.UserRole;
import com.screenvault.screenvaultAPI.user.UserStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AuthenticationService {

    private final static String BASIC_PREFIX = "Basic ";

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            AuthenticationManager authenticationManager
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    public RegisterResponseBody register(User request) {

        User user = new User(
            null,
            request.getUsername(),
            request.getLogin(),
            passwordEncoder.encode(request.getPassword()),
            UserRole.USER,
            UserStatus.ACTIVE
        );

        userRepository.save(user);

        return new RegisterResponseBody();
    }

    public LoginResponseBody authenticate(String basicAuthorizationHeader) {
        String[] split = new String(Base64.getDecoder().decode(basicAuthorizationHeader))
                .substring(BASIC_PREFIX.length())
                .split(":");

        if (split.length != 2) throw new IllegalArgumentException("Invalid basic auth header");

        User request = new User(null, null, split[0], split[1], null, null);

        // If this throws then authentication failed and controller will send out 403
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
            )
        );

        User user = userRepository.findByUsername(request.getUsername());
        String token = jwtService.generateToken(user);

        return new LoginResponseBody();
    }
}
