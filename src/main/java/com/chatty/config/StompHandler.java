package com.chatty.config;

import com.chatty.constants.Code;
import com.chatty.exception.CustomException;
import com.chatty.jwt.JwtTokenProvider;
import com.chatty.utils.jwt.JwtTokenUtils;
import com.chatty.validator.TokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.Objects;


@Slf4j
@RequiredArgsConstructor
@Component
public class StompHandler implements ChannelInterceptor {

    private final TokenValidator tokenValidator;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String accessToken = accessor.getFirstNativeHeader("Authorization");
        log.info("PreSendMethod");
        log.info("StompCommand = {}", accessor.getCommand().toString());
        log.info("message = {}", message);
        // apic 이랑 websocket 테스트 툴이랑 다름.
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("CONNECT AND SEND 검증");
            tokenValidator.validateAccessToken(accessToken);

            accessToken = JwtTokenUtils.getAccessToken(accessToken);
            String mobileNumber = jwtTokenProvider.getMobileNumber(accessToken);
            log.info("입력된 유저 번호 = {}", mobileNumber);
            accessor.getSessionAttributes().put("mobileNumber", mobileNumber);
        }

        log.info("preSend End");
        return message;
    }
}