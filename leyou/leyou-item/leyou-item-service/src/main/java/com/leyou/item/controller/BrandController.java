package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Brand;
import com.leyou.item.service.IBrandService;
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
 * @description BrandController
 * @date 2020/5/15 21:29
 */
@RestController
@RequestMapping("/brand")
public class BrandController {

    @Resource(name = "brandService")
    private IBrandService brandService;

    /**
     * 根据查询条件分页并排序查询品牌信息,使用自定义的PageResult分页结果集，其中封住了一个item集合
     *
     * @param key
     * @param page
     * @param rows
     * @param sortBy
     * @param desc
     * @return
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<Brand>> queryBrandsByPage(
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "rows", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "desc", required = false) boolean desc) {
        PageResult<Brand> result = brandService.queryBrandsByPage(key, page, rows, sortBy, desc);
        //判断result集合中当前页数据items集合元素是否为空
        if (CollectionUtils.isEmpty(result.getItems())) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    /**
     * 保存新的品牌，以post方式将品牌数据brand和分类的id数组cids保存到数据库，并返回状态码201和空响应体
     *
     * @param brand
     * @param cids
     * @return
     */
    @PostMapping    //brand为新建的品牌信息，cids是改品牌所属的分类信息的id数组
    public ResponseEntity<Void> saveBrand(Brand brand, @RequestParam(name = "cids") List<Long> cids) {

        //调用brandService的保存方法，
        brandService.saveBrand(brand, cids);

        //返回201状态码和空响应体
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据cid查询品牌集合
     *
     * @param cid
     * @return
     */
    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryBrandsByCid(@PathVariable("cid") Long cid) {
        List<Brand> brandList = brandService.queryBrandsByCid(cid);
        if (CollectionUtils.isEmpty(brandList)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brandList);
    }

    /**
     * 根据id查询品牌
     * @param id
     * @return
     */
    @GetMapping("{id}")
    public ResponseEntity<Brand> queryBrandById(@PathVariable("id") Long id) {
        Brand brand = brandService.queryBrandById(id);
        if (brand == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(brand);
    }

    /**
     * 更新品牌信息
     * @param brand
     * @return
     */
    @PutMapping
    @ApiOperation(value="更新品牌信息",notes = "更新品牌")
    @ApiImplicitParam(name = "brand",value="要更新的brand数据")
    public ResponseEntity<Void> updateBrand(Brand brand,@RequestParam("cids")Long cids){
        this.brandService.updateBrand(brand,cids);
        return ResponseEntity.accepted().build();
    }

    /**
     * 根据传递的品牌bid删除指定的品牌brand；单个删除和多个删除二合一
     * @param bid
     * @return
     */
    @DeleteMapping("/delete/{bid}")
    @ApiOperation(value="根据传递的品牌bid删除指定的品牌brand",notes = "删除品牌")
    @ApiImplicitParam(name = "bid",required = true,value = "要删除的brand的bid")
    public ResponseEntity<Void> deleteBrand(@PathVariable("bid")String bid){
        this.brandService.deleteBrand(bid);
        return ResponseEntity.ok().build();
    }

}
