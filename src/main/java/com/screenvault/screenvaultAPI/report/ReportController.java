package com.screenvault.screenvaultAPI.report;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/reportPost")
    public ResponseEntity<ReportResponseBody> reportPost(
            @RequestBody ReportRequestBody requestBody,
            Principal principal
    ) {
        Report savedReport = null;
        try {
            savedReport = reportService.reportPost(principal.getName(), requestBody.reportedObjectId());
        }
        catch (NoSuchElementException e) {
            return ResponseEntity.notFound().build();
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new ReportResponseBody(e.getMessage(), false, null)
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ReportResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new ReportResponseBody("Successfully reported post.", true, savedReport)
        );
    }

    @PostMapping("/reportComment")
    public ResponseEntity<ReportResponseBody> reportComment(
            @RequestBody ReportRequestBody requestBody,
            Principal principal
    ) {
        Report savedReport = null;
        try {
            savedReport = reportService.reportComment(principal.getName(), requestBody.reportedObjectId());
        }
        catch (NoSuchElementException e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
        catch (InternalError e) {
            return ResponseEntity.internalServerError().body(
                    new ReportResponseBody(e.getMessage(), false, null)
            );
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    new ReportResponseBody(e.getMessage(), false, null)
            );
        }

        return ResponseEntity.ok(
                new ReportResponseBody("Successfully reported comment.", true, savedReport)
        );
    }
}
