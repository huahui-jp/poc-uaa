package com.rayfay.bizcloud.uaa.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.directory.Attribute;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.query.SearchScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Configuration
@Controller
public class GroupController
{
  @Autowired
  private LdapTemplate ldapTemplate;
  

  @RequestMapping(value = "/groups")
  @ResponseBody
  public Map<String,Object> groups(HttpServletRequest request, HttpServletResponse response) throws Exception
  {
    Map<String,Object> map = new HashMap<>();
    try
    {
      map.put("groups", listGroups());
      map.put("success", true);  
    }
    catch (Exception e)
    {
      map.put("success", false);  
    }
    return map;
  }
  
  private List<String> listGroups()
  {
    List<String> ret = new ArrayList<>();
    OrFilter filter = new OrFilter();
    filter.or(new EqualsFilter("objectclass", "groupOfNames"));
    ldapTemplate.search("ou=groups", filter.encode(), SearchScope.SUBTREE.getId(), new String[] { "cn" },
        (AttributesMapper<Object>) attributes -> {
          {
            Attribute attr1 = attributes.get("cn");
            if (attr1 != null && attr1.size() > 0)
            {
              Object o1 = attr1.get(0);
              if (o1 != null)
                ret.add(String.valueOf(o1));
            }
          }
          return null;
        });
    
    return ret;
  }
}
