package com.dyx.picturebackend.exception;

public class ThrowUtils {
    public static void throwif(boolean condition, RuntimeException runtimeException){
        if (condition) {
            throw runtimeException;
        }
    }
    public static void throwif(boolean condition, ErrorCode errorCode) {
        throwif(condition,new BusinessException(errorCode));
    }
    public static void throwif(boolean condition, ErrorCode errorCode,String message) {
        throwif(condition,new BusinessException(errorCode,message));
    }

}
