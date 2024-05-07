package com.chatty.dto.report.request;

import com.chatty.entity.report.Report;
import com.chatty.entity.user.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class ReportCreateRequest {

    @NotBlank(message = "신고 사유는 필수로 입력해야 됩니다.")
    private String content;

    @Builder
    public ReportCreateRequest(final String content) {
        this.content = content;
    }

    public Report toEntity(final User reporter, final User reported) {
        return Report.builder()
                .reporter(reporter)
                .reported(reported)
                .content(content)
                .build();
    }
}
