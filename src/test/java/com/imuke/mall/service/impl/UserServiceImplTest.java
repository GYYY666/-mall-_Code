package com.imuke.mall.service.impl;

import com.imuke.mall.MallApplicationTests;
import com.imuke.mall.enums.ResponseEnum;
import com.imuke.mall.enums.RoleEnum;
import com.imuke.mall.pojo.User;
import com.imuke.mall.service.IUserService;
import com.imuke.mall.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional //数据回滚，避免污染数据库
public class UserServiceImplTest extends MallApplicationTests {

    public static final String USERNAME = "jack";
    public static final String PASSWORD = "123456";

    @Autowired
    private IUserService userService;
    @Before
    public void register() {
        User user = new User(USERNAME,PASSWORD,"123@qq.com", RoleEnum.CUSTOMER.getCode());
        userService.register(user);
    }

    @Test
    public void login() {
//        register();
        ResponseVo<User> responseVo = userService.login(USERNAME,PASSWORD);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
}