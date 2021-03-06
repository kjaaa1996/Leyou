package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 26747
 * @description BrandApi
 * @date 2020/5/25 16:00
 */
@RequestMapping("/brand")
public interface BrandApi {

    /**
     * 根据id查询品牌
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Brand queryBrandById(@PathVariable("id") Long id);
}
