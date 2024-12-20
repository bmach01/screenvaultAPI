package com.screenvault.screenvaultAPI.moderation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.DataBindingException;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class VerificationRepoImpl implements VerificationRepo {

    private static final String API_KEY = System.getenv("SCREENVAULT_OPENAI_API_KEY");
    private static final String MODERATION_API_URL = System.getenv("SCREENVAULT_MODERATION_API_URL");
    private static final String TAGS_API_URL = System.getenv("SCREENVAULT_TAGS_API_URL");
    private static final String MODERATION_MODEL = "omni-moderation-latest";
    private static final String TAGGING_MODEL = "gpt-4o-mini";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public VerificationRepoImpl(OkHttpClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public ModerationResponseBody getImageModerationResponse(String imageB64)
            throws InternalError, IllegalArgumentException
    {
        try {
            ModerationRequestBody requestBody = new ModerationRequestBody(
                    MODERATION_MODEL,
                    List.of(
                            new ModerationRequestBody.Input(
                            "image_url", null, new ModerationRequestBody.Input.ImageUrl(
                                "data:image/jpeg;base64," + imageB64
                            ))
                    )
            );
            String requestJson = objectMapper.writeValueAsString(requestBody);

            Request request = new Request.Builder()
                    .url(MODERATION_API_URL)
                    .post(RequestBody.create(requestJson, MediaType.parse("application/json")))
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new InternalError("Invalid response from server.");
                }
                return objectMapper.readValue(response.body().string(), ModerationResponseBody.class);
            }
            catch (DataBindingException | OutOfMemoryError | IOException e) {
                throw new InternalError("Internal error. Try again later.");
            }

        }
        catch (JsonProcessingException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }

    @Override
    public TagsResponseBody getImageTags(String imageB64) throws InternalError, IllegalArgumentException {
        try {
            TaggingRequestBody requestBody = new TaggingRequestBody(TAGGING_MODEL, 85,
                List.of(
                    new TaggingRequestBody.Message(
                        "developer",
                        List.of(
                            new ModerationRequestBody.Input(
                                "text",
                                "Respond with an array of tags where each should be a preferably one word or firstWord-secondWord tag that describes the image. Make them as specific as possible. Format it like: #tag1;#tag2;#tag3",
                                null
                            )
                        )
                    ),
                    new TaggingRequestBody.Message(
                        "user",
                        List.of(
                            new ModerationRequestBody.Input(
                                "text",
                                "Give me 5 tags that describe this image.",
                                null
                            ),
                            new ModerationRequestBody.Input(
                                "image_url",
                                null,
                                new ModerationRequestBody.Input.ImageUrl(
                                    "data:image/jpeg;base64," + imageB64
                                )
                            )
                        )
                    )
                )
            );

            String requestJson = objectMapper.writeValueAsString(requestBody);

            Request request = new Request.Builder()
                    .url(TAGS_API_URL)
                    .post(RequestBody.create(requestJson, MediaType.parse("application/json")))
                    .addHeader("Authorization", "Bearer " + API_KEY)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful() || response.body() == null) {
                    throw new InternalError("Invalid response from server.");
                }

                return objectMapper.readValue(response.body().string(), TagsResponseBody.class);
            }
            catch (DataBindingException | OutOfMemoryError | IOException e) {
                throw new InternalError("Internal error. Try again later.");
            }
        }
        catch (JsonProcessingException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }
}
