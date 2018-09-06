package com.rayfay.bizcloud.uaa.controller;

import java.util.HashMap;
import java.util.Map;

import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

/**
 * @author maxiang
 *
 */
public class LDAPUtils
{

  public static Object getSingleAttr(Attributes a, String attrId) throws Exception
  {
    Attribute attr = a.get(attrId);
    if (attr==null ||attr.size()==0)
      return null;
    return attr.getAll().next();
  }

  public static Map<String, Object> getSingleAttrsAsMap(Attributes a, String attrIds[]) throws Exception
  {
    Map<String, Object> map = new HashMap<>();
    for (String attrId: attrIds)
    {
      Object o = getSingleAttr(a, attrId);
      if (o!=null)
        map.put(attrId, o);
    }
    return map;
  }
  
}
