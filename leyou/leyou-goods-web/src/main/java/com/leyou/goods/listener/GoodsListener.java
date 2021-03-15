package com.leyou.goods.listener;

import com.leyou.goods.service.IGoodsHtmlService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 26747
 * @description GoodsListener
 * @date 2020/6/2 11:43
 */
@Component("goodsListener")
public class GoodsListener {

    @Resource(name = "goodsHtmlService")
    private IGoodsHtmlService goodsHtmlService;

    /**
     * 新增和更新都可使用
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            //定义队列名，进行持久化
            value = @Queue(value = "LEYOU.ITEM.SAVE.QUEUE", durable = "true"),
            //定义交换机，默认持久化，忽略同名异常，类型为topic
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            //定义 routing key为String[]类型
            key = {"item.insert", "item.update"}
    ))
    public void save(Long id) {
        if (id == null) {
            return;
        }
        this.goodsHtmlService.createHtml(id);
    }

    /**
     * 删除html页面
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            //定义队列名，进行持久化
            value = @Queue(value = "LEYOU.ITEM.DELETE.QUEUE", durable = "true"),
            //定义交换机，默认持久化，忽略同名异常，类型为topic
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            //定义 routing key为String[]类型
            key = {"item.delete"}
    ))
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        this.goodsHtmlService.deleteHtml(id);
    }
}
