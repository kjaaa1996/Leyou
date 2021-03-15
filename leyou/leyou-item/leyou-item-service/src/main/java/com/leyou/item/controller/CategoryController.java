package com.leyou.item.controller;

import com.leyou.item.pojo.Category;
import com.leyou.item.service.ICategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 26747
 * @description CategoryController
 * @date 2020/5/15 16:03
 */
@RestController
@RequestMapping("/category")    //在网关中配置过商品的微服务前缀为item/**
@Api("分类服务接口")
public class CategoryController {

    @Resource(name = "categoryService")
    private ICategoryService categoryService;

    /**
     * 根据父节点的id查询子节点
     */
    @GetMapping("/list")    //ResponseEntity
    @ApiOperation(value = "根据父节点的id查询子节点", notes = "查询子节点")
    @ApiImplicitParam(name = "pid", defaultValue = "0", value = "要查询的父节点的id")
    public ResponseEntity<List<Category>> queryCategoriesByPid(@RequestParam(value = "pid", defaultValue = "0") Long pid) {

        if (pid == null || pid < 0) {
            //返回ResponseEntity，并设置状态码status为HttpStatus中的BAC_REQUEST(400)错误的请求状态码(参数不合法)，build为返回空响应体
            //return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            //return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            return ResponseEntity.badRequest().build();
        }
        List<Category> categories = this.categoryService.queryCategoriesByPid(pid);
        //categories.isEmpty必须其本身不为null否则会NullPoint,尽量使用CollectionUtils.isEmpty(categories)
        if (CollectionUtils.isEmpty(categories)) {
            //返回404：资源服务器未找到
            //return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            return ResponseEntity.notFound().build();
        }
        //返回响应体200查询成功
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据id集合查询相应的分类名组合
     *
     * @param ids
     * @return
     */
    @GetMapping
    @ApiOperation(value = "根据id集合查询相应的分类名组合", notes = "查询分类名")
    @ApiImplicitParam(name = "ids", required = true, value = "要查询的id集合")
    public ResponseEntity<List<String>> queryNamesByIds(@RequestParam("ids") List<Long> ids) {
        List<String> names = categoryService.queryNamesByIds(ids);
        if (CollectionUtils.isEmpty(names)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(names);
    }

    /**
     * 根据cid3查询所有的分类
     *
     * @param cid
     * @return
     */
    @GetMapping("/all/level")
    @ApiOperation(value = "根据cid3查询所有的分类", notes = "查询分类")
    @ApiImplicitParam(name = "id", required = true, value = "要查询的cid")
    public ResponseEntity<List<Category>> queryAllByCid3(@RequestParam(name = "id") Long cid) {
        List<Category> categories = this.categoryService.queryAllByCid3(cid);
        if (CollectionUtils.isEmpty(categories)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }

    /**
     * 新增分类
     *
     * @param category
     * @return
     */
    @PostMapping
    @ApiOperation(value = "新增分类", notes = "新增分类")
    @ApiImplicitParam(name = "category", required = true, value = "要新增的category")
    public ResponseEntity<Void> saveCategory(Category category) {
        System.out.println(category.toString());
        this.categoryService.saveCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 获取最后一个节点的信息
     * @return
     */
    @GetMapping("/last")
    @ApiOperation(value = "获取最后一个节点", notes = "获取最新节点")
    public ResponseEntity<Category> queryLast() {
        Category category = this.categoryService.queryLast();
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(category);
    }

    /**
     * 删除分类
     *
     * @param cid
     * @return
     */
    @DeleteMapping("/delete/{cid}")
    @ApiOperation(value = "根据分类id删除分类", notes = "删除分类")
    @ApiImplicitParam(name = "cid", required = true, value = "要删除的category的cid")
    public ResponseEntity<Void> deleteCategory(@PathVariable("cid") Long cid) {
        this.categoryService.deleteCategory(cid);
        return ResponseEntity.ok().build();
    }

    /**
     * 更新分类信息
     *
     * @param category
     * @return
     */
    @PutMapping
    @ApiOperation(value = "更新分类信息", notes = "更新分类")
    @ApiImplicitParam(name = "category", required = true, value = "要更新的category")
    public ResponseEntity<Void> updateCategory(Category category) {
        this.categoryService.updateCategory(category);
        return ResponseEntity.accepted().build();
    }

    /**
     * 根据品牌id查询其所属的分类
     * @param bid
     * @return
     */
    @GetMapping("/bid/{bid}")
    @ApiOperation(value="根据品牌id查询其所属的分类",notes = "bid查询分类")
    @ApiImplicitParam(name = "bid",required = true,value = "要查询分类的品牌Id")
    public ResponseEntity<List<Category>> queryCategoryByBrandId(@PathVariable("bid") Long bid){
        List<Category> categories=this.categoryService.queryCategoryByBrandId(bid);
        if(CollectionUtils.isEmpty(categories)){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(categories);
    }
}
