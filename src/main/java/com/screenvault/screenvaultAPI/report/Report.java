package com.screenvault.screenvaultAPI.report;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("report")
public class Report {
    @Id
    private ReportKey reportKey;

    public Report() {
    }

    public Report(ReportKey reportKey) {
        this.reportKey = reportKey;
    }

    public ReportKey getReportKey() {
        return reportKey;
    }

    public void setReportKey(ReportKey reportKey) {
        this.reportKey = reportKey;
    }
}
