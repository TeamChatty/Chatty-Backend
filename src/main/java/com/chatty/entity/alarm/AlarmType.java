package com.chatty.entity.alarm;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AlarmType {

    MARKETING("마케팅"),
    FEED("피드"),
    CHATTING("채팅"),
    PROFILE("프로필");

    private final String text;

}
