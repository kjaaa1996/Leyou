package com.leyou.search.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leyou.search.service.ISearchService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author 26747
 * @description SearchListener
 * @date 2020/6/2 14:48
 */
@Component("searchListener")
public class SearchListener {

    @Resource(name = "searchService")
    private ISearchService searchService;

    /**
     * 添加和更新
     *
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SEARCH.SAVE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.insert", "item.update"}
    ))
    public void save(Long id) throws JsonProcessingException {
        if (id == null) {
            return;
        }
        this.searchService.save(id);
    }

    /**
     * 删除
     *
     * @param id
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "LEYOU.SEARCH.DELETE.QUEUE", durable = "true"),
            exchange = @Exchange(value = "LEYOU.ITEM.EXCHANGE", ignoreDeclarationExceptions = "true", type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void delete(Long id) throws JsonProcessingException {
        if (id == null) {
            return;
        }
        this.searchService.delete(id);
    }

}
