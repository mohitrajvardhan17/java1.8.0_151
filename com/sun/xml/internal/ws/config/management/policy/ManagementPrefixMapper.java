package com.sun.xml.internal.ws.config.management.policy;

import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import java.util.HashMap;
import java.util.Map;

public class ManagementPrefixMapper
  implements PrefixMapper
{
  private static final Map<String, String> prefixMap = new HashMap();
  
  public ManagementPrefixMapper() {}
  
  public Map<String, String> getPrefixMap()
  {
    return prefixMap;
  }
  
  static
  {
    prefixMap.put("http://java.sun.com/xml/ns/metro/management", "sunman");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\config\management\policy\ManagementPrefixMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */