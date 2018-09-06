package com.rayfay.bizcloud.uaa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.ManagementServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationTrustResolver;
import org.springframework.security.authentication.AuthenticationTrustResolverImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.authentication.BindAuthenticator;
import org.springframework.security.ldap.search.FilterBasedLdapUserSearch;
import org.springframework.security.ldap.search.LdapUserSearch;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.rayfay.bizcloud.uaa.controller.RayfayAuthenticationProvider;
import com.rayfay.bizcloud.uaa.filter.AuthLogFilter;
import com.rayfay.bizcloud.uaa.filter.AuthLogWriter;
import com.rayfay.bizcloud.uaa.filter.CaptchaAuthenticationFilter;

/**
 * Created by ivor on 20/04/2017.
 */
@Configuration
@EnableWebSecurity
@Order(ManagementServerProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private DefaultSpringSecurityContextSource contextSource;
    @Autowired
    private UserDetailsContextMapper userDetailsContextMapper;
    
    @Autowired
    private AuthLogWriter authLogWriter;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/js/**","/css/**","/webjars/**","/bootstrap/**",
            "/uaa.css","/uaa.js","/vendor/**","/rstpwd*","/groups","/images/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new CaptchaAuthenticationFilter("/login", "/login?error"),UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(new AuthLogFilter(authLogWriter), CaptchaAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/login","/oauth/authorize","/oauth/confirm_access","/vcode","/login?error").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/home")
                .failureUrl("/login?error")
                //.successHandler(new TestShortSessionHandler()) //for test session timeout
                .permitAll()
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessHandler(logoutSuccessHandler("/login","redirect"))
                .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(myLdapAuthProvide());
    }

    private LogoutSuccessHandler logoutSuccessHandler(String defaultTargetUrl,String targetUrlParameter){
        SimpleUrlLogoutSuccessHandler logoutSuccessHandler = new SimpleUrlLogoutSuccessHandler();
        logoutSuccessHandler.setDefaultTargetUrl(defaultTargetUrl);
        logoutSuccessHandler.setTargetUrlParameter(targetUrlParameter);
        return logoutSuccessHandler;
    }
    @Bean
    public AuthenticationTrustResolver authenticationTrustResolver(){
        return new AuthenticationTrustResolverImpl();
    }

    @Bean
    public AuthenticationProvider myLdapAuthProvide(){
        LdapUserSearch userSearch = new FilterBasedLdapUserSearch("ou=people",
                "(&(objectclass=rfUserObjectClass)(|(uid={0})(rfAlias={0})))",contextSource);

        BindAuthenticator authenticator = new BindAuthenticator(contextSource);
        authenticator.setUserSearch(userSearch);
        RayfayAuthenticationProvider provider = new RayfayAuthenticationProvider(authenticator);
        provider.setUserDetailsContextMapper(userDetailsContextMapper);
        return provider;
    }
}
