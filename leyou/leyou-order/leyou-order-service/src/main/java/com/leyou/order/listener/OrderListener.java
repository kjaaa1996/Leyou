package com.leyou.order.listener;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Stock;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.mapper.SeckillOrderMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.SeckillOrder;
import com.leyou.order.service.IOrderService;
import com.leyou.seckill.pojo.SeckillGoods;
import com.leyou.seckill.pojo.SeckillMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @author 26747
 * @description OrderListener
 * @date 2020/7/9 15:04
 */
@Component(value = "orderListener")
public class OrderListener {

    @Resource
    private GoodsClient goodsClient;

    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    @Resource(name = "orderService")
    private IOrderService orderService;

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderListener.class);


    @RabbitListener(bindings = @QueueBinding(
            //定义队列名，进行持久化
            value = @Queue(value = "LEYOU.ORDER.SECKILL.QUEUE", durable = "true"),
            //定义交换机，默认持久化，忽略同名异常，类型为topic
            exchange = @Exchange(value = "LEYOU.ORDER.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            //定义 routing key为String[]类型
            key = {"order.seckill"}
    ))
    public void SeckillLitener(String seckill) {
        SeckillMessage seckillMessage = JsonUtils.parse(seckill, SeckillMessage.class);
        UserInfo userInfo = seckillMessage.getUserInfo();
        SeckillGoods seckillGoods = seckillMessage.getSeckillGoods();

        //1.判断库存是否充足
        Stock stock = this.goodsClient.queryStockBySkuId(seckillGoods.getSkuId());
        if (stock == null || stock.getStock() <= 0 || stock.getSeckillStock() <= 0) {
            LOGGER.error("库存不足");
        }
        //2.查看秒杀订单表中是否已经有当前用户秒杀当前商品的记录
        Example example = new Example(SeckillOrder.class);
        example.createCriteria().andEqualTo("user_id", userInfo.getId()).andEqualTo("sku_id", seckillGoods.getSkuId());
        List<SeckillOrder> seckillOrders = this.seckillOrderMapper.selectByExample(example);
        if(!CollectionUtils.isEmpty(seckillOrders)){
            LOGGER.error("已经秒杀过当前商品");
        }
        //3.下订单
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
        orderDetail.setOwnSpec(this.goodsClient.querySkuById(seckillGoods.getSkuId()).getOwnSpec());
        order.setOrderDetails(Arrays.asList(orderDetail));
        this.orderService.createOrder(order, true);
    }
}
