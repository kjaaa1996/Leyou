package com.leyou.seckill.client;


import com.leyou.order.api.OrderApi;
import com.leyou.seckill.config.OrderConfig;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 26747
 * @description OrderClient
 * @date 2020/7/6 20:55
 */
@FeignClient(value = "order-service",configuration = OrderConfig.class)
public interface OrderClient extends OrderApi {
}
