package com.screenvault.screenvaultAPI.authentication;


import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.user.User;
import com.screenvault.screenvaultAPI.user.UserRepository;
import com.screenvault.screenvaultAPI.user.UserRole;
import com.screenvault.screenvaultAPI.user.UserStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;

@Service
public class AuthenticationService {

    private final static String BASIC_PREFIX = "Basic ";

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public RegisterResponseBody register(User request) {

        User user = new User(
            request.getUsername(),
            request.getLogin(),
            passwordEncoder.encode(request.getPassword()),
            UserRole.USER,
            UserStatus.ACTIVE
        );

        if (userRepository.findByUsername(request.getUsername()) != null)
            return new RegisterResponseBody("Username taken.", null);

        if (userRepository.findByLogin(request.getLogin()) != null)
            return new RegisterResponseBody("This email has an account bound to it.", null);

        userRepository.save(user);

        return new RegisterResponseBody("Account created successfully.", user);
    }

    public LoginResponseDTO login(String basicAuthorizationHeader) {
        if (!basicAuthorizationHeader.startsWith(BASIC_PREFIX))
            throw new IllegalArgumentException("Invalid Authorization header format.");

        String credentials = basicAuthorizationHeader.substring(BASIC_PREFIX.length());
        String[] split = new String(Base64.getDecoder().decode(credentials)).split(":");

        if (split.length != 2)
            throw new IllegalArgumentException("Invalid Authorization header format, credentials should be split by ':'.");

        User user = userRepository.findByLogin(split[0]);
        // Invalid credentials
        if (user == null)
            return new LoginResponseDTO("Invalid credentials.", null, null);

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new LoginResponseDTO("Successfully authenticated.", token, refreshToken);
    }
}
