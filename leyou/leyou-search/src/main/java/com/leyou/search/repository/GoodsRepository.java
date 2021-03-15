package com.leyou.search.repository;

import com.leyou.search.pojo.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Component;

/**
 * @author 26747
 * @description GoodsRepository
 * @date 2020/5/25 19:31
 */
@Component("goodsRepository")
public interface GoodsRepository extends ElasticsearchRepository<Goods, Long> {
}
