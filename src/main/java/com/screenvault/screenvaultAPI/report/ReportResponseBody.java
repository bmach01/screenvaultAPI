package com.screenvault.screenvaultAPI.report;

public record ReportResponseBody(
        String message,
        boolean success,
        Report report
) {
}
