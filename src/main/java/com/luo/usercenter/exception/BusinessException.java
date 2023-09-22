package com.luo.usercenter.exception;

import com.luo.usercenter.common.ErrorCode;

/**
 * @author 木南
 * @version 1.0
 * @Description 自定义全局异常类
 * @since 2023/9/16 10:12
 */
public class BusinessException extends RuntimeException {
    private static final long serialVersionUID = -6122123150809523900L;
    private final int code;
    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode, String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
