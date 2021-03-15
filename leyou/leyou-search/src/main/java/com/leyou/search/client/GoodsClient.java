package com.leyou.search.client;

import com.leyou.item.api.GoodsApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 26747
 * @description GoodsClient
 * @date 2020/5/25 16:08
 */
@FeignClient(value = "item-service")
public interface GoodsClient extends GoodsApi {
}
