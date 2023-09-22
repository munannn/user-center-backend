package com.luo.usercenter.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 木南
 * @version 1.0
 * @Description 通用返回类
 * @since 2023/9/15 20:22
 */
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serialVersionUID = -239469351666829019L;

    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message, String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this(code, data, message, "");
    }

    public BaseResponse(int code, T data) {
        this(code, data, "", "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage(), errorCode.getDescription());
    }
}
