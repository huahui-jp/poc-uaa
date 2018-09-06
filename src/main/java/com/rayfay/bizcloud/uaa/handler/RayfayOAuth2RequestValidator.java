//package com.rayfay.bizcloud.uaa.handler;
//
//import com.rayfay.bizcloud.uaa.entity.RayfayClientDetails;
//import com.rayfay.bizcloud.uaa.entity.RayfayUserDetails;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.common.exceptions.InvalidScopeException;
//import org.springframework.security.oauth2.provider.AuthorizationRequest;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.TokenRequest;
//import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestValidator;
//
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.Set;
//
///**
// * Created by ivor on 27/04/2017.
// */
//public class RayfayOAuth2RequestValidator extends DefaultOAuth2RequestValidator {
//
//    @Override
//    public void validateScope(AuthorizationRequest authorizationRequest, ClientDetails client) throws InvalidScopeException {
//        checkUserCanAccessClient(client);
//        super.validateScope(authorizationRequest, client);
//    }
//
//    @Override
//    public void validateScope(TokenRequest tokenRequest, ClientDetails client) throws InvalidScopeException {
//        checkUserCanAccessClient(client);
//        super.validateScope(tokenRequest, client);
//    }
//
//    private boolean checkUserCanAccessClient(ClientDetails clientDetails) throws InvalidScopeException{
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if(authentication != null && authentication.getPrincipal() instanceof RayfayUserDetails && clientDetails instanceof RayfayClientDetails){
//            RayfayUserDetails user = (RayfayUserDetails)authentication.getPrincipal();
//            RayfayClientDetails client = (RayfayClientDetails)clientDetails;
//            //find client allow groups
//            Set<String> clientAccessGroups = client.getAccessGroups();
//            Set<String> userGroups =(Set<String>)user.getAttrs().get("rfMemberOf");
//            if(userGroups == null)
//                userGroups = new HashSet<>();
//            if(clientAccessGroups == null)
//                clientAccessGroups = new HashSet<>();
//            return containsRedirectGrantType(userGroups, clientAccessGroups);
//        }
//        return false;
//    }
//
//    private boolean containsRedirectGrantType(Set<String> grantTypes, Set<String> clientTypes) {
//        Iterator it = grantTypes.iterator();
//        String type;
//        do {
//            if(!it.hasNext()) {
//                return false;
//            }
//            type = (String)it.next();
//        } while(!clientTypes.contains(type));
//
//        return true;
//    }
//}
