package com.screenvault.screenvaultAPI.report;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface ReportRepository extends MongoRepository<Report, ReportKey> {
    void deleteByReportKeyReportedObjectId(UUID reportedObjectId);
}
