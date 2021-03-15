package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import com.leyou.item.service.IGoodsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 26747
 * @description GoodsController
 * @date 2020/5/22 10:28
 */
@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Resource(name = "goodsService")
    private IGoodsService goodsService;

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
    public ResponseEntity<PageResult<SpuBo>> querySpuBoByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "saleable", required = false) Boolean saleable,    //商品上下架，可能未选择
            @RequestParam(value = "sortBy",required = false)String sortBy,
            @RequestParam(value = "desc",required = false)Boolean desc
    ) {
        PageResult<SpuBo> pageResult = goodsService.querySpuBoByPage(key, page, rows, saleable,sortBy,desc);
        //判断分页结果集中数据集items是否为空
        if (CollectionUtils.isEmpty(pageResult.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 新增商品
     *
     * @param spuBo
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveSpuBo(@RequestBody SpuBo spuBo) {
        this.goodsService.saveGoods(spuBo);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新商品
     *
     * @param spuBo
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateSpuBo(@RequestBody SpuBo spuBo) {
        goodsService.updateGoods(spuBo);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据传递的ids，分别处理单个删除还是批量删除
     * @param ids
     * @return
     */
    @DeleteMapping("/spu/{ids}")
    public ResponseEntity<Void> deleteSpuById(@PathVariable("ids")String ids){
        if(ids.contains("-")){
            String[] spuIds=ids.split("-");
            for (String spuId : spuIds) {
                this.goodsService.deleteSpuById(Long.parseLong(spuId));
            }
        }else{
            this.goodsService.deleteSpuById(Long.parseLong(ids));
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 修改相应商品的上下架状态
     * @param id
     * @return
     */
    @PutMapping("/spu/saleable/{id}")
    public ResponseEntity<Void> changeSaleable(@PathVariable("id")Long id){
        this.goodsService.changeSaleable(id);
        return ResponseEntity.ok().build();
    }


    /**
     * 根据spuId查询spuDetail详情
     *
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId") Long spuId) {
        SpuDetail spuDetail = goodsService.querySpuDetailBySpuId(spuId);
        if (spuDetail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据spuId查询所有符合条件的sku
     *
     * @param spuId
     * @return
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> querySkusBySpuId(@RequestParam(name = "id") Long spuId) {
        List<Sku> skuList = goodsService.querySkusBySpuId(spuId);
        if (CollectionUtils.isEmpty(skuList)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(skuList);
    }

    /**
     * 根据id查询spu
     *
     * @param id
     * @return
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<Spu> querySpuById(@PathVariable("id") Long id) {
        Spu spu = this.goodsService.querySpuById(id);
        if (spu == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(spu);

    }

    /**
     * 根据skuId查询sku
     * @param id
     * @return
     */
    @GetMapping("/sku/{id}")
    public ResponseEntity<Sku> querySkuById(@PathVariable("id") Long id) {
        Sku sku = this.goodsService.querySkuById(id);
        if (sku == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(sku);
    }

    /**
     * 根据skuId查询对应的stock信息
     * @param skuId
     * @return
     */
    @GetMapping("/stock/{skuId}")
    public ResponseEntity<Stock> queryStockBySkuId(@PathVariable("skuId")Long skuId){
       Stock stock= this.goodsService.queryStockBySkuId(skuId);
       if(stock==null){
           return ResponseEntity.notFound().build();
       }
       return ResponseEntity.ok(stock);
    }

    /**
     * 更新库存信息
     * @param skuId
     * @param num
     * @param seckill
     * @return
     */
    @PutMapping("/stock")
    public ResponseEntity<Void> updateStockBySkuId(
            @RequestParam(value = "skuId",required = true)Long skuId,
            @RequestParam(value = "num",required = true)Integer num,
            @RequestParam(value = "seckill",defaultValue = "false")Boolean seckill
    ){
        try {
            this.goodsService.updateStockBySkuId(skuId,num,seckill);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.accepted().build();
    }
}
