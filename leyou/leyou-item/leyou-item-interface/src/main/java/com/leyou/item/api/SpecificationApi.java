package com.leyou.item.api;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author 26747
 * @description SpecificationApi
 * @date 2020/5/25 16:00
 */
@RequestMapping("/spec")
public interface SpecificationApi {

    /**
     * 根据分类cid查询分组
     *
     * @param cid
     * @return
     */
    @GetMapping("/groups/{cid}")
    public List<SpecGroup> queryGroupsByCid(@PathVariable("cid") Long cid);

    /**
     * 根据组gid查询参数,添加其他查询条件，便于后期扩展
     *
     * @param gid
     * @return
     */
    @GetMapping("/params")
    public List<SpecParam> queryParams(
            @RequestParam(name = "gid", required = false) Long gid,
            @RequestParam(name = "cid", required = false) Long cid,
            @RequestParam(name = "generic", required = false) Boolean generic,
            @RequestParam(name = "searching", required = false) Boolean searching
    );

    /**
     * 根据分类id查询规格参数组和规格参数
     *
     * @param cid
     * @return
     */
    @GetMapping("group/param/{cid}")
    public List<SpecGroup> queryGroupsWithParam(@PathVariable("cid") Long cid);
}
