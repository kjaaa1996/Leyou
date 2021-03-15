package com.leyou.order.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 26747
 * @description IOrderService
 * @date 2020/7/7 21:22
 */
public interface IOrderService {
    /**
     * 创建新的订单
     *
     * @param order
     * @return
     */
    public Long createOrder(Order order,Boolean seckill);

    /**
     * 根据订单id查询订单
     *
     * @param id
     * @return
     */
    public Order queryById(Long id);

    /**
     * 根据状态分页查询订单
     *
     * @param page
     * @param rows
     * @param status
     * @return
     */
    public PageResult<Order> queryUserOrderList(Integer page, Integer rows, Integer status);

    /**
     * 更新订单状态
     *
     * @param id
     * @param status
     * @return
     */
    public Boolean updateStatus(Long id, Integer status);

    /**
     * 根据订单id查询订单中的sku的id集合
     * 先根据orderId查询到orderDetail，再从orderDetail中获取sku的id集合
     *
     * @param id
     * @return
     */
    public List<Long> querySkuIdByOrderId(Long id);

    /**
     * 根据订单编号查询数据库中的订单状态，并返回
     *
     * @param orderId
     * @return
     */
    public Boolean queryOrderStatus(Long orderId);

    /**
     * 根据订单检查相应的sku是否库存充足，返回不足的skuId集合
     * @param order
     * @param seckill
     * @return
     */
    List<Long> checkStock(Order order, Boolean seckill);
}
