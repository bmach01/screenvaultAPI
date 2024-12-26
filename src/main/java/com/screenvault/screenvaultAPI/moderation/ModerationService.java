package com.screenvault.screenvaultAPI.moderation;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class ModerationService {

    private static final String[] VALID_IMAGE_TYPES = { "image/jpeg", "image/png", "image/svg+xml", "image/webp" };

    private final ModerationRepository moderationRepository;

    public ModerationService(ModerationRepository moderationRepository) {
        this.moderationRepository = moderationRepository;
    }

    public boolean isImageFlagged(String imageB64) throws InternalError, IllegalArgumentException {
        return moderationRepository.getImageModerationResponse(imageB64).results().getFirst().flagged();
    }

    public String encodeImageToB64(MultipartFile image) throws InternalError, IllegalArgumentException {
        if (!isValidImageType(image.getContentType()))
            throw new IllegalArgumentException("Image type not supported.");

        try {
            return Base64.getEncoder().encodeToString(image.getBytes());
        }
        catch (IOException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    public Set<String> getImageTags(String imageB64) throws InternalError, IllegalArgumentException  {
        TagsResponseBody response = moderationRepository.getImageTags(imageB64);
        System.out.println(response.toString());

        if (response.choices().getFirst().message().refusal() != null)
            throw new InternalError("Internal error. Try again later.");

        if (response.choices().getFirst().message().content().equals("I'm sorry, but I can't assist with that."))
            return new HashSet<>(List.of("EXPLICIT"));

        Set<String> tags = new HashSet<String>();
        Collections.addAll(tags, response.choices().getFirst().message().content().split(";"));

        return tags;
    }

    private boolean isValidImageType(String type) {
        for (String valid : VALID_IMAGE_TYPES) if (type.equals(valid)) return true;
        return false;
    }
}
