package com.leyou.seckill.service;

import com.leyou.seckill.pojo.SeckillGoods;
import com.leyou.seckill.pojo.SeckillMessage;
import com.leyou.seckill.pojo.SeckillParameter;

import java.util.List;

/**
 * @author 26747
 * @description ISeckillService
 * @date 2020/7/6 16:19
 */
public interface ISeckillService {
    void addSeckillGoods(SeckillParameter seckillParameter);

    List<SeckillGoods> querySeckillGoods(Integer page, Integer rows);

    Long seckillOrder(SeckillGoods seckillGoods);

    void sendMessage(SeckillMessage seckillMessage);

    Long checkSeckillOrder(Long userId);
}
