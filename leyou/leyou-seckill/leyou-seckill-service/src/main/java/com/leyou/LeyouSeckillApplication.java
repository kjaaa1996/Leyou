package com.leyou;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author 26747
 * @description LeyouSeckillApplication
 * @date 2020/7/6 15:49
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.leyou.seckill.mapper")
@EnableSwagger2Doc
public class LeyouSeckillApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouSeckillApplication.class);
    }
}
