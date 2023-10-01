package com.luo.usercenter.service;

import com.luo.usercenter.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 木南
 * @description 针对表【user(用户表)】的数据库操作Service
 * @createDate 2023-09-02 17:23:55
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 账号
     * @param planetCode 星球编号
     * @param userPassword 密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    long userRegister(String userAccount, String planetCode, String userPassword, String checkPassword);

    /**
     * 用户登录
     * @param userAccount 账号
     * @param userPassword 密码
     * @param request 请求对象
     * @return 脱敏后的用户
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户注销
     * @param request 请求对象
     * @return 响应值
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据标签查找用户
     * @param tagList 标签列表
     * @return 包含标签列表的用户列表
     */
    List<User> searchUsersByTags(List<String> tagList);

    /**
     * 用户脱敏
     * @param user 源对象
     * @return 脱敏后对象
     */
    User getSafetyUser(User user);

    /**
     * 查询用户
     * @param username 用户名
     * @return 匹配用户集合
     */
    List<User> searchUser(String username);

    /**
     * 删除用户
     *
     * @param id 用户id
     * @return 删除是否成功
     */
    boolean deleteUser(long id);
}
