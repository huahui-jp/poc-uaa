package com.rayfay.bizcloud.uaa.respository;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.rayfay.bizcloud.uaa.entity.RayfayClientDetails;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
/**
 * Created by ivor on 20/04/2017.
 */
@Component
public class RayfayClientDetailsContextMapper extends AbstractContextMapper<ClientDetails> {
    public ClientDetails doMapFromContext(DirContextOperations ctx) {
        String clientId = ctx.getStringAttribute("uid");
        String clientSecret = ctx.getStringAttribute("rfAppSecret");
        String[] accessGroupDNs = ctx.getStringAttributes("rfAccessGroup");
        String appName = ctx.getStringAttribute("rfAppName");
        String redirectUris = ctx.getStringAttribute("rfAppURI");
        String resourceIds = ctx.getStringAttribute("rfResourceIds");
        String scopes = ctx.getStringAttribute("rfScopes");
        String grantTypes = ctx.getStringAttribute("rfGrandTypes");
        String authorities = ctx.getStringAttribute("rfAuthorities");
        String autoApprove = ctx.getStringAttribute("rfAutoApprove");
        String allowAttributes = ctx.getStringAttribute("rfAllowAttributes");
        String enable = ctx.getStringAttribute("rfEnable");
        String enableSLO = ctx.getStringAttribute("rfEnableSLO");
        String appDesc = ctx.getStringAttribute("rfAppDesc");
        Integer accessTokenValidity = (Integer) ctx.getObjectAttribute("rfAccessTokenValidity");
        Integer refreshTokenValidity = (Integer) ctx.getObjectAttribute("rfRefreshTokenValidity");
        // String additionInformation =
        // ctx.getStringAttribute("rfAdditionInformation");

        RayfayClientDetails client = new RayfayClientDetails(clientId, resourceIds, scopes, grantTypes, authorities,
                redirectUris);
        client.setClientSecret(clientSecret);
        client.setAppName(appName);
        client.setAppDesc(appDesc);
        if (accessGroupDNs != null && accessGroupDNs.length > 0) {
            Set<String> accessGroups = Stream.of(accessGroupDNs)
                    .map(s -> LdapUtils.getStringValue(LdapUtils.newLdapName(s), "cn")).collect(Collectors.toSet());
            if (!accessGroups.isEmpty()) {
                client.setAccessGroups(accessGroups);
            }
        }

        if (StringUtils.hasText(allowAttributes)) {
            Set<String> allowAttributeList = StringUtils.commaDelimitedListToSet(allowAttributes);
            if (!allowAttributeList.isEmpty()) {
                client.setAllowAttributes(allowAttributeList);
            }
        }

        client.setEnable(enable);
        client.setEnableSLO(enableSLO);
        if (autoApprove != null) {
            client.setAutoApproveScopes(StringUtils.commaDelimitedListToSet(autoApprove));
        }
        client.setAccessTokenValiditySeconds(accessTokenValidity);
        client.setRefreshTokenValiditySeconds(refreshTokenValidity);
        // client.setAdditionalInformation(null);
        return client;
    }
}
