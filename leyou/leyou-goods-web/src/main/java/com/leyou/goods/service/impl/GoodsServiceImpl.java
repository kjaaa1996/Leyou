package com.leyou.goods.service.impl;

import com.leyou.goods.client.BrandClient;
import com.leyou.goods.client.CategoryClient;
import com.leyou.goods.client.GoodsClient;
import com.leyou.goods.client.SpecificationClient;
import com.leyou.goods.service.IGoodsService;
import com.leyou.item.pojo.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 26747
 * @description GoodsServiceImpl
 * @date 2020/5/28 16:41
 */
@Service("goodsService")
public class GoodsServiceImpl implements IGoodsService {
    @Resource
    private CategoryClient categoryClient;
    @Resource
    private BrandClient brandClient;
    @Resource
    private GoodsClient goodsClient;
    @Resource
    private SpecificationClient specificationClient;

    /**
     * 根据spuId获取商品详情
     *
     * @param spuId
     * @return
     */
    public Map<String, Object> loadData(Long spuId) {
        Map<String, Object> model = new HashMap<>();
        //根据spu的id查询spu
        Spu spu = this.goodsClient.querySpuById(spuId);

        //查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spuId);

        //查询分类Map<String,Object>
        List<Long> cids = Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3());
        List<String> names = this.categoryClient.queryNamesByIds(cids);
        //初始化一个分类map
        List<Map<String, Object>> categories = new ArrayList<>();
        for (int i = 0; i < cids.size(); i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", cids.get(i));
            map.put("name", names.get(i));
            categories.add(map);
        }

        //查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());

        //查询skus
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spuId);

        //查询参数数组
        List<SpecGroup> groups = this.specificationClient.queryGroupsWithParam(spu.getCid3());

        //查询特殊的规格参数,通用属性为false
        List<SpecParam> specialParams = this.specificationClient.queryParams(null, spu.getCid3(), false, null);

        //查询通用的规格参数，通用属性为true
        List<SpecParam> genericParams = this.specificationClient.queryParams(null, spu.getCid3(), true, null);

        //初始化特殊参数map
        Map<Long, String> specialParamMap = new HashMap<>();
        specialParams.forEach(param -> {
            specialParamMap.put(param.getId(), param.getName());
        });

        //初始化通用参数map
        Map<Long, String> genericParamMap = new HashMap<>();
        genericParams.forEach(param -> {
            genericParamMap.put(param.getId(), param.getName());
        });


        model.put("spu", spu);
        model.put("spuDetail", spuDetail);
        model.put("skus", skus);
        model.put("brand", brand);
        model.put("categories", categories);
        model.put("groups", groups);
        model.put("specialParamMap", specialParamMap);
        model.put("genericParamMap", genericParamMap);

        return model;
    }

}
