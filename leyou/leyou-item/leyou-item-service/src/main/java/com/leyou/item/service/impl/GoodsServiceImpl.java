package com.leyou.item.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.*;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;
import com.leyou.item.service.ICategoryService;
import com.leyou.item.service.IGoodsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author 26747
 * @description GoodsServiceImpl
 * @date 2020/5/22 10:30
 */
@Service("goodsService")
public class GoodsServiceImpl implements IGoodsService {

    @Resource
    private SpuDetailMapper spuDetailMapper;

    @Resource
    private SkuMapper skuMapper;

    @Resource
    private StockMapper stockMapper;

    @Resource
    private SpuMapper spuMapper;
    //需要获取分类名和品牌名,品牌名只有1个直接获取，分类名3个都要
    @Resource
    private BrandMapper brandMapper;

    @Resource(name = "categoryService")
    private ICategoryService categoryService;

//    //注入amqp模版,用于发送消息
//    @Resource
//    private AmqpTemplate amqpTemplate;
//
//    /**
//     * 根据传递的类型，通知相应的微服务更新数据
//     * @param type
//     * @param id
//     */
//    @Override
//    public void sendMsg(String type, Long id) {
//        //发送消息通知其他微服务进行更新
//        try {
//            this.amqpTemplate.convertAndSend("item." + type, id);
//        } catch (AmqpException e) {
//            e.printStackTrace();
//        }
//    }

    /**
     * 根据关键字分页查询商品表
     *
     * @param key
     * @param page
     * @param rows
     * @param saleable
     * @return
     */
    @Override
    public PageResult<SpuBo> querySpuBoByPage(String key, Integer page, Integer rows, Boolean saleable,String sortBy,Boolean desc) {
        //设置查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        //判断搜索条件是否为空，不为空时，模糊查询标题进行匹配
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }
        if (saleable != null) { //判断是下架商品还是上架商品
            criteria.andEqualTo("saleable", saleable);
        }

        //先判断sortBy和desc是否存在
        if(!StringUtils.isBlank(sortBy)){
            //setOrderByClause("sort asc")其中sort为排序条件，asc为排序方法
            example.setOrderByClause(sortBy+" "+(desc ? "desc":"asc"));
        }

        //分页条件
        PageHelper.startPage(page, rows);
        //进行模版匹配查询
        List<Spu> spuList = spuMapper.selectByExample(example);
        PageInfo<Spu> pageInfo = new PageInfo<>(spuList);

        //创建返回的分页spuBo集合
        List<SpuBo> spuBos = new ArrayList<>();
        spuList.forEach(spu -> {
            SpuBo spuBo = new SpuBo();
            //copy共同属性的值到新的对象
            BeanUtils.copyProperties(spu, spuBo);
            //查询分类的名称
            List<String> names = categoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            //将分类的名称写入新的对象中
            spuBo.setCname(StringUtils.join(names, "/"));
            //查询品牌的名称
            String name = brandMapper.selectByPrimaryKey(spu.getBrandId()).getName();
            //将品牌的名称写入对象
            spuBo.setBname(name);

            spuBos.add(spuBo);
        });

        return new PageResult<>(pageInfo.getTotal(), spuBos);
    }

    /**
     * 新增商品同时对spu，spuDetail，sku，stock表进行新增
     *
     * @param spuBo
     */
    @Override
    @Transactional
    public void saveGoods(SpuBo spuBo) {
        //新增spu,直接根据spuBo进行新增，数据库中不存在的字段不会改变
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        spuMapper.insertSelective(spuBo);

        //新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        spuDetailMapper.insertSelective(spuDetail);

        //获取sku集合并存储sku和stock
        saveSkuAndStock(spuBo);

        //发送消息,现在在做其他完善先关了
        //sendMsg("insert", spuBo.getId());
    }

    /**
     * 更新商品 同时更新spu spuDetail sku stock 其中要先更新sku和stock
     *
     * @param spuBo
     */
    @Override
    @Transactional
    public void updateGoods(SpuBo spuBo) {

        List<Sku> skus = this.querySkusBySpuId(spuBo.getId());
        //如果以前存在sku数据，则删除
        if (!CollectionUtils.isEmpty(skus)) {
            /*//获取所有存在的sku数据的id集合,有点笨
            List<Long> ids = new ArrayList<>();
            skus.forEach(sku -> {
                Long id = sku.getId();
                ids.add(id);
            });
            //根据id删除以前的库存
            Example example = new Example(Stock.class);
            //如果id存在于集合中，则删除
            example.createCriteria().andIn("skuId", ids);
            this.stockMapper.deleteByExample(example);

            //根据spuId删除以前存在的sku
            Sku record = new Sku();
            record.setSpuId(spuBo.getId());
            this.skuMapper.delete(record);*/

            //根据spuBo的id获取skus之后直接遍历skus拿到每个skuId后直接删除sku和库存即可
            skus.forEach(sku -> {
                //直接根据skuId删除库存
                this.stockMapper.deleteByPrimaryKey(sku.getId());
                //直接根据skuId删除sku
                this.skuMapper.deleteByPrimaryKey(sku.getId());
            });
        }
        //更新新的sku和stock
        saveSkuAndStock(spuBo);

        //更新spu
        spuBo.setLastUpdateTime(new Date());
        spuBo.setCreateTime(null);
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        spuMapper.updateByPrimaryKeySelective(spuBo);

        //更新spuDetail
        spuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

        //发送消息，现在在做其他完善先关了
        //sendMsg("update", spuBo.getId());
    }

    /**
     * 根据传递的spuId，删除相应的spu，spuDetail，sku，stock
     * @param spuId
     */
    @Transactional
    @Override
    public void deleteSpuById(Long spuId) {
        //先根据spuId获取sku集合用于删除stock，sku
        List<Sku> skus = this.querySkusBySpuId(spuId);
        //遍历skus删除sku和stock
        skus.forEach(sku -> {
            this.stockMapper.deleteByPrimaryKey(sku.getId());
            this.skuMapper.deleteByPrimaryKey(sku.getId());
        });
        //删除spuDetail和spu
        this.spuMapper.deleteByPrimaryKey(spuId);
        this.spuDetailMapper.deleteByPrimaryKey(spuId);

        //发送消息到mq
        //this.sendMsg("delete",spuId);
    }

    /**
     * 根据id查询spu
     *
     * @param id
     * @return
     */
    @Override
    public Spu querySpuById(Long id) {
        return spuMapper.selectByPrimaryKey(id);
    }

    /**
     * 根据skuId查询sku
     *
     * @param id
     * @return
     */
    @Override
    public Sku querySkuById(Long id) {
        return skuMapper.selectByPrimaryKey(id);
    }

    /**
     * 保存sku和stock 更新商品和增加商品都适用
     *
     * @param spuBo
     */
    private void saveSkuAndStock(SpuBo spuBo) {
        //获取sku集合并存储sku和stock
        spuBo.getSkus().forEach(sku -> {
            //新增sku
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            skuMapper.insertSelective(sku);

            //新增stock
            Stock stock = new Stock();
            stock.setSkuId(sku.getId());
            stock.setStock(sku.getStock());
            stockMapper.insertSelective(stock);
        });
    }

    /**
     * 根据spuId查询spuDetail详情
     *
     * @param spuId
     * @return
     */
    @Override
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return spuDetailMapper.selectByPrimaryKey(spuId);
    }

    /**
     * 根据spuId查询所有符合条件的sku(包含stock数据)
     *
     * @param spuId
     * @return
     */
    @Override
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        List<Sku> skus = skuMapper.select(sku);
        skus.forEach(s -> {
            Stock stock = stockMapper.selectByPrimaryKey(s.getId());
            s.setStock(stock.getStock());
        });
        return skus;
    }

    /**
     * 修改指定商品的上下架状态
     * @param id
     */
    @Override
    public void changeSaleable(Long id) {
        Spu spu = this.spuMapper.selectByPrimaryKey(id);
        spu.setSaleable(!spu.getSaleable());
        this.spuMapper.updateByPrimaryKeySelective(spu);
        //this.sendMsg("update",id);
    }

    /**
     * 根据skuId查询对应的stock信息
     * @param skuId
     * @return
     */
    @Override
    public Stock queryStockBySkuId(Long skuId) {
        return this.stockMapper.selectByPrimaryKey(skuId);
    }

    /**
     * 更新库存信息
     * @param skuId
     * @param num
     * @param seckill
     */
    @Override
    public void updateStockBySkuId(Long skuId, Integer num, Boolean seckill) throws NullPointerException{
        Stock stock = this.stockMapper.selectByPrimaryKey(skuId);
        if(seckill){
            if(stock.getSeckillStock()>num){
                stock.setSeckillStock(stock.getSeckillStock()-num);
            }
        }else{
            if(stock.getStock()>num){
                stock.setStock(stock.getStock()-num);
            }
        }
        this.stockMapper.updateByPrimaryKey(stock);
    }
}
