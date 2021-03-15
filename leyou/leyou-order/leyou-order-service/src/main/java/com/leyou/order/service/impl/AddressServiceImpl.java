package com.leyou.order.service.impl;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.order.interceptor.LoginInterceptor;
import com.leyou.order.mapper.AddressMapper;
import com.leyou.order.pojo.Address;
import com.leyou.order.service.IAddressService;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 26747
 * @description AddressService
 * @date 2020/6/9 10:59
 */
@Service("addressService")
public class AddressServiceImpl implements IAddressService {

    @Resource
    private AddressMapper addressMapper;


    /**
     * 根据当前登录的用户查询所有的收货地址记录
     *
     * @return
     */
    public List<Address> queryAddresses() {
        //获取当前的用户状态
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        //根据当前用户id获取地址集合
        Address address = new Address();
        address.setUserId(userInfo.getId());
        return this.addressMapper.select(address);
    }

    /**
     * 根据登录的用户添加新的收货地址
     *
     * @param address
     */
    @Override
    public void addAddress(Address address) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        address.setUserId(loginUser.getId());
        //如果新增的地址是默认地址,先调用清空默认地址，再添加新地址
        if (address.getDefaultAddress()) {
            this.setNotDefault();
        }
        this.addressMapper.insertSelective(address);
    }

    /**
     * 更新地址信息
     *
     * @param address
     */
    @Override
    public void updateAddress(Address address) {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        address.setUserId(loginUser.getId());
        //如果更新的是默认地址，先清空默认地址，再更新地址
        if (address.getDefaultAddress()) {
            this.setNotDefault();
        }
        this.addressMapper.updateByPrimaryKeySelective(address);
    }

    /**
     * 删除地址
     *
     * @param id
     */
    @Override
    public void deleteAddress(Long id) {
        this.addressMapper.deleteByPrimaryKey(id);
    }

    /**
     * 查询地址
     *
     * @param id
     * @return
     */
    @Override
    public Address queryAddressById(Long id) {
        return this.addressMapper.selectByPrimaryKey(id);
    }

    /**
     * 将其他地址设为非默认地址
     */
    @Override
    public void setNotDefault() {
        List<Address> addresses = this.queryAddresses();
        addresses.forEach(addr -> {
            if (addr.getDefaultAddress()) {
                addr.setDefaultAddress(false);
                this.addressMapper.updateByPrimaryKeySelective(addr);
            }
        });
    }

    /**
     * 查询当前登陆用户的默认地址
     * @return
     */
    @Override
    public Address queryDefaultAddress() {
        UserInfo loginUser = LoginInterceptor.getLoginUser();
        Example example=new Example(Address.class);
        example.createCriteria().andEqualTo("user_id",loginUser.getId()).andEqualTo("default_address",true);
        return this.addressMapper.selectOneByExample(example);
    }
}
