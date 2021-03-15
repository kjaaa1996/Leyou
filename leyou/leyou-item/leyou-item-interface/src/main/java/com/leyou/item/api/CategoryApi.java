package com.leyou.item.api;

import com.leyou.item.pojo.Category;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 26747
 * @description CategoryApi
 * @date 2020/5/25 15:59
 */
@RequestMapping("/category")
public interface CategoryApi {

    /**
     * 根据id集合查询相应的分类名组合
     *
     * @param ids
     * @return
     */
    @GetMapping
    public List<String> queryNamesByIds(@RequestParam("ids") List<Long> ids);

    /**
     * 根据cid3查询所有的分类
     *
     * @param cid
     * @return
     */
    @GetMapping("/all/level")
    public List<Category> queryAllByCid3(@RequestParam(name = "id") Long cid);
}
