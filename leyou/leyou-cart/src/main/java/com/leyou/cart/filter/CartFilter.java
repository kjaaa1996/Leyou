package com.leyou.cart.filter;


import javax.servlet.*;
import javax.servlet.annotation.WebFilter;

/**
 * @author 26747
 * @description CartFilter
 * @date 2020/6/22 20:31
 */
@WebFilter(filterName = "CartFilter", urlPatterns = {"/**"})
public class CartFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("过滤器初始化");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        System.out.println("具体过滤规则");
    }

    @Override
    public void destroy() {
        System.out.println("销毁");
    }
}
