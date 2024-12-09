package com.screenvault.screenvaultAPI.user;

import com.screenvault.screenvaultAPI.image.ImageService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.util.NoSuchElementException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImageService imageService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ImageService imageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.imageService = imageService;
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

    public void changeProfilePicture(String username, MultipartFile newPicture)
        throws AccessDeniedException, IllegalArgumentException, InternalError
    {
        if (!userRepository.existsById(username))
            throw new AccessDeniedException("Acccess denied. Access not authorized.");

        imageService.deleteImage(username, true);
        imageService.uploadImage(username, newPicture, true);
    }
}
