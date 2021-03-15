package com.leyou.item.controller;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.ISpecificationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 26747
 * @description SpecController
 * @date 2020/5/21 11:11
 */
@RestController
@RequestMapping("/spec")
public class SpecificationController {

    @Resource(name = "specificationService")
    private ISpecificationService specificationService;

    /**
     * 根据分类cid查询分组
     *
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsByCid(@PathVariable("cid") Long cid) {

        List<SpecGroup> specGroups = this.specificationService.queryGroupsByCid(cid);

        if (CollectionUtils.isEmpty(specGroups)) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(specGroups);
    }

    /**
     * 根据组gid查询参数,添加其他查询条件，便于后期扩展
     *
     * @param gid
     * @return
     */
    @GetMapping("/params")
    public ResponseEntity<List<SpecParam>> queryParams(
            @RequestParam(name = "gid", required = false) Long gid,
            @RequestParam(name = "cid", required = false) Long cid,
            @RequestParam(name = "generic", required = false) Boolean generic,
            @RequestParam(name = "searching", required = false) Boolean searching
    ) {
        List<SpecParam> specParams = specificationService.queryParams(gid, cid, generic, searching);
        if (CollectionUtils.isEmpty(specParams)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(specParams);
    }

    /**
     * 新增参数表
     *
     * @param specParam
     * @return
     */
    @PostMapping("/param")
    public ResponseEntity<Void> saveParams(SpecParam specParam) {
        specificationService.saveParams(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新参数表
     *
     * @param specParam
     * @return
     */
    @PutMapping("/param")
    public ResponseEntity<Void> updateParams(SpecParam specParam) {
        specificationService.updateParams(specParam);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据id删除参数
     *
     * @param id
     * @return
     */
    @DeleteMapping("/param/{id}")
    public ResponseEntity<Void> deleteParams(@PathVariable("id") Long id) {
        specificationService.deleteParams(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 新增分组表
     *
     * @param specGroup
     * @return
     */
    @PostMapping("/group")
    public ResponseEntity<Void> saveGroups(SpecGroup specGroup) {
        this.specificationService.saveGroups(specGroup);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新分组表
     *
     * @param specGroup
     * @return
     */
    @PutMapping("/group")
    public ResponseEntity<Void> updateGroups(SpecGroup specGroup) {
        this.specificationService.updateGroups(specGroup);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    /**
     * 根据id删除分组
     *
     * @param id
     * @return
     */
    @DeleteMapping("/group/{id}")
    public ResponseEntity<Void> deleteGroups(@PathVariable("id") Long id) {
        specificationService.deleteGroups(id);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    /**
     * 根据分类id查询规格参数组和规格参数
     *
     * @param cid
     * @return
     */
    @GetMapping("group/param/{cid}")
    public ResponseEntity<List<SpecGroup>> queryGroupsWithParam(@PathVariable("cid") Long cid) {
        List<SpecGroup> groupList = specificationService.queryGroupsWithParam(cid);
        if (CollectionUtils.isEmpty(groupList)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(groupList);
    }

}
