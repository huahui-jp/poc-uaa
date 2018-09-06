package com.rayfay.bizcloud.uaa.entity;
import java.util.Collections;
import java.util.Set;

import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * Created by ivor on 20/04/2017.
 */
public class RayfayClientDetails extends BaseClientDetails {

    public static String CLIENT_ENABLE = "1";

    private String appName;
    private Set<String> allowAttributes = Collections.emptySet();
    private Set<String> accessGroups = Collections.emptySet();
    public Set<String> getAccessGroups() {
        return accessGroups;
    }

    public void setAccessGroups(Set<String> accessGroups) {
        this.accessGroups = accessGroups;
    }

    private String appDesc;
    private String enable;
    private String enableSLO;

    public RayfayClientDetails(String clientId, String resourceIds, String scopes, String grantTypes,
                               String authorities, String redirectUris) {
        super(clientId, resourceIds, scopes, grantTypes, authorities, redirectUris);
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public Set<String> getAllowAttributes() {
        return allowAttributes;
    }

    public void setAllowAttributes(Set<String> allowAttributes) {
        this.allowAttributes = allowAttributes;
    }

    public String getAppDesc() {
        return appDesc;
    }

    public void setAppDesc(String appDesc) {
        this.appDesc = appDesc;
    }

    public Boolean isEnable() {
        if(CLIENT_ENABLE.equals(enable))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    public void setEnable(String enable) {
        this.enable = enable;
    }

    public Boolean isEnableSLO() {
        if(CLIENT_ENABLE.equals(enableSLO))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    public void setEnableSLO(String enableSLO) {
        this.enableSLO = enableSLO;
    }
}
