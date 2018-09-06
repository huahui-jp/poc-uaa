package com.rayfay.bizcloud.uaa.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.naming.directory.Attribute;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.SizeLimitExceededException;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Service;

/**
 * @author maxiang
 *
 */
@Service
public class AliasChecker
{
  private static final Logger logger = LoggerFactory.getLogger(AliasChecker.class);
  
  @Autowired
  private LdapTemplate ldapTemplate;

  public String tryGetUidByAlias(String aliasName)
  {
    return tryGetUidByAlias(aliasName, false);
  }

  private String tryGetUidByAlias(String username, boolean uidOrAlias)
  {
    if (StringUtils.isEmpty(username)) return null;
    List<String> uids = new ArrayList<>();
    
    OrFilter filter = new OrFilter();
    filter.or(new EqualsFilter("objectclass", "person"));
    filter.or(new EqualsFilter("objectclass", "account"));
    ldapTemplate.search("", filter.encode(), SearchScope.SUBTREE.getId(), new String[] { "uid", "rfAlias" },
        (AttributesMapper<Object>) attributes -> {
          String uid = null;
          {
            Attribute attr1 = attributes.get("uid");
            if (attr1 != null && attr1.size()>0)
            {
              Object o1 = attr1.get(0);
              if (o1!=null) uid = String.valueOf(o1).toLowerCase();
              if (uidOrAlias && uid.equalsIgnoreCase(username))
              {
                uids.add(uid);
              }
            }
          }
          {
            Attribute attr1 = attributes.get("rfAlias");
            if (attr1 != null && attr1.size()>0)
            {
              Object o1 = attr1.get(0);
              if (o1!=null)
              {
                String aliases = String.valueOf(o1);
                List<String> arr = splitAliases(aliases);
                if (arr.contains(username.toLowerCase()))
                {
                  if (uid!=null)
                    uids.add(uid);
                }
              }
            }
          }
          
          return null;
        });
    
    int cnt = uids.size();
    
    if (cnt==0)
      return null;
    if (cnt>1)
      throw new SizeLimitExceededException(new javax.naming.SizeLimitExceededException(
          "Found multiple UIDs for uid or alias " + username + ": " + uids));
    return uids.get(0);
  }
  
  private List<String> splitAliases(String aliases)
  {
    List<String> ret = new ArrayList<>();
    String arr[] = StringUtils.split(aliases, ",; ");
    for (String a : arr)
    {
      String alias = a.trim();
      if (StringUtils.isEmpty(alias))
        continue;
      ret.add(alias.toLowerCase());
    }
    return ret;
  }
  
}
