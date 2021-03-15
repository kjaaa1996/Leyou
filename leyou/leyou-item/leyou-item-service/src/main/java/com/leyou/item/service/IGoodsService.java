package com.leyou.item.service;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;

import java.util.List;

/**
 * @author 26747
 * @description IGoodsService
 * @date 2020/5/22 10:30
 */
public interface IGoodsService {
    PageResult<SpuBo> querySpuBoByPage(String key, Integer page, Integer rows, Boolean saleable,String sortBy,Boolean desc);

    void saveGoods(SpuBo spuBo);

    SpuDetail querySpuDetailBySpuId(Long spuId);

    List<Sku> querySkusBySpuId(Long spuId);

    void updateGoods(SpuBo spuBo);

    Spu querySpuById(Long id);

    Sku querySkuById(Long id);

    void deleteSpuById(Long spuId);

    void changeSaleable(Long id);

    Stock queryStockBySkuId(Long skuId);

    void updateStockBySkuId(Long skuId, Integer num, Boolean seckill) throws NullPointerException;

//    void sendMsg(String type, Long id);
}
