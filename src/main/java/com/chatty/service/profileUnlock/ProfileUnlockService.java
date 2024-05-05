package com.chatty.service.profileUnlock;

import com.chatty.constants.Code;
import com.chatty.dto.profileUnlock.request.ProfileUnlockRequest;
import com.chatty.dto.profileUnlock.response.ProfileUnlockResponse;
import com.chatty.dto.user.response.UserProfileResponse;
import com.chatty.entity.user.ProfileUnlock;
import com.chatty.entity.user.User;
import com.chatty.exception.CustomException;
import com.chatty.repository.profileUnlock.ProfileUnlockRepository;
import com.chatty.repository.user.UserRepository;
import com.chatty.service.alarm.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ProfileUnlockService {

    private final ProfileUnlockRepository profileUnlockRepository;
    private final UserRepository userRepository;
    private final AlarmService alarmService;

    @Transactional
    public ProfileUnlockResponse unlockProfile(final Long unlockedUserId, final String mobileNumber, final ProfileUnlockRequest request, final LocalDateTime now) {
        User unlocker = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        User unlockedUser = userRepository.findById(unlockedUserId)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        if (profileUnlockRepository.existsByUnlockerIdAndUnlockedUserId(unlocker.getId(), unlockedUserId)) {
            throw new CustomException(Code.ALREADY_UNLOCK_PROFILE);
        }

        String unlockMethod = request.getUnlockMethod();

        if (unlockMethod.equals("candy")) {
            if (unlocker.isCandyQuantityLessThan(7)) {
                throw new CustomException(Code.INSUFFICIENT_CANDY);
            }

            unlocker.deductCandyQuantity(7);
        } else if (unlockMethod.equals("ticket")) {
            if (unlocker.isTicketQuantityLessThan(1)) {
                throw new CustomException(Code.INSUFFICIENT_TICKET);
            }

            unlocker.deductTicketQuantity(1);
        }

        ProfileUnlock profileUnlock = request.toEntity(unlocker, unlockedUser, now);
        profileUnlockRepository.save(profileUnlock);

        alarmService.createProfileAlarm(unlocker.getId(), unlocker.getNickname(), unlockedUser);

        return ProfileUnlockResponse.of(profileUnlock);
    }

    @Transactional
    public UserProfileResponse getUserProfile(final Long userId, final String mobileNumber) {
        User me = userRepository.findUserByMobileNumber(mobileNumber)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(Code.NOT_EXIST_USER));

        boolean result = profileUnlockRepository.existsByUnlockerIdAndUnlockedUserId(me.getId(), user.getId());

        return UserProfileResponse.of(user, result);
    }
}
