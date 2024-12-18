package com.screenvault.screenvaultAPI.moderation;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class ModerationService {

    private final ModerationRepo moderationRepo;

    public ModerationService(ModerationRepo moderationRepo) {
        this.moderationRepo = moderationRepo;
    }

    public boolean isImageFlagged(MultipartFile image) throws InternalError, IllegalArgumentException {
        return moderationRepo.getImageModerationResponse(MultipartFile2Base64(image)).results().getFirst().flagged();
    }

    private String MultipartFile2Base64(MultipartFile image) throws InternalError {
        try {
            return Base64.getEncoder().encodeToString(image.getBytes());
        }
        catch (IOException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }
}
