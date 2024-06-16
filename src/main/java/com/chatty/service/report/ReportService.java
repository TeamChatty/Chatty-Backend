package com.chatty.service.report;

import com.chatty.constants.Code;
import com.chatty.dto.report.request.ReportCreateRequest;
import com.chatty.dto.report.response.ReportResponse;
import com.chatty.entity.block.Block;
import com.chatty.entity.report.Report;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.block.BlockRepository;
import com.chatty.repository.chat.ChatRoomRepository;
import com.chatty.repository.report.ReportRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.fcm.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final BlockRepository blockRepository;
    private final ChatRoomRepository chatRoomRepository;

    private final FcmService fcmService;

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

        if (!isBlock(reporter, reported)) {
            Block block = Block.create(reporter, reported);
            blockRepository.save(block);

            chatRoomRepository.findChatRoomBySenderAndReceiver(reporter, reported)
                    .ifPresent(chatRoomRepository::delete);

            chatRoomRepository.findChatRoomBySenderAndReceiver(reported, reporter)
                    .ifPresent(chatRoomRepository::delete);
        }

        fcmService.sendReportNotification(request.getContent(), reported.getId(), "01012345678");
        return ReportResponse.of(report);
    }

    private boolean isBlock(final User reporter, final User reported) {
        return blockRepository.existsByBlockerIdAndBlockedId(reporter.getId(), reported.getId());
    }
}
