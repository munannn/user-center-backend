package com.luo.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.luo.usercenter.common.ErrorCode;
import com.luo.usercenter.exception.BusinessException;
import com.luo.usercenter.mapper.UserMapper;
import com.luo.usercenter.model.domain.User;
import com.luo.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.luo.usercenter.constant.UserConstant.*;

/**
 * @author 木南
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2023-09-02 17:23:55
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {
    private static final String SALT = "luobo";

    @Resource
    private UserMapper userMapper;

    @Override
    public long userRegister(String userAccount, String planetCode, String userPassword, String checkPassword) {
        // 非空判断
        if (StringUtils.isAnyBlank(userAccount, planetCode, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 星球编号长度不大于5位
        if (planetCode.length() > PLANETCODE_MAX_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        // 账号长度不少于4位
        if (userAccount.length() < ACCOUNT_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        // 密码长度不少于8位
        if (userPassword.length() < PASSWORD_MIN_LENGTH || checkPassword.length() < PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        // 账号不包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }
        // 账号不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_DUPLICATION);
        }
        // 星球编号不能重复
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = this.count(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PLANET_CODE_DUPLICATION);
        }
        // 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 保存用户
        User user = new User();
        user.setUserAccount(userAccount);
        user.setPlanetCode(planetCode);
        user.setUserPassword(encryptPassword);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            throw new BusinessException(ErrorCode.BUSINESS_ERROR, "保存用户失败");
        }
        // 返回用户id
        return user.getId();
    }

    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 非空判断
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        // 账号长度不少于4位
        if (userAccount.length() < ACCOUNT_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号长度过短");
        }
        // 密码长度不少于8位
        if (userPassword.length() < PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码长度过短");
        }
        // 账号不包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号包含特殊字符");
        }
        // 校验密码
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword!");
            return null;
        }
        User safetyUser = getSafetyUser(user);
        // 记录登录态
        request.getSession().setAttribute(USER_LOGIN_STATUS, safetyUser);
        // 返回脱敏用户信息
        return safetyUser;
    }

    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录状态
        request.getSession().removeAttribute(USER_LOGIN_STATUS);
        return 1;
    }


    @Override
    public List<User> searchUsersByTags(List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 查询所有用户
        List<User> users = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        // 使用filter过滤包含标签列表的用户
        return users.stream().filter(user -> {
            // 标签列表为JSON格式
            String tagsStr = user.getTags();
            // 判空
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }
            // 使用Gson将JSON反序列化为Java对象
            Set<String> tempTagSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            tempTagSet = Optional.ofNullable(tempTagSet).orElse(new HashSet<>());
            return tempTagSet.containsAll(tagList);
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    @Deprecated
    public List<User> searchUsersByTagsBySQL(List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 从数据库中匹配
        // 循环添加所有查询标签
        for (String tagName : tagList) {
            queryWrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(queryWrapper);
        // this::getSafetyUser --> user -> { getSafetyUser(user); }
        // 使用Java 8特性 stream流
        return userList.stream().map(this::getSafetyUser)
                .collect(Collectors.toList());
    }

    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        // 用户信息脱敏
        User safetyUser = new User();
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    @Override
    public List<User> searchUser(String username) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        // 根据username是否为空进行模糊查询或无条件查询
        if (StringUtils.isNoneBlank(username)) {
            queryWrapper.like("username", username);
            return userMapper.selectList(queryWrapper);
        }
        return userMapper.selectList(null);
    }

    @Override
    public boolean deleteUser(long id) {
        return userMapper.deleteById(id) > 0;
    }
}




