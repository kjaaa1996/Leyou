package com.leyou.goods.controller;

import com.leyou.goods.service.IGoodsHtmlService;
import com.leyou.goods.service.IGoodsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author 26747
 * @description GoodsController
 * @date 2020/5/28 14:33
 */
@Controller
@RequestMapping("/item")
public class GoodsController {

    @Resource(name = "goodsService")
    private IGoodsService goodsService;

    @Resource(name = "goodsHtmlService")
    private IGoodsHtmlService goodsHtmlService;

    /**
     * 根据spuId返回商品详情，并创建静态化页面
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/{id}.html")
    public String toItemPage(@PathVariable("id") Long id, Model model) {
        Map<String, Object> map = this.goodsService.loadData(id);

        model.addAllAttributes(map);

        //调用生成静态化页面方法
        this.goodsHtmlService.asyncExecute(id);
        return "item";
    }
}
