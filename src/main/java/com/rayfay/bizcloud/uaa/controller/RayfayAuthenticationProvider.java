package com.rayfay.bizcloud.uaa.controller;

import java.lang.reflect.Field;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.ldap.authentication.LdapAuthenticationProvider;
import org.springframework.security.ldap.authentication.LdapAuthenticator;
import org.springframework.util.StringUtils;

import com.rayfay.bizcloud.uaa.entity.RayfayUserDetails;;
/**
 * Created by ivor on 20/04/2017.
 */
public class RayfayAuthenticationProvider extends LdapAuthenticationProvider {
  
    private static final Logger logger = LoggerFactory.getLogger(RayfayAuthenticationProvider.class);
      
    @Autowired
    AliasChecker aliasChecker;
  
    public RayfayAuthenticationProvider(LdapAuthenticator authenticator) {
        super(authenticator);
    }

    @Override
    protected DirContextOperations doAuthentication(UsernamePasswordAuthenticationToken authentication) {

        DirContextOperations user = null;
        try{
            //try use alias to login
            String uid = aliasChecker.tryGetUidByAlias(String.valueOf(authentication.getPrincipal()));
            if (uid!=null)
            {
                unsafeSet(authentication,"principal",uid);
            }
            user = super.doAuthentication(authentication);
        }catch(BadCredentialsException bce){
          logger.error("AUTH FAILED user="+authentication.getPrincipal() + " error="+bce.getMessage());
          throw new BadCredentialsException("用户名或密码不正确");
        }catch(AuthenticationException ae){
          logger.error("AUTH FAILED user="+authentication.getPrincipal() + " error="+ae.getMessage());
            throw ae;
        }

        String status = user.getStringAttribute("rfIdentityStatus");
        String due = user.getStringAttribute("rfDueTime");

        Date dueTime = null;
        try {
            if (StringUtils.hasText(due))
                dueTime = DateUtils.parseDate(due, new String[] { "yyyy/MM/dd HH:mm:ss" });
        } catch (Exception ex) {
          logger.error("BAD rfDueTime="+due+" user="+authentication.getPrincipal());
        }

        if (!RayfayUserDetails.ACTIVE.equals(status)) {
            logger.error("AUTH FAILED user="+authentication.getPrincipal() + " error=account inactive");
            throw new DisabledException("身份状态不活动");
        }

        if (dueTime != null && (dueTime.before(new Date()))) {
            logger.error("AUTH FAILED user="+authentication.getPrincipal() + " error=account expired");
            throw new AccountExpiredException("身份已过期");
        }

        return user;
    }
    
    public final static void unsafeSet(Object inst, String name, Object value)
    {
      try
      {
        Field m = inst.getClass().getDeclaredField(name);
        if (m.isAccessible())
          throw new RuntimeException("not unsafe setting Object={"+inst+"},name={"+name+"},value={"+value+"}");
        m.setAccessible(true);
        m.set(inst, value);
        m.setAccessible(false);
      }
      catch (Exception e)
      {
        e.printStackTrace();
        throw new RuntimeException("Unsafe set failure.Object={"+inst+"},name={"+name+"},value={"+value+"}");
      }
    }

}
