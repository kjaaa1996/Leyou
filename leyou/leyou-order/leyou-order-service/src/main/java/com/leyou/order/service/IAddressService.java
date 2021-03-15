package com.leyou.order.service;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.pojo.Address;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author 26747
 * @description IAddressService
 * @date 2020/7/7 21:22
 */
public interface IAddressService {
    /**
     * 根据当前登录的用户查询所有的收货地址记录
     *
     * @return
     */
    public List<Address> queryAddresses();

    /**
     * 根据登录的用户添加新的收货地址
     *
     * @param address
     */
    public void addAddress(Address address);

    /**
     * 更新地址信息
     *
     * @param address
     */
    public void updateAddress(Address address);

    /**
     * 删除地址
     *
     * @param id
     */
    public void deleteAddress(Long id);

    /**
     * 查询地址
     *
     * @param id
     * @return
     */
    public Address queryAddressById(Long id);

    /**
     * 将其他地址设为非默认地址
     */
    public void setNotDefault();

    /**
     * 查询当前登陆用户的默认地址
     * @return
     */
    public Address queryDefaultAddress();
}
