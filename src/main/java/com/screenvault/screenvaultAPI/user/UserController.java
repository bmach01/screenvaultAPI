package com.screenvault.screenvaultAPI.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PatchMapping("/changePassword")
    public ResponseEntity<UserResponseBody> changePassword(
            @RequestBody ChangePasswordRequestBody requestBody,
            Principal principal
    ) {
        try {
            userService.changePassword(principal.getName(), requestBody.oldPassword(), requestBody.newPassword());
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new UserResponseBody(e.getMessage(), false)
            );
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                    new UserResponseBody(e.getMessage(), false)
            );
        }
        catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new UserResponseBody(e.getMessage(), false)
            );
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new UserResponseBody(e.getMessage(), false)
            );
        }

        return ResponseEntity.ok(new UserResponseBody("Successfully changed password.", true));
    }

    @PutMapping("/changePfp")
    public ResponseEntity<UserResponseBody> uploadPfp(
            @RequestParam MultipartFile newImage,
            Principal principal
    ) {
        try {
            userService.changeProfilePicture(principal.getName(), newImage);
        }
        catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new UserResponseBody(e.getMessage(), false)
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new UserResponseBody(e.getMessage(), false)
            );
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new UserResponseBody(e.getMessage(), false)
            );
        }

        return ResponseEntity.ok(new UserResponseBody("Successfully changed profile picture.", true));
    }

    @DeleteMapping("/deletePfp")
    public ResponseEntity<UserResponseBody> deletePfp(
            Principal principal
    ) {
        try {
            userService.deleteProfilePicture(principal.getName());
        }
        catch (AccessDeniedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new UserResponseBody(e.getMessage(), false)
            );
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new UserResponseBody(e.getMessage(), false)
            );
        }

        return ResponseEntity.ok(new UserResponseBody("Successfully changed profile picture.", true));
    }

}
