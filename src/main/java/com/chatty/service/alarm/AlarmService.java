package com.chatty.service.alarm;

import com.chatty.constants.Code;
import com.chatty.dto.alarm.request.AlarmCreateRequest;
import com.chatty.dto.alarm.response.AlarmListResponse;
import com.chatty.dto.alarm.response.AlarmResponse;
import com.chatty.entity.alarm.Alarm;
import com.chatty.entity.alarm.AlarmType;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.alarm.AlarmRepository;
import com.chatty.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    @Transactional
    public AlarmResponse createAlarm(final AlarmCreateRequest request) {
        User user = userRepository.findById(request.getToUser())
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        Alarm alarm = request.toEntity(user);
        alarmRepository.save(alarm);

        return AlarmResponse.of(alarm);
    }

    @Transactional
    public AlarmResponse createCommentAlarm(Long postId, Long fromUser, String fromUserNickname, User toUser, Long commentId) {
        Alarm alarm = Alarm.builder()
                .isRead(false)
                .alarmType(AlarmType.FEED)
                .commentId(commentId)
                .fromUser(fromUser)
                .content(fromUserNickname + "님이 댓글을 남겼습니다.")
                .postId(postId)
                .user(toUser)
                .build();

        alarmRepository.save(alarm);
        return AlarmResponse.of(alarm);
    }

    @Transactional
    public AlarmResponse createLikeAlarm(Long postId, Long fromUser, String fromUserNickname, User toUser) {
        Alarm alarm = Alarm.builder()
                .isRead(false)
                .alarmType(AlarmType.FEED)
                .fromUser(fromUser)
                .content(fromUserNickname + "님이 좋아요를 눌렀습니다.")
                .postId(postId)
                .user(toUser)
                .build();

        alarmRepository.save(alarm);
        return AlarmResponse.of(alarm);
    }

    @Transactional
    public AlarmResponse createCommentLikeAlarm(Long commentId, Long fromUser, String fromUserNickname, User toUser) {
        Alarm alarm = Alarm.builder()
                .isRead(false)
                .alarmType(AlarmType.FEED)
                .fromUser(fromUser)
                .content(fromUserNickname + "님이 좋아요를 눌렀습니다.")
//                .postId(postId)
                .commentId(commentId)
                .user(toUser)
                .build();

        alarmRepository.save(alarm);
        return AlarmResponse.of(alarm);
    }

    @Transactional
    public AlarmResponse createProfileAlarm(Long fromUser, String fromUserNickname, User toUser) {
        Alarm alarm = Alarm.builder()
                .isRead(false)
                .alarmType(AlarmType.PROFILE)
                .fromUser(fromUser)
                .content(fromUserNickname + "님이 프로필 잠금을 해제했습니다.")
                .user(toUser)
                .build();

        alarmRepository.save(alarm);
        return AlarmResponse.of(alarm);
    }

    public List<AlarmListResponse> getAlarmListPages(final Long lastAlarmId, final int size, final String mobileNumber) {
        PageRequest pageRequest = PageRequest.of(0, size);

        User user = userRepository.getByMobileNumber(mobileNumber);

        Page<Alarm> alarms = alarmRepository.findByIdLessThanAndUserOrderByIdDesc(lastAlarmId, user, pageRequest);

        return alarms.getContent().stream()
                .map(AlarmListResponse::of)
                .toList();
    }

    @Transactional
    public AlarmResponse readAlarm(final Long alarmId, final String mobileNumber) {
        User user = userRepository.getByMobileNumber(mobileNumber);

        Alarm alarm = alarmRepository.getById(alarmId);

        if (!alarm.getUser().getId().equals(user.getId())) {
            throw new CustomException(Code.NOT_EXIST_ALARM);
        }

        alarm.readAlarm(true);

        return AlarmResponse.of(alarm);
    }

    @Transactional
    public void readAllAlarm(final String mobileNumber) {
        User user = userRepository.getByMobileNumber(mobileNumber);

        List<Alarm> result = alarmRepository.findAllByUserAndIsReadIsFalse(user);

        for (Alarm alarm : result) {
            alarm.readAlarm(true);
        }
    }
}
