package com.leyou.goods.service;

/**
 * @author 26747
 * @description IGoodsHtmlService
 * @date 2020/6/1 14:28
 */
public interface IGoodsHtmlService {

    public void createHtml(Long spuId);

    public void asyncExecute(Long spuId);

    void deleteHtml(Long id);
}
