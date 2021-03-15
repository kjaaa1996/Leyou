package com.leyou.item.service;

import com.leyou.item.pojo.Category;

import java.util.List;

/**
 * @author 26747
 * @description ICategoryService
 * @date 2020/5/15 16:01
 */
public interface ICategoryService {
    List<Category> queryCategoriesByPid(Long pid);

    List<String> queryNamesByIds(List<Long> asList);

    List<Category> queryAllByCid3(Long cid);

    void saveCategory(Category category);

    void deleteCategory(Long cid);

    void updateCategory(Category category);

    void queryAllLeafNode(Category category, List<Category> leafNodes);

    void queryAllNode(Category category, List<Category> nodes);

    Category queryLast();

    List<Category> queryCategoryByBrandId(Long bid);
}
