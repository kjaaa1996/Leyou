package com.leyou.goods.client;

import com.leyou.item.api.BrandApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 26747
 * @description BrandClient
 * @date 2020/5/28 16:57
 */
@FeignClient(value = "item-service")
public interface BrandClient extends BrandApi {
}
