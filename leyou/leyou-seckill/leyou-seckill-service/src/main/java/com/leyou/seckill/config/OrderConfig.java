package com.leyou.seckill.config;

import feign.Feign;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author 26747
 * @description OrderConfig
 * @date 2020/7/7 16:10
 */
@Configuration
public class OrderConfig {

    @Bean
    public Feign.Builder feignBuilder() {
        return Feign.builder().requestInterceptor(requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    String value = request.getHeader(name);
                    requestTemplate.header(name, value);
                }
            }
        });
    }
}
