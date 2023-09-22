package com.luo.usercenter.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author 木南
 * @version 1.0
 * @Description 用户注册请求体
 * @since 2023/9/4 10:40
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 5153101715985359459L;
    private String userAccount;
    private String planetCode;
    private String userPassword;
    private String checkPassword;
}
