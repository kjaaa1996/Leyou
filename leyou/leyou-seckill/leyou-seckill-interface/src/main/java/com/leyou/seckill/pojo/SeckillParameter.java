package com.leyou.seckill.pojo;

import java.util.Date;

/**
 * @author 26747
 * @description SeckillParameter 传递的秒杀参数
 * @date 2020/7/6 16:15
 */
public class SeckillParameter {
    private Long id;    //要秒杀的skuid
    private Date startTime; //开始时间
    private Date endTime;   //结束时间
    private Integer count;  //参与秒杀的数量
    private double discount;    //秒杀的折扣

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
