package com.chatty.dto.notification.receive.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class NotificationReceiveUpdateRequest {

    private boolean agree;

    @Builder
    public NotificationReceiveUpdateRequest(final boolean agree) {
        this.agree = agree;
    }
}
