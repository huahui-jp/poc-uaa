package com.rayfay.bizcloud.uaa.controller;

import org.springframework.cache.Cache;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Created by ivor on 25/04/2017.
 */
public class LoginAttemptCacheService {
    private Integer allowedNumberOfAttempts = 3;
    private Cache attempts;

    public LoginAttemptCacheService(int allowedNumberOfAttempts, Cache cache){
        this.allowedNumberOfAttempts = allowedNumberOfAttempts;
        this.attempts = cache;
    }


    /**
     * Triggers on each unsuccessful login attempt and increases number of attempts
     */
    public void loginFail(String login) {
        login = RequestContextHolder.currentRequestAttributes().getSessionId();
        Integer numberOfAttempts = attempts.get(login,Integer.class);
        if(numberOfAttempts == null)
            numberOfAttempts = 0;
        numberOfAttempts++;

        if (numberOfAttempts >= allowedNumberOfAttempts) {
            activateRecaptcha();
        } else {
            attempts.put(login, numberOfAttempts);
        }
    }

    /**
     * Triggers on each successful login attempt and resets number of attempts
     */
    public void loginSuccess(String login) {
        login = RequestContextHolder.currentRequestAttributes().getSessionId();
        attempts.evict(RequestContextHolder.currentRequestAttributes().getSessionId());
        deactivateRecaptcha();
    }

    private void activateRecaptcha() {
        RequestContextHolder.currentRequestAttributes().setAttribute("needCaptcha",true, RequestAttributes.SCOPE_SESSION);
    }

    private void deactivateRecaptcha() {
        RequestContextHolder.currentRequestAttributes().setAttribute("needCaptcha",false, RequestAttributes.SCOPE_SESSION);
    }
}
