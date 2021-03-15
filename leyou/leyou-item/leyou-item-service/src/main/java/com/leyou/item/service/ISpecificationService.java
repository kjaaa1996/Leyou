package com.leyou.item.service;

import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import java.util.List;

/**
 * @author 26747
 * @description ISpecGroupService
 * @date 2020/5/21 11:10
 */
public interface ISpecificationService {
    List<SpecGroup> queryGroupsByCid(Long cid);

    void updateParams(SpecParam specParam);

    void updateGroups(SpecGroup specGroup);

    void deleteParams(Long id);

    void deleteGroups(Long id);

    List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching);

    List<SpecGroup> queryGroupsWithParam(Long cid);

    void saveGroups(SpecGroup specGroup);

    void saveParams(SpecParam specParam);
}
