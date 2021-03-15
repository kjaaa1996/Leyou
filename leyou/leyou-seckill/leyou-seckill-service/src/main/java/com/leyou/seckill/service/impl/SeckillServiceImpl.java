package com.leyou.seckill.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Stock;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.SeckillOrder;
import com.leyou.seckill.client.OrderClient;
import com.leyou.seckill.mapper.SeckillMapper;
import com.leyou.seckill.mapper.SkuMapper;
import com.leyou.seckill.mapper.StockMapper;
import com.leyou.seckill.pojo.SeckillGoods;
import com.leyou.seckill.pojo.SeckillMessage;
import com.leyou.seckill.pojo.SeckillParameter;
import com.leyou.seckill.service.ISeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author 26747
 * @description SeckillServiceImpl
 * @date 2020/7/6 16:19
 */
@Service("seckillService")
public class SeckillServiceImpl implements ISeckillService {

    @Resource
    private SeckillMapper seckillMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private OrderClient orderClient;

    @Resource
    private AmqpTemplate amqpTemplate;

    private static final Logger LOGGER = LoggerFactory.getLogger(SeckillServiceImpl.class);

    /**
     * 新增秒杀商品
     *
     * @param seckillParameter
     */
    @Transactional
    @Override
    public void addSeckillGoods(SeckillParameter seckillParameter) {
        Calendar calendar = Calendar.getInstance();   //使用calendar日历工具类获取当前时间，可以自动进行时间的换算
        calendar.setTime(new Date());
        seckillParameter.setStartTime(calendar.getTime());//将当前时间设为秒杀开始时间，点击秒杀即开始
        calendar.add(Calendar.HOUR, 2);//将calendar增加两个小时
        seckillParameter.setEndTime(calendar.getTime());//将修改后的时间作为结束时间，即秒杀持续2个小时

        //1.根据seckillParameter的id查询sku
        Sku sku = this.skuMapper.selectByPrimaryKey(seckillParameter.getId());
        //2.添加到秒杀商品表中
        SeckillGoods seckillGoods = new SeckillGoods();
        seckillGoods.setSkuId(sku.getId());
        seckillGoods.setTitle(sku.getTitle());
        seckillGoods.setEnable(true);
        seckillGoods.setEndTime(seckillParameter.getEndTime());
        seckillGoods.setImage(sku.getImages());
        seckillGoods.setSeckillPrice((long) (sku.getPrice() * seckillParameter.getDiscount()));
        seckillGoods.setSeckillTotal(seckillParameter.getCount());
        seckillGoods.setStartTime(seckillParameter.getStartTime());
        this.seckillMapper.insertSelective(seckillGoods);
        //3.更新对应的库存信息tb_stock
        Stock stock = this.stockMapper.selectByPrimaryKey(sku.getId());
        //可秒杀库存为原来的可秒杀库存+此次设置的秒杀库存
        stock.setSeckillStock((stock.getSeckillStock() == null ? 0 : stock.getSeckillStock()) + seckillParameter.getCount());
        //可秒杀总数为原来的可秒杀总数+此次设置的秒杀库存
        stock.setSeckillTotal((stock.getSeckillTotal() == null ? 0 : stock.getSeckillTotal()) + seckillParameter.getCount());
        //商品库存为原来的商品库存-此次设置的秒杀库存
        stock.setStock(stock.getStock() - seckillParameter.getCount());
        this.stockMapper.updateByPrimaryKey(stock);
    }

    /**
     * 分页查询秒杀商品列表
     *
     * @param page
     * @param rows
     * @return
     */
    @Override
    public List<SeckillGoods> querySeckillGoods(Integer page, Integer rows) {
        //开始分页
        PageHelper.startPage(page, rows);
        Example example = new Example(SeckillGoods.class);
        example.createCriteria().andEqualTo("enable", true);
        return (Page<SeckillGoods>) this.seckillMapper.selectByExample(example);
    }

    /**
     * 秒杀商品并返回创建的订单id（此时暂时不考虑地址的问题，直接使用默认地址）,用户只能秒杀一件
     *
     * @param seckillGoods
     * @return
     */
    @Override
    public Long seckillOrder(SeckillGoods seckillGoods) {
        //调用查询默认地址的接口，根据当前登录的用户查询其默认地址
        Order order = new Order();
        order.setPaymentType(1);
        order.setTotalPay(seckillGoods.getSeckillPrice());
        order.setActualPay(seckillGoods.getSeckillPrice());
        order.setPostFee(0 + "");
        order.setReceiver("李四");
        order.setReceiverMobile("15812312312");
        order.setReceiverCity("西安");
        order.setReceiverDistrict("碑林区");
        order.setReceiverState("陕西");
        order.setReceiverZip("000000000");
        order.setInvoiceType(0);
        order.setSourceType(2);

        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setSkuId(seckillGoods.getSkuId());
        orderDetail.setNum(1);
        orderDetail.setTitle(seckillGoods.getTitle());
        orderDetail.setImage(seckillGoods.getImage());
        orderDetail.setPrice(seckillGoods.getSeckillPrice());
        orderDetail.setOwnSpec(this.skuMapper.selectByPrimaryKey(seckillGoods.getSkuId()).getOwnSpec());
        order.setOrderDetails(Arrays.asList(orderDetail));
        return this.orderClient.createOrder(order, true);
    }

    /**
     * 发送信息到秒杀队列中
     *
     * @param seckillMessage
     */
    @Override
    public void sendMessage(SeckillMessage seckillMessage) {
        String json = JsonUtils.serialize(seckillMessage);
        try {
            this.amqpTemplate.convertAndSend("order.seckill", json);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("秒杀消息发送异常", seckillMessage.getSeckillGoods().getSkuId(), e);
        }
    }

    /**
     * 根据userId查询订单号
     *
     * @param userId
     * @return
     */
    @Override
    public Long checkSeckillOrder(Long userId) {
        Example example = new Example(SeckillOrder.class);
        example.createCriteria().andEqualTo("userId", userId);
        List<SeckillGoods> seckillGoods = this.seckillMapper.selectByExample(example);
        if (seckillGoods == null || seckillGoods.size() == 0) {
            return null;
        }
        return seckillGoods.get(0).getId();
    }
}
