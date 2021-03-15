package com.leyou;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author 26747
 * @description LeyouAuthServiceApplication
 * @date 2020/6/5 10:23
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
//@ConfigurationPropertiesScan("com.leyou.auth.config")
public class LeyouAuthServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LeyouAuthServiceApplication.class);
    }
}
