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
    NOT_SEND_SMS(HttpStatus.BAD_REQUEST, "naver에서 sms전송 실패","E005"),
    ALREADY_EXIST_NICKNAME(HttpStatus.CONFLICT, "이미 존재 하는 닉네임 입니다.","E006"),
    INVALID_AUTH_NUMBER(HttpStatus.BAD_REQUEST, "유효하지 않은 인증 번호 입니다.","E007"),
    ALREADY_EXIST_USER(HttpStatus.BAD_REQUEST, "이미 존재 하는 유저 입니다.","E008"),
    EXPIRED_REFRESH_TOKEN(HttpStatus.BAD_REQUEST, "refreshToken이 만료되었습니다.","E009"),
    NOT_FOUND_CHAT_ROOM(HttpStatus.BAD_REQUEST, "채팅방이 존재하지 않습니다.","E011"),
    ALREADY_EXIST_CHATROOM(HttpStatus.BAD_REQUEST, "채팅방이 이미 존재합니다.","E012"),
    NOT_FOUND_CHAT_MESSAGE(HttpStatus.BAD_REQUEST, "채팅 내용이 존재하지 않습니다.","E013"),
    NOT_IN_USER_ROOM(HttpStatus.BAD_REQUEST, "유저가 채팅방에 존재하지 않습니다.","E015"),
    INVALID_EXTENSION(HttpStatus.BAD_REQUEST, "올바르지 않은 확장자입니다.", "E016"), // 형이 작성한 Code null로 해놓을게
    NOT_EXIST_MATCH(HttpStatus.BAD_REQUEST, "존재하지 않는 매치입니다.", "E017"),
    MATCH_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "일일 매칭 횟수 제한을 초과했습니다.", "E018"),
    FAIL_AUTH_CHECK(HttpStatus.BAD_REQUEST, "계정 확인에 실패했습니다.", "E019"),
    NOT_EXIST_AUTHCHECK(HttpStatus.BAD_REQUEST, "계정 확인 이력이 존재하지 않습니다.", "E020"),
    NOT_CHECK_ALL_QUESTION(HttpStatus.BAD_REQUEST, "계정 확인 질문을 전부 완료해야 합니다.","E021"),
    NOT_BLUECHECK_USER(HttpStatus.UNAUTHORIZED, "프로필 인증이 되어있지 않습니다.", "E022"),
    INVALID_DEVICE_NUMER(HttpStatus.BAD_REQUEST, "기존 계정과 기기 번호가 일치하지 않습니다.","E023"),
    NOT_EXIST_INTEREST(HttpStatus.BAD_REQUEST, "존재하지 않는 관심사입니다.", "E024"),
    ALREADY_UNLOCK_PROFILE(HttpStatus.CONFLICT, "이미 프로필 잠금을 해제했습니다.", "E025"),
    INSUFFICIENT_CANDY(HttpStatus.BAD_REQUEST, "캔디의 개수가 부족합니다.", "E026"),
    INSUFFICIENT_TICKET(HttpStatus.BAD_REQUEST, "티켓의 개수가 부족합니다.", "E027"),
    NOT_AUTHORITY(HttpStatus.FORBIDDEN, "권한이 없습니다.", "E028"),
    NOT_EXIST_SUBSCRIPTION(HttpStatus.NOT_FOUND, "존재하지 않는 구독권입니다.", "E029"),
    NOT_AUTHORITY_USER(HttpStatus.FORBIDDEN, "회원가입을 완료해주세요.", "E030"),
    NOT_EXIST_POST(HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다.", "E031"),
    NOT_EXIST_COMMENT(HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다.", "E032"),
    EXPIRED_CHAT_TIME(HttpStatus.BAD_REQUEST, "제한 시간을 초과했습니다.", "E033"),
    ALREADY_LIKE_POST(HttpStatus.BAD_REQUEST, "이미 좋아요를 눌렀습니다.", "E034"),
    NOT_EXIST_LIKE_POST(HttpStatus.NOT_FOUND, "좋아요가 존재하지 않습니다.", "E035"),
    AUTH_NUMBER_LIMIT(HttpStatus.BAD_REQUEST, "요청 일일횟수 제한은 5번입니다.", "E036"),
    ALREADY_EXIST_BOOKMARK(HttpStatus.BAD_REQUEST, "북마크가 이미 존재합니다.","E037"),
    NOT_FOUND_BOOKMARK(HttpStatus.NOT_FOUND, "북마크가 존재하지 않습니다.", "E038"),
    ALREADY_BLOCK_USER(HttpStatus.CONFLICT, "이미 차단한 유저입니다.", "E039"),
    NOT_EXIST_ALARM(HttpStatus.NOT_FOUND, "존재하지 않는 알람입니다.", "E040"),
    ALREADY_REPORT_USER(HttpStatus.CONFLICT, "이미 신고한 유저입니다.", "E041"),
    NOT_SELF_REPORT(HttpStatus.BAD_REQUEST, "자기 자신을 신고할 수 없습니다.", "E042"),

    // ratelimit
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "너무 많은 요청을 보냈습니다.","E098");

    private final HttpStatus httpStatus;
    private final String message;
    private final String errorCode;
}