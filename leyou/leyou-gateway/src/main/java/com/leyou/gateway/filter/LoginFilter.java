package com.leyou.gateway.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author 26747
 * @description LoginFilter
 * @date 2020/6/5 20:55
 */
@Component("loginFilter")
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Resource
    private JwtProperties jwtProperties;

    @Resource
    private FilterProperties filterProperties;

    private static final Logger logger = LoggerFactory.getLogger(LoginFilter.class);

    /**
     * 过滤类型
     * pre：可以在请求被路由之前调用
     * route：在路由请求时候被调用
     * post：在route和error过滤器之后被调用
     * error：处理请求时发生错误时被调用
     */
    @Override
    public String filterType() {
        return "pre";   //前置过滤
    }

    //过滤优先级
    @Override
    public int filterOrder() {
        return 5;   //数字越小，优先级越高
    }

    //是否过滤
    @Override
    public boolean shouldFilter() {
        //1.获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        //2.获取request
        HttpServletRequest request = context.getRequest();
        //获取路径
        String requestURL = request.getRequestURI();
        //判断白名单
        //遍历允许访问的路径
        for (String path : this.filterProperties.getAllowPaths()
        ) {
            //判断是否符合
            if (requestURL.startsWith(path)) {
                return false;
            }
        }
        return true;   //是否执行该过滤器，此处为true，说明需要过滤
    }

    //过滤器的具体逻辑
    @Override
    public Object run() throws ZuulException {
        //1.获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        //2.获取request
        HttpServletRequest request = context.getRequest();
        //3.获取token
        String token = CookieUtils.getCookieValue(request, this.jwtProperties.getCookieName());
        //4.校验
        try {
            //校验通过不进行操作
            JwtUtils.getInfoFromToken(token, this.jwtProperties.getPublicKey());
        } catch (Exception e) {
            //校验出现异常，返回403
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(403);
            logger.error("非法访问，未登录，地址:{}", request.getRemoteHost(), e);
        }
        return null;
    }
}
