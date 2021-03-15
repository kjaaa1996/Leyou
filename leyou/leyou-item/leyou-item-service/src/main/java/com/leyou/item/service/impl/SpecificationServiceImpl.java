package com.leyou.item.service.impl;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;
import com.leyou.item.service.ISpecificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 26747
 * @description SpecGroupServiceImpl
 * @date 2020/5/21 11:10
 */
@Service("specificationService")
public class SpecificationServiceImpl implements ISpecificationService {

    @Resource
    private SpecGroupMapper specGroupMapper;

    @Resource
    private SpecParamMapper specParamMapper;

    /**
     * 根据分类cid查询分组
     *
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> queryGroupsByCid(Long cid) {
        SpecGroup specGroup = new SpecGroup();
        specGroup.setCid(cid);
        return this.specGroupMapper.select(specGroup);
    }

    /**
     * 根据组gid查询参数,及其他查询条件
     *
     * @param gid
     * @return
     */
    @Override
    public List<SpecParam> queryParams(Long gid, Long cid, Boolean generic, Boolean searching) {
        SpecParam specParam = new SpecParam();
        specParam.setGroupId(gid);
        specParam.setCid(cid);
        specParam.setGeneric(generic);
        specParam.setSearching(searching);
        //可以直接为param设定多个值，当其值为null时，不会将其作为查询条件，因此具有通用性
        return this.specParamMapper.select(specParam);
    }

    /**
     * 根据分类cid查询规格参数组和规格参数
     *
     * @param cid
     * @return
     */
    @Override
    public List<SpecGroup> queryGroupsWithParam(Long cid) {
        //直接调用已有的查询组的方法和查询参数的方法
        List<SpecGroup> specGroups = this.queryGroupsByCid(cid);
        specGroups.forEach(specGroup -> {
            List<SpecParam> specParams = this.queryParams(specGroup.getId(), null, null, null);
            specGroup.setParams(specParams);
        });

        return specGroups;
    }

    /**
     * 新增参数表
     *
     * @param specParam
     */
    @Override
    @Transactional
    public void saveParams(SpecParam specParam) {
        this.specParamMapper.insertSelective(specParam);
    }

    /**
     * 更新参数表
     *
     * @param specParam
     */
    @Override
    @Transactional
    public void updateParams(SpecParam specParam) {
        this.specParamMapper.updateByPrimaryKey(specParam);
    }

    /**
     * 新增分组表
     * @param specGroup
     */
    @Override
    @Transactional
    public void saveGroups(SpecGroup specGroup) {
        this.specGroupMapper.insertSelective(specGroup);
    }

    /**
     * 更新分组表
     *
     * @param specGroup
     */
    @Override
    @Transactional
    public void updateGroups(SpecGroup specGroup) {
        this.specGroupMapper.updateByPrimaryKey(specGroup);
    }

    /**
     * 根据id删除参数
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteParams(Long id) {
        this.specParamMapper.deleteByPrimaryKey(id);
    }

    /**
     * 根据id删除分组
     *
     * @param id
     */
    @Override
    @Transactional
    public void deleteGroups(Long id) {
        this.specGroupMapper.deleteByPrimaryKey(id);
        //删除分组之后相应的参数也要删除
        SpecParam specParam=new SpecParam();
        specParam.setGroupId(id);
        this.specParamMapper.delete(specParam);
    }

}
