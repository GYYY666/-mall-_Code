package com.imuke.mall;

import com.imuke.mall.consts.MallConsts;
import com.imuke.mall.exception.UserLoginException;
import com.imuke.mall.pojo.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author guanyun
 * @since 2025/2/14 18:58
 */
@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {
    @Override
    //执行前拦截 ->preHandle
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle...");
        User user = (User) request.getSession().getAttribute(MallConsts.CURRENT_USER);
        if (user == null){
            log.info("user=null");
            throw new UserLoginException();
//            return ResponseVo.error(ResponseEnum.NEED_LOGIN);
//            return false;
        }
        return true;
    }
}
