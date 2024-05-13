package com.chatty.service.report;

import com.chatty.constants.Authority;
import com.chatty.dto.report.request.ReportCreateRequest;
import com.chatty.dto.report.response.ReportResponse;
import com.chatty.entity.chat.ChatRoom;
import com.chatty.entity.report.Report;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.block.BlockRepository;
import com.chatty.repository.chat.ChatRoomRepository;
import com.chatty.repository.report.ReportRepository;
import com.chatty.repository.user.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReportServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private BlockRepository blockRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @AfterEach
    void tearDown() {
        chatRoomRepository.deleteAllInBatch();
        blockRepository.deleteAllInBatch();
        reportRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("해당 유저를 신고한다.")
    @Test
    void createReport() {
        // given
        User reporter = createUser("박지성", "01012345678");
        User reported = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(reporter, reported));

        ReportCreateRequest request = ReportCreateRequest.builder()
                .content("신고 사유")
                .build();

        // when
        ReportResponse reportResponse =
                reportService.createReport(reported.getId(), reporter.getMobileNumber(), request);

        // then
        assertThat(reportResponse.getReportId()).isNotNull();
        assertThat(reportResponse)
                .extracting("reporterId", "reportedId", "content")
                .containsExactly(reporter.getId(), reported.getId(), "신고 사유");
    }

    @DisplayName("해당 유저를 신고할 때, 이미 신고한 유저면 예외가 발생한다.")
    @Test
    void createReportWithDuplicateReport() {
        // given
        User reporter = createUser("박지성", "01012345678");
        User reported = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(reporter, reported));

        Report report = createReport(reporter, reported, "신고 사유");
        reportRepository.save(report);

        ReportCreateRequest request = ReportCreateRequest.builder()
                .content("신고 사유")
                .build();

        // when // then
        assertThatThrownBy(() -> reportService.createReport(reported.getId(), reporter.getMobileNumber(), request))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 신고한 유저입니다.");
    }

    @DisplayName("유저를 신고할 때, 본인을 신고하면 예외가 발생한다.")
    @Test
    void createReportWithSelfReport() {
        // given
        User reporter = createUser("박지성", "01012345678");
        userRepository.save(reporter);

        ReportCreateRequest request = ReportCreateRequest.builder()
                .content("신고 사유")
                .build();

        // when // then
        assertThatThrownBy(() -> reportService.createReport(reporter.getId(), reporter.getMobileNumber(), request))
                .isInstanceOf(CustomException.class)
                .hasMessage("자기 자신을 신고할 수 없습니다.");
    }

    @DisplayName("유저를 신고할 때, 차단 유무를 확인 후 차단까지 진행한다.")
    @Test
    void createReportWithBlock() {
        // given
        User reporter = createUser("박지성", "01012345678");
        User reported = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(reporter, reported));

        ReportCreateRequest request = ReportCreateRequest.builder()
                .content("신고 사유")
                .build();

        // when
        ReportResponse reportResponse =
                reportService.createReport(reported.getId(), reporter.getMobileNumber(), request);

        // then
        assertThat(reportResponse.getReportId()).isNotNull();
        assertThat(reportResponse)
                .extracting("reporterId", "reportedId", "content")
                .containsExactly(reporter.getId(), reported.getId(), "신고 사유");

        boolean isBlock = blockRepository.existsByBlockerIdAndBlockedId(reporter.getId(), reported.getId());
        assertThat(isBlock).isTrue();
    }

    @DisplayName("유저를 신고할 때, 차단을 하고 존재하는 채팅방을 삭제한다.")
    @Test
    void createReportWithBlockAndChatRoomDelete() {
        // given
        User reporter = createUser("박지성", "01012345678");
        User reported = createUser("강혜원", "01011112222");
        userRepository.saveAll(List.of(reporter, reported));

        ReportCreateRequest request = ReportCreateRequest.builder()
                .content("신고 사유")
                .build();

        ChatRoom chatRoom = createChatRoom(reporter, reported);
        chatRoomRepository.save(chatRoom);

        // when
        ReportResponse reportResponse =
                reportService.createReport(reported.getId(), reporter.getMobileNumber(), request);

        // then
        assertThat(reportResponse.getReportId()).isNotNull();
        assertThat(reportResponse)
                .extracting("reporterId", "reportedId", "content")
                .containsExactly(reporter.getId(), reported.getId(), "신고 사유");

        boolean isBlock = blockRepository.existsByBlockerIdAndBlockedId(reporter.getId(), reported.getId());
        assertThat(isBlock).isTrue();

        List<ChatRoom> chatRooms = chatRoomRepository.findAll();
        assertThat(chatRooms).hasSize(0);
    }

    private Report createReport(User reporter, User reported, String content) {
        return Report.builder()
                .reporter(reporter)
                .reported(reported)
                .content(content)
                .build();
    }

    private User createUser(final String nickname, final String mobileNumber) {
        return User.builder()
                .mobileNumber(mobileNumber)
                .deviceId("123456")
                .authority(Authority.USER)
                .birth(LocalDate.now())
                .imageUrl("이미지")
                .address("주소")
                .gender(Gender.MALE)
                .nickname(nickname)
                .candy(10)
                .ticket(10)
                .build();
    }

    private ChatRoom createChatRoom(final User sender, final User receiver) {
        return ChatRoom.builder()
                .sender(sender)
                .receiver(receiver)
                .build();
    }
}