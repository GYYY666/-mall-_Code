package com.imuke.mall.service;


import com.imuke.mall.pojo.User;
import com.imuke.mall.vo.ResponseVo;

public interface IUserService {
    /**
     * 注册
     */
    ResponseVo<User> register(User user);

    /**
     * 登录
     */
    ResponseVo<User> login(String username, String password);
}
