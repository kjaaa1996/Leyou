package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;

import java.util.List;

/**
 * @author 26747
 * @description IBrandService
 * @date 2020/5/15 21:28
 */
public interface IBrandService {
    PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, boolean desc);

    //一般不在接口上写注解，因为实现类可能有多个，但不是每个实现类都需要注解
    void saveBrand(Brand brand, List<Long> cids);

    List<Brand> queryBrandsByCid(Long cid);

    Brand queryBrandById(Long id);

    void updateBrand(Brand brand,Long cids);

    void deleteBrand(String bid);
}
