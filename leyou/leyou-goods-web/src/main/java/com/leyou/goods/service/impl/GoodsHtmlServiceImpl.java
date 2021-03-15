package com.leyou.goods.service.impl;

import com.leyou.goods.service.IGoodsHtmlService;
import com.leyou.goods.service.IGoodsService;
import com.leyou.goods.utils.ThreadUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.annotation.Resource;
import java.io.File;
import java.io.PrintWriter;


/**
 * 用于生成本地静态页面
 *
 * @author 26747
 * @description GoodsHtmlServiceImpl
 * @date 2020/6/1 14:28
 */
@Service("goodsHtmlService")
public class GoodsHtmlServiceImpl implements IGoodsHtmlService {

    @Resource
    private TemplateEngine engine;

    //需要引入controller使用的方法
    @Resource(name = "goodsService")
    private IGoodsService goodsService;

    private static final Logger LOGGER = LoggerFactory.getLogger(IGoodsHtmlService.class);

    /**
     * 创建或更新商品详情页的静态页面
     *
     * @param spuId
     */
    public void createHtml(Long spuId) {
        //初始化上下文对象Context(thymeleaf)
        Context context = new Context();
        //设置数据模型
        context.setVariables(this.goodsService.loadData(spuId));
        //创建输出流
        PrintWriter writer = null;
        try {
            //定义输出文件
            File file = new File("E:\\leyou-tools\\nginx-1.18.0\\html\\item\\" + spuId + ".html");
            writer = new PrintWriter(file);
            //调用TemplateEngine执行页面静态化方法，需要自定义的template模版，上下文对象，输出流
            this.engine.process("item", context, writer);
        } catch (Exception e) {
            LOGGER.error("页面静态化出错：{}," + e, spuId);
        } finally {
            if (writer != null)
                writer.close();
        }
    }

    /**
     * 新建线程处理页面静态化
     *
     * @param spuId
     */
    @Override
    public void asyncExecute(Long spuId) {
        ThreadUtils.execute(() -> createHtml(spuId));
        //等同于下面的方法
        /*ThreadUtils.execute(new Runnable() {
            @Override
            public void run() {
                createHtml(spuId);
            }
        });*/
    }

    /**
     * 删除html页面
     *
     * @param id
     */
    @Override
    public void deleteHtml(Long id) {
        File file = new File("E:\\leyou-tools\\nginx-1.18.0\\html\\item\\" + id + ".html");
        file.deleteOnExit();
    }
}
