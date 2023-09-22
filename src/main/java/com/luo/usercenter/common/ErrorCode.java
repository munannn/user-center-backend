package com.luo.usercenter.common;

/**
 * @author 木南
 * @version 1.0
 * @Description 错误码枚举类
 * @since 2023/9/16 9:56
 */
public enum ErrorCode {
    /**
     *
     */
    SUCCESS(0, "ok", "Everything is ok!"),
    PARAMS_ERROR(40000, "请求参数错误", ""),
    NULL_ERROR(40001, "请求数据为空", ""),
    NOT_LOGIN(40100, "用户未登录", ""),
    NOT_AUTH(40101, "用户权限不足", ""),
    USER_NOT_EXIST(40102, "用户不存在", ""),
    ACCOUNT_DUPLICATION(40200, "账号重复", ""),
    PLANET_CODE_DUPLICATION(40201, "星球编号重复", ""),
    BUSINESS_ERROR(40400, "业务异常", "该业务发生错误，请联系管理员！"),
    SYSTEM_ERROR(50000, "系统异常", "");

    /**
     * 错误码
     */
    private final int code;

    /**
     * 错误码信息
     */
    private final String message;

    /**
     * 错误码描述（详情）
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }
}
