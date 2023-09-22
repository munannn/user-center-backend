package com.luo.usercenter.constant;

/**
 * @author 木南
 * @version 1.0
 * @Description 用户常量
 * @since 2023/9/4 16:18
 */
public interface UserConstant {

    /**
     * 账号最短长度
     */
    int ACCOUNT_MIN_LENGTH = 4;

    /**
     * 星球编号最大长度
     */
    int PLANETCODE_MAX_LENGTH = 5;

    /**
     * 密码最短长度
     */
    int PASSWORD_MIN_LENGTH = 8;

    /**
     * 用户登录状态键
     */
    String USER_LOGIN_STATUS = "loginUser";

    /**
     * 默认权限
     */
    int DEFAULT_ROLE = 0;

    /**
     * 管理员权限
     */
    int ADMIN_ROLE = 1;
}
