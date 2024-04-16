package com.chatty.service.notification;

import com.chatty.constants.Authority;
import com.chatty.dto.notification.receive.request.NotificationReceiveUpdateRequest;
import com.chatty.dto.notification.receive.response.NotificationReceiveResponse;
import com.chatty.entity.notification.NotificationReceive;
import com.chatty.entity.user.Coordinate;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.User;
import com.chatty.repository.notification.NotificationReceiveRepository;
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
class NotificationReceiveServiceTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationReceiveService notificationReceiveService;

    @Autowired
    private NotificationReceiveRepository notificationReceiveRepository;

    @AfterEach
    void tearDown() {
        notificationReceiveRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("로그인한 유저의 알람 수신 목록을 조회한다. 처음 가입하면 기본 값은 true다.")
    @Test
    void getNotificationReceive() {
        // given
        User user = createUser("박지성", "01012345678");
        userRepository.save(user);

        NotificationReceive notificationReceive = createNotificationReceive(user);
        notificationReceiveRepository.save(notificationReceive);

        // when
        NotificationReceiveResponse notificationReceiveResponse =
                notificationReceiveService.getNotificationReceive(user.getMobileNumber());

        // then
        assertThat(notificationReceiveResponse.getNotificationReceiveId()).isNotNull();
        assertThat(notificationReceiveResponse)
                .extracting("userId", "marketingNotification", "chattingNotification", "feedNotification")
                .containsExactlyInAnyOrder(user.getId(), true, true, true);
    }

    @DisplayName("마케팅 알림 수신 상태(허용/비허용)를 변경한다.")
    @Test
    void updateMarketingNotificationReceive() {
        // given
        User user = createUser("박지성", "01012345678");
        userRepository.save(user);

        NotificationReceive notificationReceive = createNotificationReceive(user);
        notificationReceiveRepository.save(notificationReceive);

        NotificationReceiveUpdateRequest request = NotificationReceiveUpdateRequest.builder()
                .agree(false)
                .build();

        // when
        NotificationReceiveResponse notificationReceiveResponse =
                notificationReceiveService.updateMarketingNotification(user.getMobileNumber(), request);

        // then
        assertThat(notificationReceiveResponse.getNotificationReceiveId()).isNotNull();
        assertThat(notificationReceiveResponse)
                .extracting("userId", "marketingNotification", "chattingNotification", "feedNotification")
                .containsExactlyInAnyOrder(user.getId(), false, true, true);
    }

    @DisplayName("채팅 알림 수신 상태(허용/비허용)를 변경한다.")
    @Test
    void updateChattingNotificationReceive() {
        // given
        User user = createUser("박지성", "01012345678");
        userRepository.save(user);

        NotificationReceive notificationReceive = createNotificationReceive(user);
        notificationReceiveRepository.save(notificationReceive);

        NotificationReceiveUpdateRequest request = NotificationReceiveUpdateRequest.builder()
                .agree(false)
                .build();

        // when
        NotificationReceiveResponse notificationReceiveResponse =
                notificationReceiveService.updateChattingNotification(user.getMobileNumber(), request);

        // then
        assertThat(notificationReceiveResponse.getNotificationReceiveId()).isNotNull();
        assertThat(notificationReceiveResponse)
                .extracting("userId", "marketingNotification", "chattingNotification", "feedNotification")
                .containsExactlyInAnyOrder(user.getId(), true, false, true);
    }

    @DisplayName("피드 알림 수신 상태(허용/비허용)를 변경한다.")
    @Test
    void updateFeedNotificationReceive() {
        // given
        User user = createUser("박지성", "01012345678");
        userRepository.save(user);

        NotificationReceive notificationReceive = createNotificationReceive(user);
        notificationReceiveRepository.save(notificationReceive);

        NotificationReceiveUpdateRequest request = NotificationReceiveUpdateRequest.builder()
                .agree(false)
                .build();

        // when
        NotificationReceiveResponse notificationReceiveResponse =
                notificationReceiveService.updateMarketingNotification(user.getMobileNumber(), request);

        // then
        assertThat(notificationReceiveResponse.getNotificationReceiveId()).isNotNull();
        assertThat(notificationReceiveResponse)
                .extracting("userId", "marketingNotification", "chattingNotification", "feedNotification")
                .containsExactlyInAnyOrder(user.getId(), true, true, false);
    }

    private NotificationReceive createNotificationReceive(final User user) {
        return NotificationReceive.builder()
                .feedNotification(true)
                .marketingNotification(true)
                .chattingNotification(true)
                .user(user)
                .build();
    }

    private User createUser(final String nickname, final String mobileNumber) {
        return User.builder()
                .mobileNumber(mobileNumber)
                .deviceId("123456")
                .authority(Authority.USER)
//                .mbti(Mbti.ENFJ)
                .birth(LocalDate.now())
                .imageUrl("이미지")
                .address("주소")
                .gender(Gender.MALE)
                .nickname(nickname)
                .location(User.createPoint(
                        Coordinate.builder()
                                .lat(37.1)
                                .lng(127.1)
                                .build()
                ))
                .build();
    }
}