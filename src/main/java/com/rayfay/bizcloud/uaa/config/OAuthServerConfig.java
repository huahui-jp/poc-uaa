package com.rayfay.bizcloud.uaa.config;

import com.rayfay.bizcloud.uaa.entity.RayfayClientDetails;
import com.rayfay.bizcloud.uaa.entity.RayfayUserDetails;
import com.rayfay.bizcloud.uaa.respository.RayfayClientDetailsContextMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import java.security.KeyPair;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ivor on 20/04/2017.
 */
@Configuration
@EnableAuthorizationServer
public class OAuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RayfayClientDetailsContextMapper rfServiceContextMapper;

    @Autowired
    private ClientDetailsService clientDetailsService;

    static class AccessTokenConverter extends DefaultAccessTokenConverter {
        @Autowired
        private ClientDetailsService clientDetailsService;
        public Map<String, ?> convertAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
            @SuppressWarnings("unchecked")
            Map<String, Object> response = (Map<String, Object>) super.convertAccessToken(token, authentication);
            RayfayClientDetails clientDetails = (RayfayClientDetails) clientDetailsService.loadClientByClientId(authentication.getOAuth2Request().getClientId());
            if (authentication.getPrincipal() instanceof RayfayUserDetails) {
                Map<String,Object> userAttr = ((RayfayUserDetails) authentication.getPrincipal()).getAttrs();
                Set<String> allowedAttr = clientDetails.getAllowAttributes();
                if(allowedAttr != null)
                    allowedAttr.stream().forEach(s -> response.put(s,userAttr.getOrDefault(s,"")));
            }
            return response;
        }
    }
    @Bean
    public AccessTokenConverter rayfayAccessTokenConverter(){
        return new AccessTokenConverter();
    }

//    @Bean
//    public OAuth2RequestValidator requestValidator(){
//        return new RayfayOAuth2RequestValidator();
//    }
//
//
//    @Bean
//    public WebResponseExceptionTranslator exceptionTranslator(){
//        return new RayfayWebResponseExceptionTranslator();
//    }

    @Bean
    public JwtAccessTokenConverter jwtAccessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        KeyPair keyPair = new KeyStoreKeyFactory(new ClassPathResource("keystore.jks"), "foobar".toCharArray())
                .getKeyPair("test");
        converter.setKeyPair(keyPair);
        converter.setAccessTokenConverter(rayfayAccessTokenConverter());
        return converter;
    }

    @Bean
    public ClientDetailsService clientDetailsService(){
        return new ClientDetailsService() {
            @Autowired
            private LdapTemplate ldapTemplate;

            @Override
            public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
                AndFilter filter = new AndFilter();
                filter.and(new EqualsFilter("objectclass", "rfServiceObjectClass"));
                filter.and(new EqualsFilter("uid", clientId));
                List<ClientDetails> clients = ldapTemplate.search("ou=services", filter.encode(),
                        rfServiceContextMapper);
                if (clients != null && clients.size() == 1) {
                    ClientDetails client =  clients.get(0);
                    if(((RayfayClientDetails)client).isEnable())
                        return client;
                    else
                        throw new ClientRegistrationException("应用已下架");
                }
                else
                    throw new ClientRegistrationException("应用不存在");
            }
        };
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService);
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        endpoints.authenticationManager(authenticationManager)
                .accessTokenConverter(jwtAccessTokenConverter())
                .userApprovalHandler(null);
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");
    }

}