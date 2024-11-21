package com.screenvault.screenvaultAPI.shortKey;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

@Service
public class ShortKeyService {

    private static final int KEY_LENGTH = 8;
    private static final char[] VALID_CHARS = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789".toCharArray();
    private final ShortKeyRepository shortKeyRepository;
    private final Random random = new Random();

    public ShortKeyService(ShortKeyRepository shortKeyRepository) {
        this.shortKeyRepository = shortKeyRepository;
    }

    public ShortKey generateNewShortKey(UUID postId) {
        ShortKey newShortKey = new ShortKey(postId, guaranteeUniqueness(), new Date());
        shortKeyRepository.save(newShortKey);

        return newShortKey;
    }

    private String guaranteeUniqueness() {
        String key;
        do {
            key = getRandomKey();
        }
        while (shortKeyRepository.existsByKey(key));

        return key;
    }

    private String getRandomKey() {
        char[] chars = new char[KEY_LENGTH];
        for (int i = 0; i < KEY_LENGTH; ++i) {
            chars[i] = VALID_CHARS[random.nextInt(0, VALID_CHARS.length)];
        }

        return new String(chars);
    }

}
