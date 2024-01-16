package com.chatty.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum Code {

    OK(HttpStatus.OK, "응답 성공",null),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "올바르지 않은 파라미터 형식입니다.","E000"),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "토큰 인증을 실패했습니다.", "E001"),
    EXPIRED_ACCESS_TOKEN(HttpStatus.BAD_REQUEST, "accessToken이 만료되었습니다.","E002"),
    NOT_EXIST_USER(HttpStatus.BAD_REQUEST, "존재 하지 않는 유저 입니다.","E003"),
    NOT_SEND_SMS(HttpStatus.BAD_REQUEST, "naver에서 sms전송 실패","E-005"),
    ALREADY_EXIST_NICKNAME(HttpStatus.CONFLICT, "이미 존재 하는 닉네임 입니다.","E006"),
    INVALID_AUTH_NUMBER(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 번호 입니다.","E007"),
    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST, "이미 존재 하는 유저 입니다.","E008"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "refreshToken이 만료되었습니다.","E009"),
    NOT_FOUND_CHAT_ROOM(HttpStatus.BAD_REQUEST, "채팅방이 존재하지 않습니다.","E011"),
    ALREADY_EXIST_CHATROOM(HttpStatus.BAD_REQUEST, "채팅방이 이미 존재합니다.","E012"),
    NOT_FOUND_CHAT_MESSAGE(HttpStatus.BAD_REQUEST, "채팅 내용이 존재하지 않습니다.","E013"),
    SMS_NOT_SEND(HttpStatus.BAD_GATEWAY, "sms전송을 실패했습니다.","014"),
    NOT_IN_USER_ROOM(HttpStatus.BAD_REQUEST, "유저가 채팅방에 존재하지 않습니다.","E015"),
    INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "올바르지 않은 확장자입니다.", "E016"), // 형이 작성한 Code null로 해놓을게
    NOT_EXIST_MATCH(HttpStatus.BAD_REQUEST, "존재하지 않는 매치입니다.", "E017"),
    MATCH_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "일일 매칭 횟수 제한을 초과했습니다.", "E018");

    private final HttpStatus httpStatus;
    private final String message;
    private final String errorCode;
}