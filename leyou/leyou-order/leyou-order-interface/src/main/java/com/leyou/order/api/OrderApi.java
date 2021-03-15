package com.leyou.order.api;

import com.leyou.common.pojo.PageResult;
import com.leyou.order.pojo.Order;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author 26747
 * @description OrderApi
 * @date 2020/7/6 20:53
 */
@RequestMapping("/order")
public interface OrderApi {
    /**
     * 创建订单
     *
     * @param order 订单对象
     * @return 订单编号
     */
    @PostMapping
    public Long createOrder(@RequestBody @Valid Order order, @RequestParam("seckill") Boolean seckill);

    /**
     * 根据订单编号查询订单
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public Order queryOrderById(@PathVariable("id") Long id);

    /**
     * 分页查询当前用户订单
     *
     * @param status 订单状态
     * @return 分页订单数据
     */
    @GetMapping("list")
    public PageResult<Order> queryUserOrderList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "status", required = false) Integer status);

    /**
     * 查询付款状态
     *
     * @param orderId
     * @return 0, 状态查询失败 1,支付成功 2,支付失败
     */
    @GetMapping("state/{id}")
    public Integer queryPayState(@PathVariable("id") Long orderId);

    /**
     * 根据订单id查询订单中的sku的id集合
     * 先根据orderId查询到orderDetail，再从orderDetail中获取sku的id集合
     *
     * @param id
     * @return
     */
    @GetMapping("/skuIds/{id}")
    public List<Long> querySkuByOrderId(@PathVariable("id") Long id);
}
