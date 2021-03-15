package com.leyou.seckill.controller;

import com.leyou.auth.pojo.UserInfo;
import com.leyou.seckill.interceptor.LoginInterceptor;
import com.leyou.seckill.pojo.SeckillGoods;
import com.leyou.seckill.pojo.SeckillMessage;
import com.leyou.seckill.pojo.SeckillParameter;
import com.leyou.seckill.service.ISeckillService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 26747
 * @description SeckillController
 * @date 2020/7/6 16:09
 */
@RestController
@Api("秒杀商品接口")
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {

    @Resource(name = "seckillService")
    private ISeckillService seckillService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String KEY_PREFIX = "leyou:seckill:stock";

    //设置标记位，当库存不足时直接返回不再访问redis
    private Map<Long, Boolean> localHashMap = new HashMap<>();

    /**
     * 系统初始化，初始化秒杀商品数量
     *
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        /*//第一次使用时，先从数据库查询，并保存在redis中
        //查询可以秒杀的商品
        List<SeckillGoods> seckillGoods = this.seckillService.querySeckillGoods(1, 0);
        if (CollectionUtils.isEmpty(seckillGoods)) {
            return;
        }
        //获取hash操作对象
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(KEY_PREFIX);
        //如果集合中存在关键字KEY_PREFIX，则删除关键字
        if (hashOps.hasKey(KEY_PREFIX)) {
            hashOps.delete(KEY_PREFIX);
        }
        //将获取到的可以秒杀的商品放入redis中
        seckillGoods.forEach(good -> {
            hashOps.put(good.getSkuId().toString(), good.getStock().toString());
        });*/


        //以后使用时直接访问redis，并将redis中的数据赋值给localHashMap
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(KEY_PREFIX);
        if (hashOps.hasKey(KEY_PREFIX)) {
            //默认将value设为false意味着商品还没有缺货，如果商品缺货后将其设为true
            hashOps.entries().forEach((m, n) -> localHashMap.put(Long.parseLong(m.toString()), false));
        }
    }

    /**
     * 新增秒杀商品
     *
     * @param seckillParameter
     * @return
     */
    @ApiOperation(value = "添加新的秒杀商品", notes = "新增秒杀")
    @ApiImplicitParam(value = "传递的秒杀商品信息，主要是id、count、discount", name = "seckillParameter", required = true, type = "com.leyou.seckill.pojo.SeckillParameter")
    @PostMapping("/addSeckill")
    public ResponseEntity<Void> addSeckillGoods(@RequestBody @Valid SeckillParameter seckillParameter) {
        if (seckillParameter != null) {
            this.seckillService.addSeckillGoods(seckillParameter);
        } else {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok().build();
    }

    /**
     * 分页查询秒杀商品列表
     *
     * @param page
     * @param rows
     * @return
     */
    @GetMapping("/list")
    @ApiOperation(value = "分页查询当前秒杀商品", notes = "分页查询当前秒杀商品")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "当前页", defaultValue = "1", type = "Integer"),
            @ApiImplicitParam(name = "rows", value = "每页大小", defaultValue = "5", type = "Integer")})
    public ResponseEntity<List<SeckillGoods>> querySeckillGoods(
            @RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "rows", defaultValue = "5") Integer rows) {
        List<SeckillGoods> seckillGoods = this.seckillService.querySeckillGoods(page, rows);
        if (CollectionUtils.isEmpty(seckillGoods)) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(seckillGoods);
    }

    /**
     * 秒杀商品并返回当前状态
     *
     * @param seckillGoods
     * @return
     */
    @PostMapping("/seck")
    @ApiOperation(value = "秒杀商品并返回当前状态", notes = "秒杀商品")
    @ApiImplicitParam(value = "传递的秒杀的商品信息", name = "seckillGoods", required = true, type = "com.leyou.seckill.pojo.SeckillGoods")
    public ResponseEntity<String> seckillOrder(@RequestBody @Valid SeckillGoods seckillGoods) {
        String waiting = "秒杀中...";

        //内存标记，减少对redis的访问
        boolean over = this.localHashMap.get(seckillGoods.getSkuId());
        if (over) {  //如果获取到商品的value为true，说明这件商品已经缺货
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();//未实现501
        }

        //读取redis缓存，将库存-1后更新缓存
        BoundHashOperations<String, Object, Object> hashOps = this.stringRedisTemplate.boundHashOps(KEY_PREFIX);
        String s = (String) hashOps.get(seckillGoods.getSkuId().toString());    //获取对应的库存信息
        if (s == null) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
        int stock = Integer.parseInt(s) - 1;
        if (stock < 0) {    //如果库存减一后小于0，说明库存不足
            this.localHashMap.put(seckillGoods.getSkuId(), true);
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
        }
        hashOps.delete(seckillGoods.getSkuId().toString());    //删除原来的库存记录
        hashOps.put(seckillGoods.getSkuId().toString(), String.valueOf(stock)); //将新的库存写入缓存

        //库存充足状态下，将请求发送到队列，附带用户信息
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        this.seckillService.sendMessage(new SeckillMessage(userInfo, seckillGoods));

        /*//为了缓解并发压力，不再此处直接调用创建订单，而是将秒杀请求传递到队列中让订单微服务按序处理
        Long id = this.seckillService.seckillOrder(seckillGoods);
        if (id == null) {
            return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();//未实现501
        }*/
        return ResponseEntity.ok(waiting);
    }

    /**
     * 根据userId查询订单号
     *
     * @param userId
     * @return
     */
    @GetMapping("orderId")
    @PostMapping("/checkSeckillOrder")
    @ApiOperation(value = "根据userId查询订单号", notes = "查询订单")
    @ApiImplicitParam(value = "根据userId查询订单号", name = "checkSeckillOrder", required = true, type = "Long")
    public ResponseEntity<Long> checkSeckillOrder(Long userId) {
        Long result = this.seckillService.checkSeckillOrder(userId);
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);

    }
}
