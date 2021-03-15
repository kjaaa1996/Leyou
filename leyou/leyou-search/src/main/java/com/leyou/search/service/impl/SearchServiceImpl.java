package com.leyou.search.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leyou.item.pojo.*;
import com.leyou.search.client.BrandClient;
import com.leyou.search.client.CategoryClient;
import com.leyou.search.client.GoodsClient;
import com.leyou.search.client.SpecificationClient;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.repository.GoodsRepository;
import com.leyou.search.service.ISearchService;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 26747
 * @description SearchServiceImpl
 * @date 2020/5/25 16:14
 */
@Service("searchService")
public class SearchServiceImpl implements ISearchService {

    @Resource
    private CategoryClient categoryClient;
    @Resource
    private BrandClient brandClient;
    @Resource
    private GoodsClient goodsClient;
    @Resource
    private SpecificationClient specificationClient;
    @Resource(name = "goodsRepository")
    private GoodsRepository goodsRepository;
    //序列化为json
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将Spu数据转换为Goods数据
     *
     * @param spu
     * @return
     */
    @Override
    public Goods buildGoods(Spu spu) throws JsonProcessingException {
        //根据分类的id查询分类名称
        List<String> strings = this.categoryClient.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
        //根据品牌id查询品牌
        Brand brand = this.brandClient.queryBrandById(spu.getBrandId());
        //根据spuId获取所有的sku
        List<Sku> skus = this.goodsClient.querySkusBySpuId(spu.getId());
        //初始化价格集合
        List<Long> prices = new ArrayList<>();
        //收集sku的必要信息
        List<Map<String, Object>> skuList = new ArrayList<>();
        //将价格添加到集合中
        skus.forEach(sku -> {
            prices.add(sku.getPrice());
            Map<String, Object> map = new HashMap<>();
            map.put("id", sku.getId());
            map.put("title", sku.getTitle());
            map.put("price", sku.getPrice());
            //获取sku中的图片信息，为空则置空，非空则取首,逗号","为分割
            map.put("image", StringUtils.isBlank(sku.getImages()) ? "" : StringUtils.split(sku.getImages(), ",")[0]);
            skuList.add(map);
        });
        //根据spu中的cid3查询出所有的搜索规格参数
        List<SpecParam> params = this.specificationClient.queryParams(null, spu.getCid3(), null, true);
        //根据spuId查询spuDetail
        SpuDetail spuDetail = this.goodsClient.querySpuDetailBySpuId(spu.getId());
        //把通用的规格参数值进行反序列化
        Map<String, Object> genericSpecMaps = MAPPER.readValue(spuDetail.getGenericSpec(), new TypeReference<Map<String, Object>>() {
        });
        //对特殊的规格参数值进行反序列化
        Map<String, List<Object>> specialSpecMaps = MAPPER.readValue(spuDetail.getSpecialSpec(), new TypeReference<Map<String, List<Object>>>() {
        });
        //创建map对象用于存储{name:value}格式的规格参数
        Map<String, Object> spec = new HashMap<>();
        params.forEach(specParam -> {
            //判断规格参数的类型是否是通用的规格参数
            if (specParam.getGeneric()) {
                //如果是通用类型的参数，则从genericSpecMaps获取数据
                String value = genericSpecMaps.get(specParam.getId().toString()).toString();
                //判断是否是数值区间，如果是返回一个区间
                if (specParam.getNumeric()) {
                    value = chooseSegment(value, specParam);
                }
                spec.put(specParam.getName(), value);
            } else {
                //如果是特殊的规格参数，则从specialSpecMaps中获取数据
                List<Object> value = specialSpecMaps.get(specParam.getId().toString());
                //特殊的规格参数不可能是区间，不需要判断
                spec.put(specParam.getName(), value);
            }
        });

        Goods goods = new Goods();
        goods.setId(spu.getId());
        goods.setCid1(spu.getCid1());
        goods.setCid2(spu.getCid2());
        goods.setCid3(spu.getCid3());
        goods.setBrandId(spu.getBrandId());
        goods.setCreateTime(spu.getCreateTime());
        goods.setSubTitle(spu.getSubTitle());
        //拼接all字段，需要分类名称以及品牌名称
        goods.setAll(spu.getTitle() + " " + StringUtils.join(strings, " ") + " " + brand.getName());
        //获取spu下的所有sku的价格
        goods.setPrice(prices);
        //获取spu下的所有sku的必要信息，并转化为json字符串
        goods.setSkus(MAPPER.writeValueAsString(skuList));
        //获取所有查询的规格参数{name:value}
        goods.setSpecs(spec);

        return goods;
    }

    /**
     * 根据请求的数据(js格式，先封装为对象)，返回分页结果集
     *
     * @param searchRequest
     * @return
     */
    @Override
    public SearchResult search(SearchRequest searchRequest) {
        //先判断传递的请求参数中key字段是否为空
        String key = searchRequest.getKey();
        if (StringUtils.isBlank(key)) {
            return null;
        }
        //自定义查询构建器
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加查询条件
        //1.对key进行全文查找,此处的查询条件为基本条件，应当抽取出来以供别处使用
        //QueryBuilder basicQuery = QueryBuilders.matchQuery("all", key).operator(Operator.AND);
        //当有过滤之后使用boolQueryBuilder来进行过滤
        BoolQueryBuilder basicQuery = buildBoolQueryBuilder(searchRequest);
        queryBuilder.withQuery(basicQuery);
        //2.分页
        //准备分页参数,在pageRequest中分页页码从0开始
        Integer page = searchRequest.getPage();
        Integer size = searchRequest.getSize();
        queryBuilder.withPageable(PageRequest.of(page - 1, size));

        //3.通过sourceFilter设置返回的结果字段，我们只需要spuId、skus、subTitle
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id", "skus", "subTitle"}, null));

        //4.排序,根据排序字段和是否排序决定排序
        String sortBy = searchRequest.getSortBy();
        Boolean descending = searchRequest.getDescending();
        if (StringUtils.isNotBlank(sortBy)) {
            queryBuilder.withSort(SortBuilders.fieldSort(sortBy).order(descending ? SortOrder.DESC : SortOrder.ASC));
        }

        //添加分类和品牌的聚合
        String categoryAggName = "categories";
        String brandAggName = "brands";
        queryBuilder.addAggregation(AggregationBuilders.terms(categoryAggName).field("cid3"));
        queryBuilder.addAggregation(AggregationBuilders.terms(brandAggName).field("brandId"));

        //查询获取结果
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) goodsRepository.search(queryBuilder.build());

        //获取聚合结果集并解析
        List<Map<String, Object>> categories = getCategoryAggResult(goodsPage.getAggregation(categoryAggName));
        List<Brand> brands = getBrandAggResult(goodsPage.getAggregation(brandAggName));

        List<Map<String, Object>> specs = null;
        //先进行判断，只有一个分类的情况下才进规格参数的聚合
        if (!CollectionUtils.isEmpty(categories) && categories.size() == 1) {
            //对规格参数进行聚合,根据分类id和基本的查询条件获取规格参数集合
            specs = getParamAggResult((Long) categories.get(0).get("id"), basicQuery);
        }

        //封装结果并返回
        return new SearchResult(goodsPage.getTotalElements(), goodsPage.getTotalPages(), goodsPage.getContent(), categories, brands, specs);
    }

    /**
     * 构建布尔查询
     *
     * @param searchRequest
     * @return
     */
    private BoolQueryBuilder buildBoolQueryBuilder(SearchRequest searchRequest) {
        //初始化boolQueryBuilder
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //给boolQueryBuilder添加基本查询条件
        boolQueryBuilder.must(QueryBuilders.matchQuery("all", searchRequest.getKey()).operator(Operator.AND));
        //添加过滤条件
        //1.获取用户选择的过滤信息
        Map<String, Object> filter = searchRequest.getFilter();
        for (Map.Entry<String, Object> entry : filter.entrySet()) {
            //判断获取到的是品牌id还是分类的cid3或规格参数名
            String key = entry.getKey();
            //获取key对应的value，以便处理
            Object value = entry.getValue();
            //设置数值区间判断条件，此处不需要，因为传递的数值范围判断条件直接就是一个范围的关键字，可以直接判断
            String regex = "^(\\d+\\.?\\d*)-(\\d+\\.?\\d*)$";
            if (StringUtils.equals("品牌", key)) {
                key = "brandId";
            } else if (StringUtils.equals("分类", key)) {
                key = "cid3";
            } else if (StringUtils.equals("price", key)) {   //如果是价格进行匹配，要考虑是否有以上两个字，否则将其分为两部分
                String val = String.valueOf(value);
                if (val.contains("元以上")) {
                    String num = StringUtils.substringBefore(val, "元以上");
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery(key).gte(Double.parseDouble(num) * 100));
                } else {
                    String[] nums = StringUtils.substringBefore(val, "元").split("-");
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery(key).gte(Double.parseDouble(nums[0]) * 100).lt(Double.parseDouble(nums[1]) * 100));
                }
                break;
            } else {    //规格参数名在进行过滤，要添加上头尾信息，可以直接过滤区间
                key = "specs." + key + ".keyword";
            }
            boolQueryBuilder.filter(QueryBuilders.termQuery(key, value));
        }
        //返回boolQueryBuilder
        return boolQueryBuilder;
    }

    /**
     * 根据查询条件聚合规格参数
     *
     * @param cid
     * @param basicQuery
     * @return
     */
    private List<Map<String, Object>> getParamAggResult(Long cid, QueryBuilder basicQuery) {
        //自定义查询对象构建
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();
        //添加基本查询条件
        queryBuilder.withQuery(basicQuery);

        //1.查询要聚合的规格参数,不关心组id和是否为通用字段，只要是这个分类的搜索字段都要查询
        List<SpecParam> params = this.specificationClient.queryParams(null, cid, null, true);

        //2.添加规格参数的聚合
        params.forEach(param -> {
            //根据字段进行聚合，terms(聚合名称),field(聚合字段)，聚合不需要返回值
            // 聚合字段参照kibana书写，中间的字段名称跟随param的改变而改变，其余不变，keyword代表不进行分词
            queryBuilder.addAggregation(AggregationBuilders.terms(param.getName()).field("specs." + param.getName() + ".keyword"));
        });

        //3.添加结果集过滤，不包含任何字段，没有不包含字段，可以过滤掉普通结果集
        queryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{}, null));

        //4.执行查询,获取聚合结果集
        AggregatedPage<Goods> goodsPage = (AggregatedPage<Goods>) this.goodsRepository.search(queryBuilder.build());

        //初始化返回集
        List<Map<String, Object>> specs = new ArrayList<>();
        //5.解析聚合结果集，map的key为聚合名称(规格参数名)，value为聚合对象
        Map<String, Aggregation> aggregationMap = goodsPage.getAggregations().asMap();
        //遍历解析聚合结果集
        for (Map.Entry<String, Aggregation> aggregationEntry : aggregationMap.entrySet()) {
            //初始化一个map，{k:规格参数名,options:聚合的规格参数值数组}
            Map<String, Object> map = new HashMap<>();
            map.put("k", aggregationEntry.getKey());
            //解析规格参数的聚合结果集
            //先将聚合强转为对应类型
            StringTerms terms = (StringTerms) aggregationEntry.getValue();
            //获取桶的集合
            List<String> options = new ArrayList<>();
            terms.getBuckets().forEach(bucket -> {
                options.add(bucket.getKeyAsString());
            });
            map.put("options", options);
            specs.add(map);
        }
        return specs;
    }


    /**
     * 解析品牌的聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Brand> getBrandAggResult(Aggregation aggregation) {
        //先将聚合强转为对应类型的
        LongTerms terms = (LongTerms) aggregation;

        //获取聚合中的桶
        return terms.getBuckets().stream().map(bucket -> {
            return this.brandClient.queryBrandById(bucket.getKeyAsNumber().longValue());
        }).collect(Collectors.toList());
    }

    /**
     * 解析分类的聚合结果集
     *
     * @param aggregation
     * @return
     */
    private List<Map<String, Object>> getCategoryAggResult(Aggregation aggregation) {
        //先将聚合强转为对应类型的
        LongTerms terms = (LongTerms) aggregation;

        //获取聚合中的桶，遍历桶的集合将其转化为List<Map<String, Object>>
        return terms.getBuckets().stream().map(bucket -> {
            //初始化一个map
            Map<String, Object> map = new HashMap<>();
            //获取桶中的分类id
            long id = bucket.getKeyAsNumber().longValue();
            //根据id获取name集合
            List<String> names = categoryClient.queryNamesByIds(Arrays.asList(id));
            //将数据存储进map
            map.put("id", id);
            map.put("name", names.get(0));
            return map;
        }).collect(Collectors.toList());

    }

    /**
     * 创建一个方法，用来判断规格参数是否在区间中
     *
     * @param value
     * @param specParam
     * @return
     */
    private static String chooseSegment(String value, SpecParam specParam) {
        //创建返回值s
        String result = "其他";
        //将value转换为Double类型
        double val = NumberUtils.toDouble(value);
        //先将specParam按照逗号","分割开
        for (String segment : specParam.getSegments().split(",")
        ) {
            //再将seg按照"-"分割开
            String[] segs = segment.split("-");
            double begin = NumberUtils.toDouble(segs[0]);
            double end = Double.MAX_VALUE;
            //如果分割后的元素个数为2，则将第二个元素赋给end
            if (segs.length == 2) {
                end = NumberUtils.toDouble(segs[1]);
            }
            //判断是否在范围内
            if (val >= begin && val <= end) {
                if (segs.length == 1) {
                    result = segs[0] + specParam.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + specParam.getUnit() + "以下";
                } else {
                    result = segment + specParam.getUnit();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 根据消息队列传递的信息保存或更新
     *
     * @param id
     */
    @Override
    public void save(Long id) throws JsonProcessingException {
        Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = this.buildGoods(spu);
        this.goodsRepository.save(goods);
    }

    /**
     * 根据消息队列传递的信息删除
     *
     * @param id
     */
    @Override
    public void delete(Long id) throws JsonProcessingException {
        /*Spu spu = this.goodsClient.querySpuById(id);
        Goods goods = this.buildGoods(spu);
        this.goodsRepository.delete(goods);*/
        this.goodsRepository.deleteById(id);
    }
}
