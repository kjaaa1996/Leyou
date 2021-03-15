package com.leyou.cart.interceptor;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.cart.config.JwtProperties;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 26747
 * @description LoginIntercepter
 * @date 2020/6/8 9:39
 */
@Component("loginInterceptor")
@EnableConfigurationProperties(JwtProperties.class)
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Resource
    private JwtProperties jwtProperties;

    //定义一个线程域，存放登录用户
    private static final ThreadLocal<UserInfo> tl = new ThreadLocal<>();

    /**
     * 前置方法 判断是否登录成功，成功放入线程域，失败返回401
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //查询token
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        if (StringUtils.isBlank(token)) {
            //未登录，返回401
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return false;
        }
        //有token查询用户信息
        try {
            //解析成功，证明已登录
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
            //放入线程域
            tl.set(userInfo);
            return true;
        } catch (Exception e) {
            //抛出异常，证明未登录，返回401
            response.setStatus(401);
            return false;
        }
    }

    /**
     * 完成方法 清空线程域 必须重写 因为使用的tomcat的线程池，线程不会结束
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
        tl.remove();
    }

    /**
     * 获取登录用户
     *
     * @return
     */
    public static UserInfo getUserInfo() {
        return tl.get();
    }
}
