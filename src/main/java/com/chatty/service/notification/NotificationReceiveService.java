package com.chatty.service.notification;

import com.chatty.dto.notification.receive.request.NotificationReceiveUpdateRequest;
import com.chatty.dto.notification.receive.response.NotificationReceiveResponse;
import com.chatty.entity.notification.NotificationReceive;
import com.chatty.entity.user.User;
import com.chatty.repository.notification.NotificationReceiveRepository;
import com.chatty.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class NotificationReceiveService {

    private final NotificationReceiveRepository notificationReceiveRepository;
    private final UserRepository userRepository;

    @Transactional
    public NotificationReceiveResponse getNotificationReceive(final String mobileNumber) {
        User user = userRepository.getByMobileNumber(mobileNumber);
        NotificationReceive notificationReceive = notificationReceiveRepository.findById(user.getNotificationReceive().getId()).get();

        return NotificationReceiveResponse.of(notificationReceive);
    }

    @Transactional
    public NotificationReceiveResponse updateMarketingNotification(final String mobileNumber,
                                                                   NotificationReceiveUpdateRequest request) {
        User user = userRepository.getByMobileNumber(mobileNumber);

        NotificationReceive notificationReceive =
                notificationReceiveRepository.getById(user.getNotificationReceive().getId());

        notificationReceive.updateMarketingNotification(request.isAgree());

        return NotificationReceiveResponse.of(notificationReceive);
    }

    @Transactional
    public NotificationReceiveResponse updateChattingNotification(final String mobileNumber,
                                                                   NotificationReceiveUpdateRequest request) {
        User user = userRepository.getByMobileNumber(mobileNumber);

        NotificationReceive notificationReceive =
                notificationReceiveRepository.getById(user.getNotificationReceive().getId());

        notificationReceive.updateChattingNotification(request.isAgree());

        return NotificationReceiveResponse.of(notificationReceive);
    }

    @Transactional
    public NotificationReceiveResponse updateFeedNotification(final String mobileNumber,
                                                                   NotificationReceiveUpdateRequest request) {
        User user = userRepository.getByMobileNumber(mobileNumber);

        NotificationReceive notificationReceive =
                notificationReceiveRepository.getById(user.getNotificationReceive().getId());

        notificationReceive.updateFeedNotification(request.isAgree());

        return NotificationReceiveResponse.of(notificationReceive);
    }
}
