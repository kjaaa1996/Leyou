package com.leyou.item.service.impl;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;
import com.leyou.item.service.ICategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 26747
 * @description CategoryServiceImpl
 * @date 2020/5/15 16:02
 */
@Service("categoryService")
public class CategoryServiceImpl implements ICategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    /**
     * 根据父节点查询子节点
     *
     * @param pid
     * @return
     */
    @Override
    public List<Category> queryCategoriesByPid(Long pid) {
        Category record = new Category();
        //新建一个类，将其个别字段设为判断标准
        record.setParentId(pid);
        //根据这个类的特定属性来进行筛选
        return categoryMapper.select(record);
    }

    /**
     * 根据id数组查询3级分类的名称
     *
     * @param cidList
     * @return
     */
    @Override
    public List<String> queryNamesByIds(List<Long> cidList) {
        //1.一句话直接输出
        //return cidList.stream().map(cid->categoryMapper.selectByPrimaryKey(cid).getName()).collect(Collectors.toList());

        //2.两句话输出，需要继承SelectByIdListMapper<实体类型,id类型>接口
        //List<Category> categories = this.categoryMapper.selectByIdList(cidList);
        //return categories.stream().map(Category::getName).collect(Collectors.toList());

        //3.基本方法
        List<String> names = new ArrayList<>();
        cidList.forEach(cid -> {
            String name = categoryMapper.selectByPrimaryKey(cid).getName();
            names.add(name);
        });
        return names;
    }

    /**
     * 根据cid3查询所有的分类
     *
     * @param cid
     * @return
     */
    @Override
    public List<Category> queryAllByCid3(Long cid) {
        Category category3 = categoryMapper.selectByPrimaryKey(cid);
        Category category2 = categoryMapper.selectByPrimaryKey(category3.getParentId());
        Category category1 = categoryMapper.selectByPrimaryKey(category2.getParentId());
        return Arrays.asList(category1, category2, category3);
    }

    /**
     * 新增分类
     *
     * @param category
     */
    @Override
    @Transactional
    public void saveCategory(Category category) {
        //首先将本身的id设为null，id为自增长
        category.setId(null);
        this.categoryMapper.insertSelective(category);
        //获取到新增节点的父节点，为了避免他不是父节点，将其设为父节点
        Category parentCategory = new Category();
        parentCategory.setId(category.getParentId());
        parentCategory.setIsParent(true);
        //将父节点的信息更新
        this.categoryMapper.updateByPrimaryKeySelective(parentCategory);
    }

    /**
     * 删除分类
     *
     * @param cid
     */
    @Override
    @Transactional
    public void deleteCategory(Long cid) {
        //先根据id查询到指定节点
        Category category = this.categoryMapper.selectByPrimaryKey(cid);
        //如果指定节点是父节点
        if (category.getIsParent()) {
            //递归查询所有的叶子节点
            List<Category> leafNodes = new ArrayList<>();
            this.queryAllLeafNode(category, leafNodes);

            //递归查询所有的子节点
            List<Category> nodes = new ArrayList<>();
            this.queryAllNode(category, nodes);

            //直接删除所有的子节点
            for (Category c : nodes
            ) {
                this.categoryMapper.delete(c);
            }

            //根据叶子节点的集合遍历删除中间表
            for (Category c : leafNodes
            ) {
                this.categoryMapper.deleteCategoryBrandByCategoryId(c.getId());
            }
        } else {//不是父节点就是叶子节点
            //看他的父节点有几个孩子节点
            Example example = new Example(Category.class);
            example.createCriteria().andEqualTo("parentId", category.getParentId());
            List<Category> categories = this.categoryMapper.selectByExample(example);
            if (categories.size() != 1) {
                //有兄弟节点，直接删除此节点，并更新中间表
                this.categoryMapper.delete(category);
                this.categoryMapper.deleteCategoryBrandByCategoryId(category.getId());
            } else {
                //没有兄弟节点，删除此节点，将父结点设为叶子节点，并更新中间表
                this.categoryMapper.delete(category);
                Category parent = new Category();
                parent.setId(category.getParentId());
                parent.setIsParent(false);
                this.categoryMapper.updateByPrimaryKeySelective(parent);
                this.categoryMapper.deleteCategoryBrandByCategoryId(category.getId());
            }
        }
    }

    /**
     * 递归查询指定分类下的所有叶子节点，用于维护中间表
     *
     * @param category
     * @param leafNodes
     * @return
     */
    @Override
    public void queryAllLeafNode(Category category, List<Category> leafNodes) {
        //如果本次递归的节点本身是一个叶子节点，将其加入集合中
        if (!category.getIsParent()) {
            leafNodes.add(category);
        }
        //将指定节点的id作为parentId条件进行查询，将查询到的节点集合进行递归查询叶子节点
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("parentId", category.getId());
        List<Category> categories = this.categoryMapper.selectByExample(example);
        for (Category c : categories
        ) {
            this.queryAllLeafNode(c, leafNodes);
        }
    }

    /**
     * 递归查询指定分类下的所有子节点，用于直接删除
     *
     * @param category
     * @param nodes
     * @return
     */
    @Override
    public void queryAllNode(Category category, List<Category> nodes) {
        //递归查询指定节点的所有子节点，其本身也要添加进集合中
        nodes.add(category);
        //将指定节点的id作为父节点id进行条件查询
        Example example = new Example(Category.class);
        example.createCriteria().andEqualTo("parentId", category.getId());
        List<Category> categories = this.categoryMapper.selectByExample(example);
        for (Category c : categories
        ) {
            this.queryAllNode(c, nodes);
        }
    }

    /**
     * 更新分类信息
     *
     * @param category
     */
    @Override
    @Transactional
    public void updateCategory(Category category) {
        this.categoryMapper.updateByPrimaryKeySelective(category);
    }

    /**
     * 查询最后一个节点
     *
     * @return
     */
    @Override
    public Category queryLast() {
        Category record = this.categoryMapper.queryLast();
        return this.categoryMapper.selectByPrimaryKey(record.getId());
    }

    /**
     * 根据品牌id查询其所属的分类
     * @param bid
     * @return
     */
    @Override
    public List<Category> queryCategoryByBrandId(Long bid) {
        return this.categoryMapper.queryCategoryByBrandId(bid);
    }
}
