package com.rayfay.bizcloud.uaa.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;

/**
 * Created by ivor on 20/04/2017.
 */
@Configuration
public class LdapConfig {
    @Value("${rayfay.ldap.providerUrl}")
    private String providerUrl;
    @Value("${rayfay.ldap.managerDn}")
    private String managerDn;
    @Value("${rayfay.ldap.managerPass}")
    private String managerPass;

    @Bean
    public DefaultSpringSecurityContextSource contextSource() {
        DefaultSpringSecurityContextSource contextSource
                = new DefaultSpringSecurityContextSource(providerUrl);
        contextSource.setUserDn(managerDn);
        contextSource.setPassword(managerPass);
        return contextSource;

    }

    @Bean
    public LdapTemplate ldapTemplate(ContextSource contextSource) {
        return new LdapTemplate(contextSource);
    }
}
