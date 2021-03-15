package com.leyou.cart.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 26747
 * @description GoodsClient
 * @date 2020/6/8 11:13
 */
@FeignClient("item-service")
public interface GoodsClient extends GoodsApi {
}
