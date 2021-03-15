package com.leyou.search.client;

import com.leyou.item.api.CategoryApi;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 26747
 * @description CategoryApi
 * @date 2020/5/25 15:59
 */
@FeignClient(value = "item-service")
public interface CategoryClient extends CategoryApi {

}
