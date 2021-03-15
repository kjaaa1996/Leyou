package com.leyou.cart.config;

import com.leyou.auth.utils.RsaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @author 26747
 * @description JwtProperties
 * @date 2020/6/8 9:34
 */
@ConfigurationProperties(prefix = "leyou.jwt")
public class JwtProperties {
    private String pubKeyPath;
    private String cookieName;
    private PublicKey publicKey;

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    /**
     * 在构造方法执行之后执行
     */
    @PostConstruct
    public void init() {
        try {
            //获取公钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            logger.error("获取公钥失败!", e);
            throw new RuntimeException();
        }
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public static Logger getLogger() {
        return logger;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }
}
