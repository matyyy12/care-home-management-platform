package com.bd2.seniorhomeapp.report.controller;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bd2.seniorhomeapp.report.service.SeniorHomeSummaryPdfService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/reports/senior-home-summary")
@RequiredArgsConstructor
public class SeniorHomeSummaryPdfController {

    private final SeniorHomeSummaryPdfService seniorHomeSummaryPdfService;

    @GetMapping("/{homeId}/pdf")
    public ResponseEntity<byte[]> downloadPdf(@PathVariable Long homeId) {
        byte[] pdf = seniorHomeSummaryPdfService.generatePdf(homeId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("senior-home-summary-" + homeId + ".pdf")
                        .build()
                        .toString())
                .body(pdf);
    }
}
