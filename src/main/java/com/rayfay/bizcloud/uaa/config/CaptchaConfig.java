package com.rayfay.bizcloud.uaa.config;

import java.util.Properties;

import com.rayfay.bizcloud.uaa.controller.LoginAttemptCacheService;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCacheFactoryBean;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;

@Configuration  
public class CaptchaConfig {
    @Bean(name="captchaProducer")  
    public DefaultKaptcha getKaptchaBean(){  
        DefaultKaptcha defaultKaptcha=new DefaultKaptcha();  
        Properties properties=new Properties();  
        properties.setProperty("kaptcha.border", "yes");  
        properties.setProperty("kaptcha.border.color", "105,179,90");  
        properties.setProperty("kaptcha.textproducer.font.color", "blue");  
        properties.setProperty("kaptcha.image.width", "125");  
        properties.setProperty("kaptcha.image.height", "45");  
        properties.setProperty("kaptcha.session.key", "code");  
        properties.setProperty("kaptcha.textproducer.char.length", "4");  
        properties.setProperty("kaptcha.textproducer.font.names", "宋体,楷体,微软雅黑");          
        Config config=new Config(properties);  
        defaultKaptcha.setConfig(config);  
        return defaultKaptcha;
    }

    @Bean
    public LoginAttemptCacheService captchaPolicy(){
        return new LoginAttemptCacheService(3,captchaCache());
    }


    private Cache captchaCache() {
        ConcurrentMapCacheFactoryBean localCache = new ConcurrentMapCacheFactoryBean();
        localCache.setName("captchaCache");
        localCache.afterPropertiesSet();
        return localCache.getObject();
    }
}
