package com.screenvault.screenvaultAPI.report;

import java.util.UUID;

public record ReportRequestBody(
        UUID reportedObjectId
) {
}
