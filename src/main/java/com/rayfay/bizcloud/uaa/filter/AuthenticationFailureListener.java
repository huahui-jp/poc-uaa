package com.rayfay.bizcloud.uaa.filter;

import com.rayfay.bizcloud.uaa.controller.LoginAttemptCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.stereotype.Component;

/**
 * Created by ivor on 25/04/2017.
 */

@Component
public class AuthenticationFailureListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {
    @Autowired
    private LoginAttemptCacheService loginAttemptCacheService;

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        loginAttemptCacheService.loginFail(event.getAuthentication().getName());
    }
}
