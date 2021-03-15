package com.leyou.cart.service.impl;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.cart.client.GoodsClient;
import com.leyou.cart.interceptor.LoginInterceptor;
import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.ICartService;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.pojo.Sku;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 26747
 * @description CartServiceImpl
 * @date 2020/6/8 10:46
 */
@Service("cartService")
public class CartServiceImpl implements ICartService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private GoodsClient goodsClient;

    static final String KEY_PREFIX = "leyou:cart:uid:";


    /**
     * 添加购物车
     * 1.查询之前的购物车数据
     * 2.判断添加的商品是否存在
     * 2.1存在直接修改数量写回redis
     * 2.2不存在，新加数据，写回redis
     *
     * @param cart
     */
    @Override
    public void addCart(Cart cart) {
        //获取登录用户
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //获取Redis的key
        String key = KEY_PREFIX + userInfo.getId();
        //获取Hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
        //查询是否存在
        Long skuId = cart.getSkuId();
        Integer num = cart.getNum();
        Boolean boo = hashOps.hasKey(skuId.toString());
        if (boo == null) {
            return;
        }
        if (boo) {
            //存在获取购物车数量
            Object obj = hashOps.get(skuId.toString());
            if (obj == null) {
                return;
            }
            String json = obj.toString();
            cart = JsonUtils.parse(json, Cart.class);
            //修改购物车数量
            if (cart == null) {
                return;
            }
            cart.setNum(cart.getNum() + num);
        } else {
            //不存在，新增购物车数据
            cart.setUserId(userInfo.getId());
            //其他商品信息需要根据传递的skuId查询商品服务获取
            Sku sku = this.goodsClient.querySkuById(skuId);
            cart.setTitle(sku.getTitle());
            cart.setPrice(sku.getPrice());
            cart.setOwnSpec(sku.getOwnSpec());
            cart.setImage(StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
        }
        //将购物车数据写入redis
        String cartStr = JsonUtils.serialize(cart);
        if (cartStr == null) {
            return;
        }
        hashOps.put(cart.getSkuId().toString(), cartStr);
    }

    /**
     * 已登录状态下查询购物车
     *
     * @return
     */
    @Override
    public List<Cart> queryCartList() {
        //获取登录用户
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //判断是否存在购物车
        String key = KEY_PREFIX + userInfo.getId();
        Boolean bool = this.stringRedisTemplate.hasKey(key);
        if (bool == null) {
            return null;
        }
        if (!bool) {
            //不存在购物车，直接返回
            return null;
        }
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
        //获取字符串集合
        List<Object> cartList = hashOps.values();
        //判断是否存在数据
        if (CollectionUtils.isEmpty(cartList)) {
            return null;
        }
        //将List<Object>集合转化为List<Cart>集合并返回
        return cartList.stream().map(obj -> JsonUtils.parse(obj.toString(), Cart.class)).collect(Collectors.toList());
    }

    /**
     * 更新购物车商品数量
     *
     * @param cart
     */
    @Override
    public void updateCartNum(Cart cart) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        //获取购物车信息
        String key = KEY_PREFIX + userInfo.getId();
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
        Long skuId = cart.getSkuId();
        Object obj = hashOps.get(skuId.toString());
        if (obj == null) {
            return;
        }
        String cartJson = obj.toString();
        Cart parse = JsonUtils.parse(cartJson, Cart.class);
        if (parse == null) {
            return;
        }
        //更新购物车信息
        parse.setNum(cart.getNum());

        String parseStr = JsonUtils.serialize(parse);
        if (parseStr == null) {
            return;
        }
        //写入redis
        hashOps.put(skuId.toString(), parseStr);
    }

    /**
     * 根据skuId删除sku
     *
     * @param skuId
     */
    @Override
    public void deleteCart(String skuId) {
        //获取用户信息
        UserInfo userInfo = LoginInterceptor.getUserInfo();
        String key = KEY_PREFIX + userInfo.getId();
        //获取购物车信息
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(key);
        //删除sku信息
        hashOps.delete(skuId);
    }
}
