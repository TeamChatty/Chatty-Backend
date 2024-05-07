package com.chatty.dto.report.response;

import com.chatty.entity.report.Report;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReportResponse {

    private Long reportId;

    private Long reporterId;

    private Long reportedId;

    private String content;

    @Builder
    public ReportResponse(final Long reportId, final Long reporterId, final Long reportedId, final String content) {
        this.reportId = reportId;
        this.reporterId = reporterId;
        this.reportedId = reportedId;
        this.content = content;
    }

    public static ReportResponse of(final Report report) {
        return ReportResponse.builder()
                .reportId(report.getId())
                .reporterId(report.getReporter().getId())
                .reportedId(report.getReported().getId())
                .content(report.getContent())
                .build();
    }
}
