package com.leyou.cart.service;

import com.leyou.cart.pojo.Cart;

import java.util.List;

/**
 * @author 26747
 * @description ICartService
 * @date 2020/6/8 10:46
 */
public interface ICartService {
    void addCart(Cart cart);

    List<Cart> queryCartList();

    void updateCartNum(Cart cart);

    void deleteCart(String skuId);
}
