package com.luo.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 木南
 * @version 1.0
 * @Description 用户登录请求体
 * @since 2023/9/4 10:40
 */
@Data
public class UserLoginRequest implements Serializable {
    private static final long serialVersionUID = -7710927194250944670L;
    private String userAccount;
    private String userPassword;
}
