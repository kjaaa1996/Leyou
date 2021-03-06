package com.leyou.order.config;

import com.github.wxpay.sdk.WXPayConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.io.InputStream;

@Configuration
@EnableConfigurationProperties(PayProperties.class)
public class PayConfig implements WXPayConfig {

    @Resource
    private PayProperties payProperties;

    @Override
    public String getAppID() {
        return payProperties.getAppId();
    }

    @Override
    public String getMchID() {
        return payProperties.getMchId();
    }

    @Override
    public String getKey() {
        return payProperties.getKey();
    }

    @Override
    public InputStream getCertStream() {
        return null;
    }

    @Override
    public int getHttpConnectTimeoutMs() {
        return payProperties.getConnectTimeoutMs();
    }

    @Override
    public int getHttpReadTimeoutMs() {
        return payProperties.getReadTimeoutMs();
    }

}
