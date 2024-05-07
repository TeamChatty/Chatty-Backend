package com.chatty.controller.report;

import com.chatty.dto.ApiResponse;
import com.chatty.dto.report.request.ReportCreateRequest;
import com.chatty.dto.report.response.ReportResponse;
import com.chatty.service.report.ReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/v1/report/{userId}")
    public ApiResponse<ReportResponse> createReport(@PathVariable Long userId,
                                                    @Valid @RequestBody ReportCreateRequest request,
                                                    Authentication authentication) {
        return ApiResponse.ok(reportService.createReport(userId, authentication.getName(), request));
    }
}
