package com.dyx.picturebackend.exception;

import lombok.Data;
import lombok.Getter;

/**
 * 自定义异常
 */
@Getter
public class BusinessException extends RuntimeException{
    private final int code;


    public BusinessException(int code,String message){
        super(message);
        this.code=code;
    }
    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code=errorCode.getCode();
    }
    public BusinessException(ErrorCode errorCode,String mes){
        super(mes);
        this.code=errorCode.getCode();
    }

}
