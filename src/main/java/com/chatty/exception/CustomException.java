package com.chatty.exception;

import com.chatty.constants.Code;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{

    private final Code code;

    public CustomException(Code code){
        super(code.getMessage());
        this.code = code;
    }

    public CustomException(Code code, String message){
        super(message);
        this.code = code;
    }
}
