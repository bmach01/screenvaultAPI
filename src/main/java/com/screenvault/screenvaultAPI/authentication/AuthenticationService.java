package com.screenvault.screenvaultAPI.authentication;


import com.screenvault.screenvaultAPI.jwt.JwtService;
import com.screenvault.screenvaultAPI.jwt.JwtType;
import com.screenvault.screenvaultAPI.user.User;
import com.screenvault.screenvaultAPI.user.UserRepository;
import com.screenvault.screenvaultAPI.user.UserRole;
import com.screenvault.screenvaultAPI.user.UserStatus;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.NoSuchElementException;

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

        if (userRepository.findByUsername(request.getUsername()).isPresent())
            throw new IllegalArgumentException("Username taken.");

        if (userRepository.findByLogin(request.getLogin()).isPresent())
            throw new IllegalArgumentException("Email has been used already.");

        User user = new User(
                request.getUsername(),
                request.getLogin(),
                passwordEncoder.encode(request.getPassword()),
                UserRole.USER,
                UserStatus.INACTIVE
        );

        userRepository.save(user);

        return user;
    }

    public TokensDTO login(String basicAuthorizationHeader)
            throws BadCredentialsException, IllegalArgumentException, InternalError
    {
        if (!basicAuthorizationHeader.startsWith(BASIC_PREFIX))
            throw new IllegalArgumentException("Invalid Authorization header format. Basic auth starts with 'Basic '.");

        String credentials = basicAuthorizationHeader.substring(BASIC_PREFIX.length());
        String[] split = new String(Base64.getDecoder().decode(credentials)).split(":");

        if (split.length != 2)
            throw new IllegalArgumentException("Invalid Authorization header format, credentials should be split by ':'.");

        User user = userRepository.findByLogin(split[0])
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials or account does not exist."));

        if (!passwordEncoder.matches(split[1], user.getPassword()))
            throw new BadCredentialsException("Invalid credentials or account does not exist.");

        String token = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        user.setStatus(UserStatus.ACTIVE);

        try {
            userRepository.save(user);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later.");
        }

        return new TokensDTO(token, refreshToken);
    }

    public void logout(String username, HttpServletResponse response)
            throws NoSuchElementException, InternalError
    {
        Cookie tokenCookie = new Cookie(JwtType.ACCESS_TOKEN.name(), null);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0);

        Cookie refreshCookie = new Cookie(JwtType.REFRESH_TOKEN.name(), null);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/authentication/noAuth/refreshToken");
        refreshCookie.setMaxAge(0);

        response.addCookie(tokenCookie);
        response.addCookie(refreshCookie);

        try {
            User user = userRepository.findByUsername(username).orElseThrow();
            user.setStatus(UserStatus.INACTIVE);
            userRepository.save(user);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Internal error. Try again later");
        }
    }

    public String refreshToken(String refreshToken) throws BadCredentialsException {
        User user = userRepository.findByUsername(jwtService.extractUsername(refreshToken))
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token."));

        if (!jwtService.isValidRefreshToken(refreshToken, user))
            throw new BadCredentialsException("Refresh token may have expired.");

        return jwtService.generateToken(user);
    }

    public User getMyUser(String token) {
        User user = null;
        try {
            if (token.isBlank()) throw new NoSuchElementException("Token is null.");
            user = userRepository.findByUsername(jwtService.extractUsername(token)).orElseThrow();
            user.setPassword(null);
            user.setStatus(null);
        }
        catch (NoSuchElementException e) {
            user = new User("anonymous", null, null, UserRole.ANONYMOUS, null);
        }

        return user;
    }
}
