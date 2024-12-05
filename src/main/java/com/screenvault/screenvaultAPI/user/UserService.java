package com.screenvault.screenvaultAPI.user;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void changePassword(String username, String oldPassword, String newPassword)
        throws IllegalArgumentException, NoSuchElementException, InternalError, AccessDeniedException
    {
        if (newPassword == null || newPassword.isBlank())
            throw new IllegalArgumentException("Credentials and username may not be blank or null.");

        try {
            User user = userRepository.findByUsername(username).orElseThrow();

            if (!passwordEncoder.matches(oldPassword, user.getPassword()))
                throw new AccessDeniedException("Access denied (provided wrong existing password).");

            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
        catch (OptimisticLockingFailureException e) {
            throw new InternalError("Failed to change the password. Try again later");
        }
    }
}
