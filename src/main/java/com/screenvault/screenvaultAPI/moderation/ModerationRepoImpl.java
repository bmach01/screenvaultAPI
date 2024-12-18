package com.screenvault.screenvaultAPI.moderation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.xml.bind.DataBindingException;
import okhttp3.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
public class ModerationRepoImpl implements ModerationRepo {

    private static final String API_KEY = System.getenv("SCREENVAULT_OPENAI_API_KEY");
    private static final String API_URL = System.getenv("SCREENVAULT_OPENAI_API_URL");
    private static final String MODEL = "omni-moderation-latest";

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public ModerationRepoImpl(OkHttpClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    @Override
    public ModerationResponseBody getImageModerationResponse(String imageB64)
            throws InternalError, IllegalArgumentException
    {
        try {
            ModerationRequestBody requestBody = new ModerationRequestBody(
                    MODEL,
                    List.of(
                            new ModerationRequestBody.Input(
                            "image_url", null, new ModerationRequestBody.Input.ImageUrl(
                                "data:image/jpeg;base64," + imageB64
                            ))
                    )
            );
            String requestJson = objectMapper.writeValueAsString(requestBody);

            Request request = new Request.Builder()
                    .url(API_URL)
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

        } catch (JsonProcessingException e) {
            throw new InternalError("Internal error. Try again later.");
        }
    }
}
