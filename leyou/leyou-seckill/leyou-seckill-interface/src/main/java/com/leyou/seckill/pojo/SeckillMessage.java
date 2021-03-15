package com.leyou.seckill.pojo;

import com.leyou.auth.pojo.UserInfo;

/**
 * @author 26747
 * @description SeckillMessage
 * @date 2020/7/8 20:34
 */
public class SeckillMessage {
    //用户信息
    private UserInfo userInfo;

    //秒杀商品
    private SeckillGoods seckillGoods;

    public SeckillMessage() {
    }

    public SeckillMessage(UserInfo userInfo, SeckillGoods seckillGoods) {
        this.userInfo = userInfo;
        this.seckillGoods = seckillGoods;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public SeckillGoods getSeckillGoods() {
        return seckillGoods;
    }

    public void setSeckillGoods(SeckillGoods seckillGoods) {
        this.seckillGoods = seckillGoods;
    }
}
