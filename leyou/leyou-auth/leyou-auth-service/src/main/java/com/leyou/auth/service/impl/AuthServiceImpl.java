package com.leyou.auth.service.impl;

import com.leyou.auth.client.UserClient;
import com.leyou.auth.config.JwtProperties;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.auth.service.IAuthService;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.user.pojo.User;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author 26747
 * @description AuthServiceImpl
 * @date 2020/6/5 11:26
 */
@Service("authService")
public class AuthServiceImpl implements IAuthService {

    @Resource
    private UserClient userClient;

    @Resource
    private JwtProperties properties;

    /**
     * 登录授权
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public String accredit(String username, String password) {
        try {
            //调用微服务，执行查询
            User user = this.userClient.queryUser(username, password);
            //如果查询结果为null，这直接返回null
            if (user == null) {
                return null;
            }
            //如果有查询结果，则通过JwtUtils生成token，并返回
            String token = JwtUtils.generateToken(new UserInfo(user.getId(), user.getUsername()), properties.getPrivateKey(), properties.getExpire());
            return token;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
