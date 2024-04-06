package com.chatty.handler;

import static com.chatty.constants.Code.*;

import com.chatty.dto.ErrorResponse;
import com.chatty.exception.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ErrorResponse> handleNormalException(CustomException e){
        return ResponseEntity.status(e.getCode().getHttpStatus())
                .body(ErrorResponse.builder()
                        .errorCode(e.getCode().getErrorCode())
                        .status(e.getCode().getHttpStatus())
                        .message(e.getMessage())
                        .build());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse validationException(BindException e){
        return ErrorResponse.of(
                INVALID_PARAMETER.getErrorCode(),
                e.getBindingResult().getAllErrors().get(0).getDefaultMessage()
        );
    }

    @ExceptionHandler({JsonProcessingException.class, RestClientException.class, URISyntaxException.class, InvalidKeyException.class, NoSuchAlgorithmException.class, UnsupportedEncodingException.class})
    public ErrorResponse sendSmsException(Exception e){
        //TODO: LocalDate에 값을 어떻게 검증할 것인지? 이상하게 넣으면 Parsing 예외 발생한다.
        e.printStackTrace();
        return ErrorResponse.of(NOT_SEND_SMS.getErrorCode(),e.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ErrorResponse userException(UsernameNotFoundException e){
        return ErrorResponse.of(NOT_EXIST_USER.getErrorCode(),e.getMessage());
    }
}