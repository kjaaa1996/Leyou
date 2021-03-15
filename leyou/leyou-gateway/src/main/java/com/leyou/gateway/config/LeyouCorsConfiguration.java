package com.leyou.gateway.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;


/**
 * @author 26747
 * @description LeyouCorsConfiguration
 * @date 2020/5/15 20:28
 */
//专门用于处理跨域请求的配置类
@Configuration
public class LeyouCorsConfiguration {

    //注意此处CorsFilter是springframework的类
    @Bean
    public CorsFilter corsFilter() {
        //1.初始化cors配置对象
        CorsConfiguration configuration = new CorsConfiguration();
        //1.1允许的域名，不要写*，否则cookie无法使用，set的方法要使用集合
        configuration.addAllowedOrigin("http://manage.leyou.com");
        configuration.addAllowedOrigin("http://www.leyou.com");
        //1.2是否发送Cookie信息
        configuration.setAllowCredentials(true);
        //1.3设置允许的请求方式
//        configuration.addAllowedMethod("OPTIONS");
//        configuration.addAllowedMethod("HEAD");
//        configuration.addAllowedMethod("GET");
//        configuration.addAllowedMethod("PUT");
//        configuration.addAllowedMethod("POST");
//        configuration.addAllowedMethod("DELETE");
//        configuration.addAllowedMethod("PATCH");
        //允许所有请求方式
        configuration.addAllowedMethod("*");
        //1.4设置允许一切头信息
        configuration.addAllowedHeader("*");

        //2.初始化cors配置源对象
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        //2.1添加映射路径，设置要拦截一切请求，并添加cors配置
        configurationSource.registerCorsConfiguration("/**", configuration);

        //3.返回新的CorsFilter并配入配置源
        return new CorsFilter(configurationSource);

    }
}
