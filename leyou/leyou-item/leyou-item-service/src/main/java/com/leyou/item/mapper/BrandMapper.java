package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author 26747
 * @description BrandMapper
 * @date 2020/5/15 21:29
 */
public interface BrandMapper extends Mapper<Brand> {

    /**
     * 新增商品分类和品牌表的中间表category_brand的数据
     *
     * @param cid
     * @param bid
     */
    @Insert("insert into tb_category_brand(category_id,brand_id) values(#{cid},#{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid, @Param("bid") Long bid);

    /**
     * 根据分类id查询品牌集合    两条查询语句相同
     * @param cid
     * @return
     */
    //@Select("select * from tb_brand where id in (select brand_id from tb_category_brand where category_id = #{cid})")
    @Select("select b.* from tb_brand b inner join tb_category_brand cb on b.id = cb.brand_id where cb.category_id = #{cid}")
    List<Brand> queryBrandsByCid(Long cid);

    /**
     * 根据品牌id更新中间表
     * @param cid
     * @param bid
     */
    @Update("update tb_category_brand set category_id = #{cid} where brand_id = #{bid}")
    void updateCategoryAndBrand(@Param("cid")Long cid,@Param("bid")Long bid);

    /**
     * 根据品牌bid删除中间表的数据
     * @param bid
     */
    @Delete("delete from tb_category_brand where brand_id = #{bid}")
    void deleteCategoryAndBrand(String bid);
}
