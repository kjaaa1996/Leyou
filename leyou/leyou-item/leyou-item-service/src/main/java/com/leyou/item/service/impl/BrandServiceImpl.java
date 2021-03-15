package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.IBrandService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 26747
 * @description BrandServiceImpl
 * @date 2020/5/15 21:28
 */
@Service("brandService")
public class BrandServiceImpl implements IBrandService {

    @Resource
    private BrandMapper brandMapper;

    /**
     * 根据查询条件分页并排序查询品牌信息
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @Override
    public PageResult<Brand> queryBrandsByPage(String key, Integer page, Integer rows, String sortBy, boolean desc) {
        //初始化example对象
        Example example = new Example(Brand.class);
        Example.Criteria criteria = example.createCriteria();
        //1.key:根据name模糊查询，或者根据首字母查询
        //先判断key是否为空，不为空时才进行查询  此处的StringUtils为org.apache.commons.lang.StringUtils
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("name", "%" + key + "%").orEqualTo("letter", key);
        }

        //2.page&rows:添加分页条件
        PageHelper.startPage(page, rows);

        //3.sortBy&desc:添加排序条件,Criteria没有排序方法，使用example,
        //使用orderBy需要判断desc是否为true，或者使用setOrderByClause拼接sql语句
        if (StringUtils.isNotBlank(sortBy)) {
            //setOrderByClause("sort asc")其中sort为排序条件，asc为排序方法
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc"));
        }

        //使用根据例子查询可以添加模糊查询条件
        List<Brand> brands = this.brandMapper.selectByExample(example);

        //要返回成PageResult<Brand>必须要有总页数和总条数,需要使用PageInfo来获取,并将结果集brands封装入PageInfo中
        PageInfo<Brand> pageInfo = new PageInfo(brands);
        //也可以将brands换成pageInfo.getList()效果一样

        return new PageResult<>(pageInfo.getTotal(), pageInfo.getPages(), brands);
    }

    /**
     * 保存新的品牌，将品牌数据brand和分类的id数组cids保存到数据库,此处先新增brand表在新增中间表category_brand表
     *
     * @param brand
     * @param cids
     */
    @Override   //新增中间表category_brand表需要调用brandMapper的自定义方法
    @Transactional
    public void saveBrand(Brand brand, List<Long> cids) {
        //新增brand表,insertSelective方法会先对字段进行判断再更新，如果为null就忽略更新，insert会将null也更新进数据库
        brandMapper.insertSelective(brand);
        //新增中间表category_brand
        /*for (Long cid : cids
        ) {
            brandMapper.insertCategoryAndBrand(cid, brand.getId());
        }*/
        //lambda表达式
        cids.forEach(cid -> {
            brandMapper.insertCategoryAndBrand(cid, brand.getId());
        });
    }

    /**
     * 根据cid查询品牌集合
     *
     * @param cid
     * @return
     */
    @Override
    public List<Brand> queryBrandsByCid(Long cid) {
        return brandMapper.queryBrandsByCid(cid);
    }

    /**
     * 根据id查询品牌
     *
     * @param id
     * @return
     */
    @Override
    public Brand queryBrandById(Long id) {
        return brandMapper.selectByPrimaryKey(id);
    }

    /**
     * 更新品牌信息
     * @param brand
     */
    @Override
    @Transactional
    public void updateBrand(Brand brand,Long cids) {
        this.brandMapper.updateByPrimaryKey(brand);
        //更新中间表
        this.brandMapper.updateCategoryAndBrand(cids,brand.getId());
    }

    /**
     * 根据传递的品牌bid删除指定的品牌brand
     * @param bid
     */
    @Override
    @Transactional
    public void deleteBrand(String bid) {
        if(bid.contains("-")){  //多个删除
            String[] ids=bid.split("-");
            for (String id:ids
                 ) {
                this.brandMapper.deleteByPrimaryKey(id);
                //删除后同时删除中间表的数据
                this.brandMapper.deleteCategoryAndBrand(id);
            }
        }else { //单个删除
            this.brandMapper.deleteByPrimaryKey(bid);
            //删除后同时删除中间表的数据
            this.brandMapper.deleteCategoryAndBrand(bid);
        }
    }
}
