package com.leyou;

import com.spring4all.swagger.EnableSwagger2Doc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * @author 26747
 * @description LeyouGatewayApplication
 * @date 2020/5/13 17:08
 */
@SpringBootApplication
@EnableDiscoveryClient //启用eureka
@EnableZuulProxy//启用zuul组件
@EnableSwagger2Doc
public class LeyouGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(LeyouGatewayApplication.class);
    }
}
