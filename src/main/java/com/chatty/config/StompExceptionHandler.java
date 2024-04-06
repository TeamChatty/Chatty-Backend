package com.chatty.config;

import java.nio.charset.StandardCharsets;

import com.chatty.constants.Code;
import com.chatty.dto.ErrorResponse;
import com.chatty.exception.CustomException;
import com.chatty.handler.GlobalExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

@Slf4j
@RequiredArgsConstructor
@Component
public class StompExceptionHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;
    private final GlobalExceptionHandler globalExceptionHandler;

    /**
     * 클라이언트 메시지 처리 중에 발생한 오류를 처리
     *
     * @param clientMessage 클라이언트 메시지
     * @param ex 발생한 예외
     * @return 오류 메시지를 포함한 Message 객체
     */
    @Override
    public Message<byte[]> handleClientMessageProcessingError(
            Message<byte[]> clientMessage,
            Throwable ex) {
        log.info("StompExceptionHandler = {}", ex.getMessage());
        Throwable cause = ex.getCause();

        if (cause instanceof CustomException) {
            try {
                return errorMessage(globalExceptionHandler.handleNormalException((CustomException) cause).getBody());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }

        return super.handleClientMessageProcessingError(clientMessage, ex);
    }

    /**
     * 오류 메시지를 포함한 Message 객체를 생성
     *
     * @return 오류 메시지를 포함한 Message 객체
     */
    private Message<byte[]> errorMessage(ErrorResponse ex) throws JsonProcessingException {
        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true);

        return MessageBuilder.createMessage(objectMapper.writeValueAsString(ex).getBytes(StandardCharsets.UTF_8),
                accessor.getMessageHeaders());
    }
}
