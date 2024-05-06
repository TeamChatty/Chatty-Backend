package com.chatty.service.profileUnlock;

import com.chatty.constants.Authority;
import com.chatty.dto.notification.receive.response.NotificationReceiveResponse;
import com.chatty.dto.profileUnlock.request.ProfileUnlockRequest;
import com.chatty.dto.profileUnlock.response.ProfileUnlockResponse;
import com.chatty.dto.user.response.UserProfileResponse;
import com.chatty.entity.user.Gender;
import com.chatty.entity.user.ProfileUnlock;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.alarm.AlarmRepository;
import com.chatty.repository.profileUnlock.ProfileUnlockRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.notification.NotificationReceiveService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
class ProfileUnlockServiceTest {

    @Autowired
    private ProfileUnlockService profileUnlockService;

    @Autowired
    private ProfileUnlockRepository profileUnlockRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AlarmRepository alarmRepository;

    @MockBean
    private NotificationReceiveService notificationReceiveService;

    @AfterEach
    void tearDown() {
        alarmRepository.deleteAllInBatch();
        profileUnlockRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("캔디를 이용하여 프로필 잠금을 해제한다.")
    @Test
    void unlockProfile() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User unlocker = createUser("박지성", "01012345678", 7, 5);
        User unlockedUser = createUser("강혜원", "01011112222", 7, 11);
        userRepository.saveAll(List.of(unlocker, unlockedUser));

        ProfileUnlockRequest request = ProfileUnlockRequest.builder()
                .unlockMethod("candy")
                .build();

        // stubbing
        when(notificationReceiveService.getNotificationReceive(any(String.class)))
                .thenReturn(NotificationReceiveResponse.builder()
                        .userId(unlockedUser.getId())
                        .feedNotification(false)
                        .marketingNotification(false)
                        .chattingNotification(false)
                        .build());

        // when
        ProfileUnlockResponse profileUnlockResponse = profileUnlockService.unlockProfile(unlockedUser.getId(), unlocker.getMobileNumber(), request, now);

        // then
        assertThat(profileUnlockResponse.getId()).isNotNull();
        assertThat(profileUnlockResponse)
                .extracting("unlockerId", "unlockedUserId", "registeredDateTime")
                .containsExactlyInAnyOrder(unlocker.getId(), unlockedUser.getId(), now);
    }

    @DisplayName("캔디를 이용하여 프로필 잠금을 해제할 때, 보유한 캔디(7개미만)가 부족하면 예외가 발생한다.")
    @Test
    void unlockProfileWithNoCandy() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User unlocker = createUser("박지성", "01012345678", 6, 5);
        User unlockedUser = createUser("강혜원", "01011112222", 7, 11);
        userRepository.saveAll(List.of(unlocker, unlockedUser));

        ProfileUnlockRequest request = ProfileUnlockRequest.builder()
                .unlockMethod("candy")
                .build();

        // when // then
        assertThatThrownBy(() -> profileUnlockService.unlockProfile(unlockedUser.getId(), unlocker.getMobileNumber(), request, now))
                .isInstanceOf(CustomException.class)
                .hasMessage("캔디의 개수가 부족합니다.");
    }

    @DisplayName("캔디를 이용하여 프로필 잠금을 해제할 때, 이미 잠금을 해제한 유저면 예외가 발생한다.")
    @Test
    void unlockProfileWithDuplicateUnlock() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User unlocker = createUser("박지성", "01012345678", 6, 5);
        User unlockedUser = createUser("강혜원", "01011112222", 7, 11);
        userRepository.saveAll(List.of(unlocker, unlockedUser));

        ProfileUnlock profileUnlock = ProfileUnlock.builder()
                .registeredDateTime(now)
                .unlocker(unlocker)
                .unlockedUser(unlockedUser)
                .build();
        profileUnlockRepository.save(profileUnlock);

        ProfileUnlockRequest request = ProfileUnlockRequest.builder()
                .unlockMethod("candy")
                .build();

        // when // then
        assertThatThrownBy(() -> profileUnlockService.unlockProfile(unlockedUser.getId(), unlocker.getMobileNumber(), request, now))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 프로필 잠금을 해제했습니다.");
    }

    @DisplayName("티켓을 이용하여 프로필 잠금을 해제한다.")
    @Test
    void unlockProfileWithTicket() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User unlocker = createUser("박지성", "01012345678", 7, 5);
        User unlockedUser = createUser("강혜원", "01011112222", 7, 11);
        userRepository.saveAll(List.of(unlocker, unlockedUser));

        ProfileUnlockRequest request = ProfileUnlockRequest.builder()
                .unlockMethod("ticket")
                .build();

        // stubbing
        when(notificationReceiveService.getNotificationReceive(any(String.class)))
                .thenReturn(NotificationReceiveResponse.builder()
                        .userId(unlockedUser.getId())
                        .feedNotification(false)
                        .marketingNotification(false)
                        .chattingNotification(false)
                        .build());

        // when
        ProfileUnlockResponse profileUnlockResponse = profileUnlockService.unlockProfile(unlockedUser.getId(), unlocker.getMobileNumber(), request, now);

        // then
        assertThat(profileUnlockResponse.getId()).isNotNull();
        assertThat(profileUnlockResponse)
                .extracting("unlockerId", "unlockedUserId", "registeredDateTime")
                .containsExactlyInAnyOrder(unlocker.getId(), unlockedUser.getId(), now);
    }

    @DisplayName("티켓을 이용하여 프로필 잠금을 해제할 때, 보유한 티켓(1개미만)이 부족하면 예외가 발생한다.")
    @Test
    void unlockProfileWithNoTicket() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User unlocker = createUser("박지성", "01012345678", 6, 0);
        User unlockedUser = createUser("강혜원", "01011112222", 7, 11);
        userRepository.saveAll(List.of(unlocker, unlockedUser));

        ProfileUnlockRequest request = ProfileUnlockRequest.builder()
                .unlockMethod("ticket")
                .build();

        // when // then
        assertThatThrownBy(() -> profileUnlockService.unlockProfile(unlockedUser.getId(), unlocker.getMobileNumber(), request, now))
                .isInstanceOf(CustomException.class)
                .hasMessage("티켓의 개수가 부족합니다.");
    }

    @DisplayName("티켓을 이용하여 프로필 잠금을 해제할 때, 이미 잠금을 해제한 유저면 예외가 발생한다.")
    @Test
    void unlockProfileWithTicketAndDuplicateUnlock() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User unlocker = createUser("박지성", "01012345678", 6, 5);
        User unlockedUser = createUser("강혜원", "01011112222", 7, 11);
        userRepository.saveAll(List.of(unlocker, unlockedUser));

        ProfileUnlock profileUnlock = ProfileUnlock.builder()
                .registeredDateTime(now)
                .unlocker(unlocker)
                .unlockedUser(unlockedUser)
                .build();
        profileUnlockRepository.save(profileUnlock);

        ProfileUnlockRequest request = ProfileUnlockRequest.builder()
                .unlockMethod("ticket")
                .build();

        // when // then
        assertThatThrownBy(() -> profileUnlockService.unlockProfile(unlockedUser.getId(), unlocker.getMobileNumber(), request, now))
                .isInstanceOf(CustomException.class)
                .hasMessage("이미 프로필 잠금을 해제했습니다.");
    }

    @DisplayName("상대방 프로필을 조회할 때, 잠금을 해제한 경우 unlock 값은 true다.")
    @Test
    void getUserProfileWithUnlockProfile() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User unlocker = createUser("박지성", "01012345678", 10, 5);
        User unlockedUser = createUser("강혜원", "01011112222", 10, 5);
        userRepository.saveAll(List.of(unlocker, unlockedUser));

        ProfileUnlock profileUnlock = ProfileUnlock.builder()
                .unlocker(unlocker)
                .unlockedUser(unlockedUser)
                .registeredDateTime(now)
                .build();
        profileUnlockRepository.save(profileUnlock);

        // when
        UserProfileResponse userProfileResponse = profileUnlockService.getUserProfile(unlockedUser.getId(), unlocker.getMobileNumber());

        // then
        assertThat(userProfileResponse.getId()).isNotNull();
        assertThat(userProfileResponse.isUnlock()).isTrue();
    }

    @DisplayName("상대방 프로필을 조회할 때, 잠금을 해제하지 않은 경우 unlock 값은 false다.")
    @Test
    void getUserProfileWithoutUnlockProfile() {
        // given
        LocalDateTime now = LocalDateTime.now();
        User user1 = createUser("박지성", "01012345678", 10, 5);
        User user2 = createUser("강혜원", "01011112222", 10, 5);
        userRepository.saveAll(List.of(user1, user2));

        // when
        UserProfileResponse userProfileResponse = profileUnlockService.getUserProfile(user2.getId(), user1.getMobileNumber());

        // then
        assertThat(userProfileResponse.getId()).isNotNull();
        assertThat(userProfileResponse.isUnlock()).isFalse();
    }

    private User createUser(final String nickname, final String mobileNumber, final int candy, final int ticket) {
        return User.builder()
                .mobileNumber(mobileNumber)
                .deviceId("123456")
                .authority(Authority.USER)
                .birth(LocalDate.now())
                .imageUrl("이미지")
                .address("주소")
                .gender(Gender.MALE)
                .nickname(nickname)
                .candy(candy)
                .ticket(ticket)
                .build();
    }

}