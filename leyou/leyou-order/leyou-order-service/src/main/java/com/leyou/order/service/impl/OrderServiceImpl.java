package com.leyou.order.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.leyou.auth.pojo.UserInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.IdWorker;
import com.leyou.item.pojo.Stock;
import com.leyou.order.client.GoodsClient;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.mapper.OrderDetailMapper;
import com.leyou.order.mapper.OrderMapper;
import com.leyou.order.mapper.OrderStatusMapper;
import com.leyou.order.mapper.SeckillOrderMapper;
import com.leyou.order.pojo.Order;
import com.leyou.order.pojo.OrderDetail;
import com.leyou.order.pojo.OrderStatus;
import com.leyou.order.pojo.SeckillOrder;
import com.leyou.order.service.IOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("orderService")
public class OrderServiceImpl implements IOrderService {

    @Resource
    private IdWorker idWorker;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderDetailMapper detailMapper;

    @Resource
    private OrderStatusMapper statusMapper;

    @Resource
    private GoodsClient goodsClient;

    @Resource
    private SeckillOrderMapper seckillOrderMapper;


    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    /**
     * 创建新的订单
     *
     * @param order
     * @return
     */
    @Transactional
    @Override
    public Long createOrder(Order order, Boolean seckill) {
        // 生成orderId
        long orderId = idWorker.nextId();
        // 获取登录用户
        UserInfo user = LoginInterceptor.getLoginUser();
        // 初始化数据
        order.setBuyerNick(user.getUsername());
        order.setBuyerRate(false);
        order.setCreateTime(new Date());
        order.setOrderId(orderId);
        order.setUserId(user.getId());
        // 保存数据
        this.orderMapper.insertSelective(order);

        // 保存订单状态
        OrderStatus orderStatus = new OrderStatus();
        orderStatus.setOrderId(orderId);
        orderStatus.setCreateTime(order.getCreateTime());
        orderStatus.setStatus(1);// 初始状态为未付款

        this.statusMapper.insertSelective(orderStatus);

        // 订单详情中添加orderId
        order.getOrderDetails().forEach(orderDetail -> {
            orderDetail.setOrderId(orderId);
            //更新秒杀库存信息
            this.goodsClient.updateStockBySkuId(orderDetail.getSkuId(), orderDetail.getNum(), seckill);
            if (seckill) {
                SeckillOrder seckillOrder = new SeckillOrder();
                seckillOrder.setId(null);
                seckillOrder.setOrderId(orderId);
                seckillOrder.setUserId(user.getId());
                this.seckillOrderMapper.insertSelective(seckillOrder);
            }
        });
        // 保存订单详情,使用批量插入功能
        this.detailMapper.insertList(order.getOrderDetails());

        logger.debug("生成订单，订单编号：{}，用户id：{}", orderId, user.getId());

        return orderId;
    }

    /**
     * 根据订单检查相应的sku是否库存充足，返回不足的skuId集合
     *
     * @param order
     * @param seckill
     * @return
     */
    @Override
    public List<Long> checkStock(Order order, Boolean seckill) throws NullPointerException {
        List<OrderDetail> orderDetailList = order.getOrderDetails();
        List<Long> skuIdList = new ArrayList<>();
        orderDetailList.forEach(orderDetail -> {
            Stock stock = this.goodsClient.queryStockBySkuId(orderDetail.getSkuId());
            if (stock.getStock() < orderDetail.getNum()) {    //正常的订单库存不足
                skuIdList.add(orderDetail.getSkuId());
            } else {  //正常库存充足，查看秒杀库存
                if (seckill && stock.getSeckillStock() < orderDetail.getNum()) {
                    skuIdList.add(orderDetail.getSkuId());
                }
            }
        });
        return skuIdList;
    }

    /**
     * 根据订单id查询订单
     *
     * @param id
     * @return
     */
    @Override
    public Order queryById(Long id) {
        // 查询订单
        Order order = this.orderMapper.selectByPrimaryKey(id);

        // 查询订单详情
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(id);
        List<OrderDetail> details = this.detailMapper.select(detail);
        order.setOrderDetails(details);

        // 查询订单状态
        OrderStatus status = this.statusMapper.selectByPrimaryKey(order.getOrderId());
        order.setOrderStatus(status);
        order.setStatus(status.getStatus());
        return order;
    }

    /**
     * 根据状态分页查询订单
     *
     * @param page
     * @param rows
     * @param status
     * @return
     */
    @Override
    public PageResult<Order> queryUserOrderList(Integer page, Integer rows, Integer status) {
        try {
            // 分页
            PageHelper.startPage(page, rows);
            // 获取登录用户
            UserInfo user = LoginInterceptor.getLoginUser();
            // 创建查询条件
            Page<Order> pageInfo = (Page<Order>) this.orderMapper.queryOrderList(user.getId(), status);
            return new PageResult<>(pageInfo.getTotal(), pageInfo.getPages(), pageInfo);
        } catch (Exception e) {
            logger.error("查询订单出错", e);
            return null;
        }
    }

    /**
     * 更新订单状态
     *
     * @param id
     * @param status
     * @return
     */
    @Transactional
    @Override
    public Boolean updateStatus(Long id, Integer status) {
        OrderStatus record = new OrderStatus();
        record.setOrderId(id);
        record.setStatus(status);
        // 根据状态判断要修改的时间
        switch (status) {
            case 2:
                record.setPaymentTime(new Date());// 付款
                break;
            case 3:
                record.setConsignTime(new Date());// 发货
                break;
            case 4:
                record.setEndTime(new Date());// 确认收获，订单结束
                break;
            case 5:
                record.setCloseTime(new Date());// 交易失败，订单关闭
                break;
            case 6:
                record.setCommentTime(new Date());// 评价时间
                break;
            default:
                return null;
        }
        int count = this.statusMapper.updateByPrimaryKeySelective(record);
        return count == 1;
    }

    /**
     * 根据订单id查询订单中的sku的id集合
     * 先根据orderId查询到orderDetail，再从orderDetail中获取sku的id集合
     *
     * @param id
     * @return
     */
    @Override
    public List<Long> querySkuIdByOrderId(Long id) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.setOrderId(id);
        //获取订单对应的订单详情
        List<OrderDetail> orderDetails = this.detailMapper.select(orderDetail);
        //返回sku的ID集合
        return orderDetails.stream().map(OrderDetail::getSkuId).collect(Collectors.toList());
    }

    /**
     * 根据订单编号查询数据库中的订单状态，并返回
     *
     * @param orderId
     * @return
     */
    @Override
    public Boolean queryOrderStatus(Long orderId) {
        OrderStatus order = new OrderStatus();
        order.setOrderId(orderId);

        OrderStatus orderStatus = this.statusMapper.selectOne(order);
        if (orderStatus == null) {
            return null;
        }
        if (orderStatus.getStatus() == 1) {
            orderStatus.setStatus(2);
            orderStatus.setPaymentTime(new Date());
        }
        return true;
    }
}
