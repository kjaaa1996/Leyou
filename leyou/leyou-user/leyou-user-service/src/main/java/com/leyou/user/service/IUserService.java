package com.leyou.user.service;

import com.leyou.user.pojo.User;

/**
 * @author 26747
 * @description IUserService
 * @date 2020/6/2 16:26
 */
public interface IUserService {
    Boolean checkUser(String data, Integer type);

    Boolean sendVerifyCode(String phone);

    Boolean userRegister(User user, String code);

    User queryUser(String username, String password);
}
