package com.leyou.search.pojo;

import java.util.Map;

/**
 * @author 26747
 * @description SearchRequest
 * @date 2020/5/26 9:55
 */
public class SearchRequest {
    private String key;         //搜索条件
    private Integer page;       //当前页
    private String sortBy;      //排序字段
    private Boolean descending; //是否排序
    private Map<String,Object> filter;  //过滤条件


    private static final Integer DEFAULT_SIZE = 20;   //每页大小，不从页面接收，而是固定大小
    private static final Integer DEFAULT_PAGE = 1;    //默认页

    public Map<String, Object> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public Boolean getDescending() {
        return descending;
    }

    public void setDescending(Boolean descending) {
        this.descending = descending;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        //获取页码时进行校验，不能小于1
        return Math.max(DEFAULT_PAGE, page);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return DEFAULT_SIZE;
    }
}
