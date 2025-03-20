package com.imuke.mall.service.impl;

import com.imuke.mall.dao.UserMapper;
import com.imuke.mall.enums.ResponseEnum;
import com.imuke.mall.enums.RoleEnum;
import com.imuke.mall.pojo.User;
import com.imuke.mall.service.IUserService;
import com.imuke.mall.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

/**
 * @author guanyun
 * @since 2025/2/12 17:32
 */
@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public ResponseVo<User> register(User user) {
//        error();
        //username不能重复
        int countByUsename = userMapper.countByUsername(user.getUsername());
        if (countByUsename > 0) {
            return ResponseVo.error(ResponseEnum.USERNAME_EXIST);

        }
        //不能重复
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if (countByEmail > 0) {
            return ResponseVo.error(ResponseEnum.EMAIL_EXIST);
        }

        user.setRole(RoleEnum.CUSTOMER.getCode());
        //MD5摘要算法(Spring自带)
		user.setPassword(DigestUtils.md5DigestAsHex(
				user.getPassword().getBytes(StandardCharsets.UTF_8))
		);

        //写入数据库
        int resultCount = userMapper.insertSelective(user);
		if (resultCount == 0) {
            return ResponseVo.error(ResponseEnum.ERROR);
		}

        return ResponseVo.sucess();

    }

    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null) {
            //用户不不存在
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        if(!user.getPassword().equalsIgnoreCase(DigestUtils.md5DigestAsHex(
				password.getBytes(StandardCharsets.UTF_8)))){
            //密码错误（返回用户名或密码错误
            // ）
            return ResponseVo.error(ResponseEnum.USERNAME_OR_PASSWORD_ERROR);
        }
        user.setPassword("");
        return ResponseVo.sucess(user);
    }

    public void error(){
        throw new RuntimeException("意外错误");
    }
}
