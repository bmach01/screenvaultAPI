package com.screenvault.screenvaultAPI.authentication;


import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.user.User;
import com.screenvault.screenvaultAPI.user.UserRepository;
import com.screenvault.screenvaultAPI.user.UserRole;
import com.screenvault.screenvaultAPI.user.UserStatus;
import org.springframework.security.authentication.BadCredentialsException;
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

    private boolean isBlankOrNull(String str) {
        return str == null || str.isBlank();
    }

    public User register(User request) throws IllegalArgumentException {
        if (isBlankOrNull(request.getLogin()) ||
                isBlankOrNull(request.getUsername()) ||
                isBlankOrNull(request.getPassword())
        ) throw new IllegalArgumentException("Credentials and username may not be blank or null.");

        User user = new User(
                request.getUsername(),
                request.getLogin(),
                passwordEncoder.encode(request.getPassword()),
                UserRole.USER,
                UserStatus.ACTIVE
        );

        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new IllegalArgumentException("Username taken.");

        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new IllegalArgumentException("Email has been used already.");

        userRepository.save(user);

        return user;
    }

    public TokensDTO login(String basicAuthorizationHeader) throws BadCredentialsException, IllegalArgumentException {
        if (!basicAuthorizationHeader.startsWith(BASIC_PREFIX))
            throw new IllegalArgumentException("Invalid Authorization header format.");

        String credentials = basicAuthorizationHeader.substring(BASIC_PREFIX.length());
        String[] split = new String(Base64.getDecoder().decode(credentials)).split(":");

        if (split.length != 2)
            throw new IllegalArgumentException("Invalid Authorization header format, credentials should be split by ':'.");

        User user = userRepository.findByLogin(split[0]).orElseThrow(() -> new BadCredentialsException("Invalid credentials."));
        if (!passwordEncoder.matches(split[1], user.getPassword())) throw new BadCredentialsException("Invalid credentials.");

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new TokensDTO(token, refreshToken);
    }

    public String refreshToken(String refreshToken) throws BadCredentialsException {
        User user = userRepository.findByUsername(jwtService.extractUsername(refreshToken))
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token."));

        if (!jwtService.isValidRefreshToken(refreshToken, user))
            throw new BadCredentialsException("Refresh token may have expired.");

        return jwtService.generateToken(user);
    }
}
