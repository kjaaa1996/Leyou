package com.leyou.cart.config;

import com.leyou.cart.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author 26747
 * @description LeyouWebMvcConfiguration
 * @date 2020/6/8 10:24
 */
@Configuration
public class LeyouWebMvcConfiguration implements WebMvcConfigurer {

    @Resource(name = "loginInterceptor")
    private LoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }

//    @Bean(name="someFilterRegistration1")
//    public FilterRegistrationBean someFilterRegistration1(){
//        //新建过滤器注册类
//        FilterRegistrationBean registrationBean=new FilterRegistrationBean();
//        //添加写好的过滤器
//        registrationBean.setFilter(new CartFilter());
//        //设置过滤器的URL模式
//        registrationBean.addUrlPatterns("/*");
//        return registrationBean;
//    }
}
