package com.leyou.goods.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 26747
 * @description SpecificationClient
 * @date 2020/5/28 16:59
 */
@FeignClient(value = "item-service")
public interface SpecificationClient extends SpecificationApi {
}
