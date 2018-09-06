package com.rayfay.bizcloud.uaa.filter;

import com.rayfay.bizcloud.uaa.controller.LoginAttemptCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

/**
 * Created by ivor on 25/04/2017.
 */
@Component
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    @Autowired
    private LoginAttemptCacheService loginAttemptCacheService;
    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        loginAttemptCacheService.loginSuccess(event.getAuthentication().getName());
    }
}
