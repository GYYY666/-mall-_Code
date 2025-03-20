package com.imuke.mall.controller;

import com.imuke.mall.consts.MallConsts;
import com.imuke.mall.form.UserLoginForm;
import com.imuke.mall.form.UserRegisterForm;
import com.imuke.mall.pojo.User;
import com.imuke.mall.service.IUserService;
import com.imuke.mall.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

/**
 * @author guanyun
 * @since 2025/2/13 19:07
 */
@RestController
//@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @PostMapping("/user/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm userform) {

//        if (bindingResult.hasErrors()) {
//            log.error("注册提交的参数有误，{} {}",
//                    Objects.requireNonNull(bindingResult.getFieldError()).getField(),
//                    bindingResult.getFieldError().getDefaultMessage()) ;
////            throw new RuntimeException(bindingResult.getAllErrors().get(0).getDefaultMessage());
//            return ResponseVo.error(ResponseEnum.PARAM_ERROR, bindingResult);
//        }

        User user = new User();
        BeanUtils.copyProperties(userform,user);

        return userService.register(user);

    }

    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm,
                                  HttpSession session){
        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());

        //设置session
        session.setAttribute(MallConsts.CURRENT_USER,userResponseVo.getData());
        log.info("/login sessionId = {}" , session.getId());
        return userResponseVo;

    }

    @GetMapping("/user")
    public ResponseVo<User> userInfo(HttpSession session){
        log.info("/user sessionId = {}" , session.getId());
        User user = (User) session.getAttribute(MallConsts.CURRENT_USER);
        return ResponseVo.sucess(user);
    }

    @PostMapping("/user/logout")

//    {@link TomcatServletWebServerFactory}  getSessionTimeoutMinutes}

    public ResponseVo logout(HttpSession session) {
        log.info("/user/logout sessionId = {}", session.getId());
        session.removeAttribute(MallConsts.CURRENT_USER);
        return ResponseVo.sucess();
    }
}
