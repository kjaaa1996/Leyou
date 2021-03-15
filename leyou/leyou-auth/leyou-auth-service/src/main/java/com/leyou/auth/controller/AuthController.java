package com.leyou.auth.controller;

import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.service.IAuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author 26747
 * @description AuthController
 * @date 2020/6/5 11:06
 */
@Controller
@EnableConfigurationProperties(JwtProperties.class)
public class AuthController {

    @Resource(name = "authService")
    private IAuthService authService;

    @Resource
    private JwtProperties properties;

    /**
     * 登录授权
     *
     * @param username
     * @param password
     * @return
     */
    @PostMapping("/accredit")
    public ResponseEntity<Void> accredit(
            @RequestParam("username") String username,
            @RequestParam("password") String password,
            HttpServletRequest request,
            HttpServletResponse response) {
        //1.登录校验
        String token = this.authService.accredit(username, password);
        if (StringUtils.isBlank(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        //将token写入cookie，并指定httpOnly为true，防止通过js获取和篡改
        CookieUtils.setCookie(request, response, properties.getCookieName(), token, properties.getCookieMaxAge() * 60, null, true);
        return ResponseEntity.ok().build();
    }

    /**
     * 验证用户信息   添加刷新token
     *
     * @param token
     * @return
     */
    @GetMapping("verify")
    public ResponseEntity<UserInfo> verifyUser(@CookieValue("LY_TOKEN") String token, HttpServletRequest request, HttpServletResponse response) {
        try {
            //从cookie中解析token信息
            UserInfo userInfo = JwtUtils.getInfoFromToken(token, this.properties.getPublicKey());
            //解析成功要重新刷新token
            token = JwtUtils.generateToken(userInfo, this.properties.getPrivateKey(), this.properties.getExpire());
            //更新Cookie中的token
            CookieUtils.setCookie(request, response, this.properties.getCookieName(), token, this.properties.getCookieMaxAge() * 60);
            //解析成功返回用户信息
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //出现异常则响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}
