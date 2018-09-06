package com.rayfay.bizcloud.uaa.respository;

import java.util.Collection;

import com.rayfay.bizcloud.uaa.entity.RayfayUserDetails;
import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.ldap.userdetails.UserDetailsContextMapper;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
/**
 * Created by ivor on 20/04/2017.
 */
@Component
public class RayfayUserDetailsContextMapper implements UserDetailsContextMapper {

    public UserDetails mapUserFromContext(DirContextOperations ctx, String username,
                                          Collection<? extends GrantedAuthority> authorities) {
        RayfayUserDetails.Essence p = new RayfayUserDetails.Essence(ctx);

        p.setUsername(username);
        p.setAuthorities(authorities);

        return p.createUserDetails();

    }

    public void mapUserToContext(UserDetails user, DirContextAdapter ctx) {
        Assert.isInstanceOf(RayfayUserDetails.class, user, "UserDetails must be an RayfayUser instance");

        RayfayUserDetails p = (RayfayUserDetails) user;
        p.populateContext(ctx);
    }
}
