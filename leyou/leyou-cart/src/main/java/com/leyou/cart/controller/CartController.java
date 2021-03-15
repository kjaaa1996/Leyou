package com.leyou.cart.controller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.ICartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 26747
 * @description CartController
 * @date 2020/6/8 10:45
 */
@RestController
public class CartController {

    @Resource(name = "cartService")
    private ICartService cartService;

    /**
     * 添加购物车
     *
     * @param cart
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart) {
        this.cartService.addCart(cart);
        return ResponseEntity.ok().build();
    }

    /**
     * 已登录状态下查询购物车
     *
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Cart>> queryCartList() {
        List<Cart> cartList = this.cartService.queryCartList();
        if (CollectionUtils.isEmpty(cartList)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(cartList);
    }

    /**
     * 更新购物车中数量
     *
     * @param cart
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateCartNum(@RequestBody Cart cart) {
        this.cartService.updateCartNum(cart);
        return ResponseEntity.noContent().build();
    }

    /**
     * 根据skuId删除购物车中的sku
     *
     * @param skuId
     * @return
     */
    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") String skuId) {
        this.cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }
}
