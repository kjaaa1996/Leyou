package com.leyou;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import tk.mybatis.spring.annotation.MapperScan;

/**
 * @author 26747
 * @description LeyouItemApplication
 * @date 2020/5/13 17:58
 */
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.leyou.item.mapper")
@EnableSwagger2Doc
public class LeyouItemApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeyouItemApplication.class);
    }
}
