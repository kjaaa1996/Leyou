package com.leyou.search.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.pojo.Spu;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;

/**
 * @author 26747
 * @description ISearchService
 * @date 2020/5/25 16:13
 */
public interface ISearchService {
    public Goods buildGoods(Spu spu) throws JsonProcessingException;

    SearchResult search(SearchRequest searchRequest);

    void save(Long id) throws JsonProcessingException;

    void delete(Long id) throws JsonProcessingException;
}
