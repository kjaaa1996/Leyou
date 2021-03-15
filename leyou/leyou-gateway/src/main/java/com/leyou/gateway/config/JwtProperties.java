package com.leyou.gateway.config;

import com.leyou.auth.utils.RsaUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;
import java.security.PublicKey;

/**
 * @author 26747
 * @description JwtProperties
 * @date 2020/6/5 20:48
 */
@ConfigurationProperties(prefix = "leyou.jwt")
public class JwtProperties {
    private String pubKeyPath;  //公钥

    private PublicKey publicKey;    //公钥

    private String cookieName;      //cookie名称

    private static final Logger logger = LoggerFactory.getLogger(JwtProperties.class);

    /**
     * 初始化方法，获取公钥
     */
    @PostConstruct
    public void init() {
        try {
            //获取公钥
            this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        } catch (Exception e) {
            logger.error("获取公钥失败！", e);
            throw new RuntimeException();
        }
    }

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public static Logger getLogger() {
        return logger;
    }
}
