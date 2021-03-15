package com.leyou.user.service.impl;

import com.leyou.common.utils.NumberUtils;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.service.IUserService;
import com.leyou.user.utils.CodecUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author 26747
 * @description UserServiceImpl
 * @date 2020/6/2 16:27
 */
@Service("userService")
public class UserServiceImpl implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    //存入redis的关键字前缀
    static final String KEY_PREFIX = "user:code:phone:";

    static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    /**
     * 检查用户名和手机号是否可用
     *
     * @param data
     * @param type
     * @return
     */
    @Override
    public Boolean checkUser(String data, Integer type) {
        User user = new User();
        switch (type) {
            case 1:
                user.setUsername(data);
                break;
            case 2:
                user.setPhone(data);
                break;
            default:
                return null;
        }
        return this.userMapper.selectCount(user) == 0;
    }

    /**
     * 发送验证码
     *
     * @param phone
     * @return
     */
    @Override
    public Boolean sendVerifyCode(String phone) {
        if (StringUtils.isBlank(phone))
            return null;
        //生成6位验证码，不会以0开头
        String code = NumberUtils.generateCode(6);
        try {
            //发送短信
            Map<String, String> msg = new HashMap<>();
            msg.put("phone", phone);
            msg.put("code", code);
            //发送消息给sms微服务
            this.amqpTemplate.convertAndSend("LEYOU.SMS.EXCHANGE", "sms.verify.code", msg);
            //将code存入redis,设定key为前缀+phone，value为code，存活时间为5分钟
            this.stringRedisTemplate.opsForValue().set(KEY_PREFIX + phone, code, 5, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {

            logger.error("发送短信失败.phone:{},code:{}", phone, code);
            return false;
        }
    }

    /**
     * 用户注册
     *
     * @param user
     * @param code
     * @return
     */
    @Override
    public Boolean userRegister(User user, String code) {
        //1.校验验证码   获取校验码cacheCode
        String cacheCode = this.stringRedisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());
        if (!StringUtils.equals(code, cacheCode)) {
            return false;
        }
        //2.生成盐
        String salt = CodecUtils.generateSalt();
        user.setSalt(salt);

        //3.对密码进行加密
        user.setPassword(CodecUtils.md5Hex(user.getPassword(), salt));

        //4.写入数据库
        user.setId(null);
        user.setCreated(new Date());
        boolean b = this.userMapper.insertSelective(user) == 1;

        //5.删除redis中的验证码
        if (b) {
            this.stringRedisTemplate.delete(KEY_PREFIX + user.getPhone());
        }

        return b;
    }

    /**
     * 根据用户名和密码查询用户信息
     *
     * @param username
     * @param password
     * @return
     */
    @Override
    public User queryUser(String username, String password) {
        //1.找到数据库中用户名为username的用户
        User record = new User();
        record.setUsername(username);
        User user = this.userMapper.selectOne(record);
        if (user == null) {
            return null;
        }
        //获取他的盐值salt
        String salt = user.getSalt();
        //将数据库中存储的密码和加密后的password进行比较
        if (!StringUtils.equals(user.getPassword(), CodecUtils.md5Hex(password, salt))) {
            return null;
        }
        return user;
    }
}
