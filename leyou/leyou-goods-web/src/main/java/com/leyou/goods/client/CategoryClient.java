package com.leyou.goods.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 26747
 * @description CategoryClient
 * @date 2020/5/28 16:58
 */
@FeignClient(value = "item-service")
public interface CategoryClient extends CategoryApi {
}
