package com.xm.reggie.common;

/**
 * 自定义异常
 * @author YU
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
