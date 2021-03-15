package com.leyou.user.api;

import com.leyou.user.pojo.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author 26747
 * @description UserApi
 * @date 2020/6/5 15:19
 */
public interface UserApi {

    /**
     * 根据传递的用户名和密码查询数据库中的用户信息
     *
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    @ResponseBody
    public User queryUser(@RequestParam("username") String username, @RequestParam("password") String password);

}
