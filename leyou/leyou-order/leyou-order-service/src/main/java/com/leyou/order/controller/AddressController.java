package com.leyou.order.controller;

import com.leyou.order.pojo.Address;
import com.leyou.order.service.IAddressService;
import com.leyou.order.service.impl.AddressServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

/**
 * @author 26747
 * @description AddressController
 * @date 2020/6/9 10:52
 */
@RestController
@RequestMapping("/address")
@Api("地址服务接口")
public class AddressController {

    @Resource(name = "addressService")
    private IAddressService addressService;


    /**
     * 根据当前登录的用户查询所有的收货地址记录
     *
     * @return
     */
    @GetMapping
    @ApiOperation(value = "根据用户id，返回地址集合", notes = "查询地址")
    public ResponseEntity<List<Address>> queryAddresses() {
        List<Address> addresses = this.addressService.queryAddresses();
        if (CollectionUtils.isEmpty(addresses)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(addresses);
    }

    /**
     * 保存新的收货地址
     *
     * @param address
     * @return
     */
    @PostMapping
    @ApiOperation(value = "获取登录用户的用户id并保存新的收货地址", notes = "保存收货地址")
    @ApiImplicitParam(value = "传递的地址信息", name = "address", required = true)
    public ResponseEntity<Void> addAddress(@RequestBody @Valid Address address) {
        this.addressService.addAddress(address);
        return ResponseEntity.accepted().build();
    }

    /**
     * 更新收货地址
     *
     * @param address
     * @return
     */
    @PutMapping
    @ApiOperation(value = "更新收货地址", notes = "更新收货地址")
    @ApiImplicitParam(value = "传递的地址信息", name = "address", required = true)
    public ResponseEntity<Void> updateAddress(@RequestBody @Valid Address address) {
        this.addressService.updateAddress(address);
        return ResponseEntity.accepted().build();
    }

    /**
     * 删除地址
     *
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    @ApiOperation(value = "根据地址id删除地址信息", notes = "删除地址")
    @ApiImplicitParam(value = "要删除的地址id", name = "id", type = "Long")
    public ResponseEntity<Void> deleteAddress(@PathVariable("id") Long id) {
        this.addressService.deleteAddress(id);
        return ResponseEntity.accepted().build();
    }

    /**
     * 根据id查询地址信息
     *
     * @param id
     * @return
     */
    @GetMapping("{id}")
    @ApiOperation(value = "根据地址id查询地址信息", notes = "查询地址")
    @ApiImplicitParam(value = "要查询的地址id", name = "id", type = "Long")
    public ResponseEntity<Address> queryAddressById(@PathVariable("id") Long id) {
        Address address = this.addressService.queryAddressById(id);
        if (address == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(address);
    }

    /**
     * 查询当前用户的默认地址
     * @return
     */
    @GetMapping("/default")
    @ApiOperation(value="根据当前登录的用户id查询其默认地址",notes = "查询默认地址")
    public ResponseEntity<Address> queryDefaultAddress(){
        Address address=this.addressService.queryDefaultAddress();
        if(address==null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(address);
    }

}
