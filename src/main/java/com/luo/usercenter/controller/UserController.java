package com.luo.usercenter.controller;

import com.luo.usercenter.common.BaseResponse;
import com.luo.usercenter.common.ErrorCode;
import com.luo.usercenter.common.ResultUtils;
import com.luo.usercenter.constant.UserConstant;
import com.luo.usercenter.exception.BusinessException;
import com.luo.usercenter.model.domain.User;
import com.luo.usercenter.model.domain.request.UserLoginRequest;
import com.luo.usercenter.model.domain.request.UserRegisterRequest;
import com.luo.usercenter.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 木南
 * @version 1.0
 * @Description 用户接口
 * @since 2023/9/4 10:34
 */
@RestController
@RequestMapping("/user")
@Api("用户管理")
public class UserController {

    @Resource
    private UserService userService;

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String planetCode = userRegisterRequest.getPlanetCode();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, planetCode, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        long result = userService.userRegister(userAccount, planetCode, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    @PostMapping("/logout")
    @ApiOperation(value = "用户注销")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    @GetMapping("/current")
    @ApiOperation(value = "获取当前登录用户")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        long userId = user.getId();
        User currentUser = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(currentUser);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    @ApiOperation(value = "搜索用户", notes = "根据用户名搜索，无传入值搜索所有用户")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request) {
        //鉴权
        if (!ifAdmin(request)) {
            throw new BusinessException(ErrorCode.NOT_AUTH, "用户无权限查询");
        }
        List<User> userList = userService.searchUser(username);
        List<User> safetyUserList =
                userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(safetyUserList);
    }

    @PostMapping("/delete")
    @ApiOperation(value = "删除用户", notes = "需要有管理员权限")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!ifAdmin(request)) {
            throw new BusinessException(ErrorCode.NOT_AUTH, "用户无权限查询");
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户编号非法");
        }
        boolean result = userService.deleteUser(id);
        return ResultUtils.success(result);
    }

    @PostMapping("/searchUsersByTags")
    @ApiOperation(value = "根据标签搜索用户")
    public BaseResponse<List<User>> searchUsersByTags(@RequestBody List<String> tagList) {
        if (CollectionUtils.isEmpty(tagList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagList);
        return ResultUtils.success(userList);
    }

    /**
     * 判断登录用户是否为管理员
     * inverted
     *
     * @param request
     * @return
     */
    private boolean ifAdmin(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(UserConstant.USER_LOGIN_STATUS);
        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }
}
