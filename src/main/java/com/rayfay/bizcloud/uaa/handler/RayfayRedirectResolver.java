package com.rayfay.bizcloud.uaa.handler;

import com.rayfay.bizcloud.uaa.entity.RayfayClientDetails;
import com.rayfay.bizcloud.uaa.entity.RayfayUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.DefaultRedirectResolver;
import org.springframework.web.util.HtmlUtils;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by July on 2018/7/17.
 */
public class RayfayRedirectResolver extends DefaultRedirectResolver {
    @Override
    public String resolveRedirect(String s, ClientDetails clientDetails) throws OAuth2Exception {
        if(!checkUserCanAccessClient(clientDetails)){
            throw new InvalidRequestException("没有访问该应用的权限.");
        }
        if(!s.equals(HtmlUtils.htmlEscape(s))){
            throw new InvalidRequestException("redirect_uri 包含非法参数");
        }
        return super.resolveRedirect(s, clientDetails);
    }

    private boolean checkUserCanAccessClient(ClientDetails clientDetails) throws InvalidScopeException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null && authentication.getPrincipal() instanceof RayfayUserDetails && clientDetails instanceof RayfayClientDetails){
            RayfayUserDetails user = (RayfayUserDetails)authentication.getPrincipal();
            RayfayClientDetails client = (RayfayClientDetails)clientDetails;
            //find client allow groups
            Set<String> clientAccessGroups = client.getAccessGroups();
            Set<String> userGroups =(Set<String>)user.getAttrs().get("rfMemberOf");
            if(userGroups == null)
                userGroups = new HashSet<>();
            if(clientAccessGroups == null)
                clientAccessGroups = new HashSet<>();
            return containsRedirectGrantType(userGroups, clientAccessGroups);
        }
        return false;
    }

    private boolean containsRedirectGrantType(Set<String> grantTypes, Set<String> clientTypes) {
        Iterator it = grantTypes.iterator();
        String type;
        do {
            if(!it.hasNext()) {
                return false;
            }
            type = (String)it.next();
        } while(!clientTypes.contains(type));

        return true;
    }

}
