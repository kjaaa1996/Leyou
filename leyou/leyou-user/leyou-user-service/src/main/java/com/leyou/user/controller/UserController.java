package com.leyou.user.controller;

import com.leyou.user.pojo.User;
import com.leyou.user.service.IUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author 26747
 * @description UserController
 * @date 2020/6/2 16:28
 */
@Controller
public class UserController {

    @Resource(name = "userService")
    private IUserService userService;

    /**
     * 检查数据(手机号，用户名)是否可用
     *
     * @param data
     * @param type
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkUser(@PathVariable("data") String data, @PathVariable("type") Integer type) {
        Boolean bool = this.userService.checkUser(data, type);
        if (bool == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bool);
    }

    /**
     * 发送手机验证码
     *
     * @param phone
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendVerifyCode(@RequestParam("phone") String phone) {
        Boolean boo = this.userService.sendVerifyCode(phone);
        //如果返回值我为null或false(发送短信失败)
        if (boo == null || !boo) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 用户注册，201注册成功，400参数错误，500服务器异常
     *
     * @param user
     * @param code
     * @return
     */
    @PostMapping("/register")
    public ResponseEntity<Void> userRegister(@Valid User user, @RequestParam(value = "code") String code) {
        Boolean bool = this.userService.userRegister(user, code);
        if (bool == null || !bool) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据传递的用户名和密码查询数据库中的用户信息
     *
     * @param username
     * @param password
     * @return
     */
    @GetMapping("/query")
    @ResponseBody
    public ResponseEntity<User> queryUser(@RequestParam("username") String username, @RequestParam("password") String password) {
        User user = this.userService.queryUser(username, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(user);
    }
}
