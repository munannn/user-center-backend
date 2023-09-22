package com.luo.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.luo.usercenter.common.ErrorCode;
import com.luo.usercenter.exception.BusinessException;
import com.luo.usercenter.mapper.UserMapper;
import com.luo.usercenter.model.domain.User;
import com.luo.usercenter.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
        }
        // 账号长度不少于4位
        if (userAccount.length() < ACCOUNT_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度过短");
        }
        // 密码长度不少于8位
        if (userPassword.length() < PASSWORD_MIN_LENGTH) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度过短");
        }
        // 账号不包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
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




