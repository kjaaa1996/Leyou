package com.leyou.order.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 26747
 * @description GoodsClient
 * @date 2020/7/7 21:20
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
