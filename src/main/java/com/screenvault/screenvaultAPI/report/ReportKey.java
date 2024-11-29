package com.screenvault.screenvaultAPI.report;

import java.util.UUID;

public record ReportKey(
        String username,
        UUID reportedObjectId
) {
}
