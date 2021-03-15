package com.leyou.search.client;

import com.leyou.item.api.SpecificationApi;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @author 26747
 * @description SpecificationClient
 * @date 2020/5/25 16:10
 */
@FeignClient(value = "item-service")
public interface SpecificationClient extends SpecificationApi {
}
