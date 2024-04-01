package com.chatty.config;

import com.chatty.handler.ChatHandler;
import com.chatty.jwt.JwtTokenProvider;
import com.chatty.validator.TokenValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Slf4j
@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer{

    private final TokenValidator tokenValidator;
    private final JwtTokenProvider jwtTokenProvider;
    private final ChatHandler chatHandler;
    private final StompHandler stompHandler;
    private final StompExceptionHandler stompExceptionHandler;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {

        registry.setErrorHandler(stompExceptionHandler)
                .addEndpoint("/ws")
//                .addInterceptors(new WebSocketMatchInterceptor(tokenValidator, jwtTokenProvider))
                .setAllowedOriginPatterns("*");

        registry.addEndpoint("/signaling")
//                .addInterceptors(new WebSocketMatchInterceptor(tokenValidator, jwtTokenProvider))
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 메시지를 구독하는 요청 url
        registry.enableSimpleBroker("/sub");

        // 메시지를 발행하는 요청 url
        registry.setApplicationDestinationPrefixes("/pub"); // Controller 객체의 MessageMapping 메서드 라우팅
    }

    @Override
    public void configureClientInboundChannel(final ChannelRegistration registration) {
        log.info("configureClientInboundChannel");
        registration.interceptors(stompHandler);
    }
}
