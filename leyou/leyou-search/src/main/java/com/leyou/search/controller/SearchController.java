package com.leyou.search.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.pojo.Goods;
import com.leyou.search.pojo.SearchRequest;
import com.leyou.search.pojo.SearchResult;
import com.leyou.search.service.ISearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;

/**
 * @author 26747
 * @description SearchController
 * @date 2020/5/26 10:02
 */
@Controller
public class SearchController {

    @Resource(name = "searchService")
    private ISearchService searchService;

    /**
     * 根据请求的数据(js格式，先封装为对象)，返回分页结果集
     *
     * @param searchRequest
     * @return
     */
    @PostMapping("/page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest searchRequest) {
        SearchResult result = searchService.search(searchRequest);
        //service方法中可能会传递一个null，所以要先判断是否为null
        if (result == null || CollectionUtils.isEmpty(result.getItems()))
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(result);
    }
}
