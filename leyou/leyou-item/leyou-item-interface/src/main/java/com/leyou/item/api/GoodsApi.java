package com.leyou.item.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 26747
 * @description GoodsApi
 * @date 2020/5/25 15:56
 */
@RequestMapping("/goods")
public interface GoodsApi {

    /**
     * 根据关键字分页查询商品表spu
     *
     * @param key
     * @param page
     * @param rows
     * @param saleable
     * @return
     */
    @GetMapping("/spu/page")
    public PageResult<SpuBo> querySpuBoByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,    //商品上下架，可能未选择
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",required = false)Boolean desc
    ) ;

    /**
     * 根据spuId查询spuDetail详情
     *
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{spuId}")
    SpuDetail querySpuDetailBySpuId(@PathVariable("spuId") Long spuId);

    /**
     * 根据spuId查询所有符合条件的sku
     *
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public List<Sku> querySkusBySpuId(@RequestParam(name = "id") Long spuId);

    /**
     * 根据id查询spu
     *
     * @param id
     * @return
     */
    @GetMapping("/spu/{id}")
    public Spu querySpuById(@PathVariable("id") Long id);

    /**
     * 根据skuId查询sku
     * @param id
     * @return
     */
    @GetMapping("/sku/{id}")
    public Sku querySkuById(@PathVariable("id") Long id);

    /**
     * 根据skuId查询对应的stock信息
     * @param skuId
     * @return
     */
    @GetMapping("/stock/{skuId}")
    public Stock queryStockBySkuId(@PathVariable("skuId")Long skuId);

    /**
     * 更新库存信息
     * @param skuId
     * @param num
     * @param seckill
     * @return
     */
    @PutMapping("/stock")
    public Void updateStockBySkuId(
            @RequestParam(value = "skuId",required = true)Long skuId,
            @RequestParam(value = "num",required = true)Integer num,
            @RequestParam(value = "seckill",defaultValue = "false")Boolean seckill
    );
}
