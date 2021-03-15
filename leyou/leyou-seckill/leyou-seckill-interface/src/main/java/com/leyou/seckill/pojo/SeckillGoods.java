package com.leyou.seckill.pojo;

import javax.persistence.*;
import java.util.Date;

/**
 * @author 26747
 * @description SeckillGoods
 * @date 2020/7/6 16:01
 */
@Table(name = "tb_seckill_sku")
public class SeckillGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //id主键
    private Long skuId;  //商品ID
    private Date startTime; //开始时间
    private Date endTime;   //结束时间
    private Long seckillPrice;    //秒杀价格
    private String title;   //标题
    private String image;   //图片
    private Boolean enable; //是否可以秒杀

    @Transient
    private Integer stock;  //秒杀库存
    @Transient
    private Integer seckillTotal;   //秒杀总数

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Long getSeckillPrice() {
        return seckillPrice;
    }

    public void setSeckillPrice(Long seckillPrice) {
        this.seckillPrice = seckillPrice;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getEnable() {
        return enable;
    }

    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getSeckillTotal() {
        return seckillTotal;
    }

    public void setSeckillTotal(Integer seckillTotal) {
        this.seckillTotal = seckillTotal;
    }
}
