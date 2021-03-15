package com.leyou.common.pojo;

import java.util.List;

/**
 * @author 26747
 * @description PageResult
 * @date 2020/5/16 11:08
 */
//封装一个集合类，来表示分页结果，其中当前页数据是一个集合
public class PageResult<T> {//为了后续使用使用泛型
    private Long total;//总条数
    private Integer totalPage;//总页数
    private List<T> items;//当前页数据，使用泛型来保证后续使用

    public PageResult() {
    }

    public PageResult(Long total, List<T> items) {
        this.total = total;
        this.items = items;
    }

    public PageResult(Long total, Integer totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
