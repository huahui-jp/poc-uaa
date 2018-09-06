package com.rayfay.bizcloud.uaa.config;

import com.rayfay.bizcloud.uaa.controller.AliasChecker;
import com.rayfay.bizcloud.uaa.handler.RayfayRedirectResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.provider.endpoint.AuthorizationEndpoint;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.stereotype.Component;

/**
 * Created by July on 2018/7/17.
 */
@Configuration
public class AuthenticationEndpointBeanPostProcessor implements BeanPostProcessor {
    private static final Logger logger = LoggerFactory.getLogger(AliasChecker.class);

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof AuthorizationEndpoint){
            logger.info("process AuthorizationEndpoint. set redirect Resolver to RayfayRedirectResolver");
            RedirectResolver redirectResolver = new RayfayRedirectResolver();
            ((AuthorizationEndpoint) bean).setRedirectResolver(redirectResolver);
        }
        return bean;
    }
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}
