package com.leyou.gateway.config;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 26747
 * @description DocumentationConfig
 * @date 2020/7/7 15:35
 */
@Component
@Primary
public class DocumentationConfig implements SwaggerResourcesProvider {
    @Override
    public List<SwaggerResource> get() {
        List<SwaggerResource> resources = new ArrayList<>();
        resources.add(swaggerResource("订单接口", "/api/order-service/v2/api-docs"));
        resources.add(swaggerResource("秒杀接口", "/api/seckill-service/v2/api-docs"));
        resources.add(swaggerResource("商品接口", "/api/item-service/v2/api-docs"));

        return resources;
    }

    private SwaggerResource swaggerResource(String name, String location) {
        SwaggerResource swaggerResource = new SwaggerResource();
        swaggerResource.setName(name);
        swaggerResource.setLocation(location);
        swaggerResource.setSwaggerVersion("1.0");
        return swaggerResource;
    }
}
