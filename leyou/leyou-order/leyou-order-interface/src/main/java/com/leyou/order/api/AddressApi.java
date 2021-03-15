package com.leyou.order.api;

import com.leyou.order.pojo.Address;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author 26747
 * @description AddressApi
 * @date 2020/7/6 20:38
 */
@RequestMapping("/address")
public interface AddressApi {
    /**
     * 根据当前登录的用户查询所有的收货地址记录
     *
     * @return
     */
    @GetMapping
    @ApiOperation(value = "根据用户id，返回地址集合", notes = "查询地址")
    public List<Address> queryAddresses();

    /**
     * 根据id查询地址信息
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation(value = "根据地址id查询地址信息", notes = "查询地址")
    @ApiImplicitParam(value = "要查询的地址id", name = "id", type = "Long")
    public Address queryAddressById(@PathVariable("id") Long id);

    /**
     * 查询当前用户的默认地址
     *
     * @return
     */
    @GetMapping("/default")
    @ApiOperation(value = "根据当前登录的用户id查询其默认地址", notes = "查询默认地址")
    public Address queryDefaultAddress();
}
