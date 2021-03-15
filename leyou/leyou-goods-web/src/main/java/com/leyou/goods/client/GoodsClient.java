package com.leyou.goods.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 26747
 * @description GoodsClient
 * @date 2020/5/28 16:58
 */
@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {
}
