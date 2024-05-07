package com.chatty.service.report;

import com.chatty.constants.Code;
import com.chatty.dto.report.request.ReportCreateRequest;
import com.chatty.dto.report.response.ReportResponse;
import com.chatty.entity.report.Report;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.report.ReportRepository;
import com.chatty.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReportResponse createReport(final Long userId, final String mobileNumber, ReportCreateRequest request) {
        User reporter = userRepository.getByMobileNumber(mobileNumber);

        if (reporter.getId().equals(userId)) {
            throw new CustomException(Code.NOT_SELF_REPORT);
        }

        User reported = userRepository.getById(userId);

        if (reportRepository.existsByReporterAndReported(reporter, reported)) {
            throw new CustomException(Code.ALREADY_REPORT_USER);
        }

        Report report = request.toEntity(reporter, reported);
        reportRepository.save(report);

        return ReportResponse.of(report);
    }
}
