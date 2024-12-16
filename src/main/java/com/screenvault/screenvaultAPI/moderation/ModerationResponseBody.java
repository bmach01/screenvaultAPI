package com.screenvault.screenvaultAPI.moderation;

import java.util.List;
import java.util.Map;

public record ModerationResponseBody(
        String id,
        String model,
        List<Result> results
) {
    public record Result(
            boolean flagged,
            Categories categories,
            Map<String, Double> category_scores,
            Map<String, List<String>> category_applied_input_types
    ) {
        public record Categories(
                boolean sexual,
                boolean sexual_minors,
                boolean harassment,
                boolean harassment_threatening,
                boolean hate,
                boolean hate_threatening,
                boolean illicit,
                boolean illicit_violent,
                boolean self_harm,
                boolean self_harm_intent,
                boolean self_harm_instructions,
                boolean violence,
                boolean violence_graphic
        ) {}
    }
}
