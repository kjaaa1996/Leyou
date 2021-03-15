package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 26747
 * @description CategoryMapper
 * @date 2020/5/15 15:59
 */
public interface CategoryMapper extends Mapper<Category>, SelectByIdListMapper<Category,Long> {

    //根据品牌id查询分类(一个品牌可能有多种分类的商品)
    @Select("select * from tb_category where id in (select category_id from tb_category_brand where brand_id = #{bid})")
    List<Category> queryCategoryByBrandId(Long bid);

    //根据分类id维护中间表(删除)
    @Delete("delete from tb_category_brand where category_id = #{cid}")
    void deleteCategoryBrandByCategoryId(Long cid);

    //查询分类表中最后一条数据，嵌套查询，查询最大id对应的数据
    @Select("select * from tb_category where id = (select MAX(id) from tb_category)")
    Category queryLast();
}
