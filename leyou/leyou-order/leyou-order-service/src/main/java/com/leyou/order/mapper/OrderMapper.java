package com.leyou.order.mapper;

import com.leyou.order.pojo.Order;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderMapper extends Mapper<Order> {

    /**
     * 根据状态分页查询订单
     * 具体的sql语句在/resources/mapper/OrderMapper.xml中
     *
     * @param userId
     * @param status
     * @return
     */
    List<Order> queryOrderList(
            @Param("userId") Long userId,
            @Param("status") Integer status);

}
