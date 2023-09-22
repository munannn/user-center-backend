package com.luo.usercenter.service;

import com.luo.usercenter.model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author 木南
 * @version 1.0
 * @Description TODO
 * @since 2023/9/2 17:37
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testAddUser() {
        User user = new User();
        user.setUsername("luobo");
        user.setUserAccount("188818498");
        user.setAvatarUrl("https://baomidou.com/img/logo.svg");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setEmail("2087609705@qq.com");
        user.setPhone("1881854267");
        boolean result = userService.save(user);
        System.out.println(user.getId());
        Assertions.assertTrue(result);
    }

    @Test
    void userRegister() {
        String userAccount = "yupi";
        String planetCode = "1234";
        String userPassword = "";
        String checkPassword = "123456";
        long result = userService.userRegister(userAccount, planetCode, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        userAccount = "yu";
        result = userService.userRegister(userAccount, planetCode, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        userAccount = "yupi";
        userPassword = "123456";
        result = userService.userRegister(userAccount, planetCode, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        userAccount = "yu pi";
        userPassword = "12345678";
        result = userService.userRegister(userAccount, planetCode, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        checkPassword = "123456789";
        result = userService.userRegister(userAccount, planetCode, userPassword, checkPassword);
        Assertions.assertEquals(-1, result);
        userAccount = "dogluobo";
        checkPassword = "12345678";
        result = userService.userRegister(userAccount, planetCode, userPassword, checkPassword);
        Assertions.assertTrue(result > 0);
        userAccount = "yupi";
        result = userService.userRegister(userAccount, planetCode, userPassword, checkPassword);
        Assertions.assertTrue(result > 0);
    }
}