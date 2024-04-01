package com.chatty.config;

import com.chatty.constants.Code;
import com.chatty.exception.CustomException;
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

    @Override
    public Message<?> preSend(final Message<?> message, final MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        log.info("PreSend = gdgd");
        System.out.println("message:" + message);
        System.out.println("헤더 : " + message.getHeaders());
        System.out.println("토큰" + accessor.getNativeHeader("Authorization"));
        System.out.println("accessor.getFirstNativeHeader(\"Authorization\") = " + accessor.getFirstNativeHeader("Authorization"));
        // apic 이랑 websocket 테스트 툴이랑 다름.
        if (StompCommand.CONNECT.equals(accessor.getCommand()) || StompCommand.SEND.equals(accessor.getCommand())) {
            System.out.println("검증을 제대로 하나요?");
//            tokenValidator.validateAccessToken(Objects.requireNonNull(accessor.getFirstNativeHeader("Authorization")));
//            throw new AccessDeniedException("invalid token");
            throw new MessageDeliveryException("invalid token");
        }
        System.out.println("message = " + message);
        log.info("message = {}", message);
        return message;
    }
}
