package com.screenvault.screenvaultAPI.moderation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/verification")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/noAuth/verifyAndGetTags")
    public ResponseEntity<VerificationAndTagsResponseBody> verifyAndGetTags(
            @RequestParam MultipartFile image
    ) {
        String imageB64 = verificationService.encodeImageToB64(image);
        try {
            boolean flagged = verificationService.isImageFlagged(imageB64);

            if (!flagged) {
                Set<String> tags = verificationService.getImageTags(imageB64);

                if (!tags.contains("EXPLICIT"))
                    return ResponseEntity.ok(new VerificationAndTagsResponseBody(
                            "Successfully checked image for illegal content and generated tags.",
                            true,
                            false,
                            tags
                    ));
            }
            return ResponseEntity.ok(new VerificationAndTagsResponseBody(
                    "Successfully checked image for illegal content.",
                    true,
                    true,
                    null
            ));

        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new VerificationAndTagsResponseBody(e.getMessage(), false, null, null)
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new VerificationAndTagsResponseBody(e.getMessage(), false, null, null)
            );
        }
    }

}
