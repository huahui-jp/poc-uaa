package com.rayfay.bizcloud.uaa.entity;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.xml.soap.Name;

import org.springframework.ldap.core.DirContextAdapter;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.support.LdapUtils;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.ldap.userdetails.LdapUserDetails;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;

/**
 * Created by ivor on 20/04/2017.
 */
public class RayfayUserDetails extends LdapUserDetailsImpl {

    private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

    public static final String ACTIVE = "1";
    public static final String INACTIVE = "2";

//    private String givenName;
//    private String sn;
//    private String cn;
//    private Set<String> memberOf;
//    private String gender;
//    private String pwdProtectQuestion;
//    private String pwdProtectAnswer;
//    private String findBackEmail;
//    private String dueTime;
//    private String identityStatus;
//    private String identityType;
//    private String identityStaffID;
//    private String identityCardID;
//    private String identityCardType;
//    private String identityOrg;
//    private String identityDep;
//    private String identityPosition;
//    private String phone;
//    private String mobile;
//    private Set<String> bindAccount;

    private Map<String, Object> attrs;

    protected RayfayUserDetails() {
    }

//    public String getGivenName() {
//        return givenName;
//    }
//
//    public String getSn() {
//        return sn;
//    }
//
//    public String getCn() {
//        return cn;
//    }
//
//    public Set<String> getMemberOf() {
//        return memberOf;
//    }
//
//    public String getGender() {
//        return gender;
//    }
//
//    public String getPwdProtectQuestion() {
//        return pwdProtectQuestion;
//    }
//
//    public String getPwdProtectAnswer() {
//        return pwdProtectAnswer;
//    }
//
//    public String getFindBackEmail() {
//        return findBackEmail;
//    }
//
//    public String getDueTime() {
//        return dueTime;
//    }
//
//    public String getIdentityStatus() {
//        return identityStatus;
//    }
//
//    public String getIdentityType() {
//        return identityType;
//    }
//
//    public String getIdentityStaffID() {
//        return identityStaffID;
//    }
//
//    public String getIdentityCardID() {
//        return identityCardID;
//    }
//
//    public String getIdentityCardType() {
//        return identityCardType;
//    }
//
//    public String getIdentityOrg() {
//        return identityOrg;
//    }
//
//    public String getIdentityDep() {
//        return identityDep;
//    }
//
//    public String getIdentityPosition() {
//        return identityPosition;
//    }
//
//    public String getPhone() {
//        return phone;
//    }
//
//    public String getMobile() {
//        return mobile;
//    }
//
//    public Set<String> getBindAccount() {
//        return bindAccount;
//    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public void populateContext(DirContextAdapter adapter) {

    }

    public static class Essence extends LdapUserDetailsImpl.Essence {

        public Essence() {
        }

        public Essence(DirContextOperations ctx) {
            super(ctx);

            Attributes attrs = ctx.getAttributes();

            Map<String, Object> attrsMap = Collections.list(attrs.getAll()).stream().collect(Collectors.toMap(
                    Attribute::getID,
                    attr-> {
                        try {
                            if("rfMemberOf".equalsIgnoreCase(attr.getID()))
                                return Collections.list(
                                        attr.getAll()).stream().map(
                                        s -> LdapUtils.getStringValue(LdapUtils.newLdapName(s.toString()), "cn")
                                ).collect(Collectors.toSet());
                            if("rfBindAccount".equalsIgnoreCase(attr.getID()))
                                return Collections.list(
                                        attr.getAll()).stream().map(
                                        s -> LdapUtils.getStringValue(LdapUtils.newLdapName(s.toString()), "uid")
                                ).collect(Collectors.toSet());

                            return attr.size() == 1 ? attr.get() :
                                Collections.list(
                                        attr.getAll()).stream().map(
                                            s -> s.toString()
                                        ).collect(Collectors.toSet());
                            }catch(NamingException ex){return "";}
                    }));

            setAttrs(attrsMap);
        }

        public Essence(RayfayUserDetails copyMe) {
            super(copyMe);
            ((RayfayUserDetails) instance).attrs = new HashMap<>(copyMe.attrs);
        }

        public void setAttrs(Map<String, Object> attrs) {
            ((RayfayUserDetails) instance).attrs = attrs;
        }

        protected LdapUserDetailsImpl createTarget() {
            return new RayfayUserDetails();
        }

        public LdapUserDetails createUserDetails() {
            RayfayUserDetails p = (RayfayUserDetails) super.createUserDetails();

            return p;
        }
    }
}
