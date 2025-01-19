package com.projectn.projectn.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {
    private String code;
    private Object[] objects;

    public AppException(String code, Object[] objects, String msg) {
        super(msg);
        this.code = code;
        this.objects = objects;
    }
}
