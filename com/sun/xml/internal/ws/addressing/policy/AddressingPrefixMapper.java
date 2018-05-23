package com.sun.xml.internal.ws.addressing.policy;

import com.sun.xml.internal.ws.api.addressing.AddressingVersion;
import com.sun.xml.internal.ws.policy.spi.PrefixMapper;
import java.util.HashMap;
import java.util.Map;

public class AddressingPrefixMapper
  implements PrefixMapper
{
  private static final Map<String, String> prefixMap = new HashMap();
  
  public AddressingPrefixMapper() {}
  
  public Map<String, String> getPrefixMap()
  {
    return prefixMap;
  }
  
  static
  {
    prefixMap.put(MEMBERpolicyNsUri, "wsap");
    prefixMap.put(MEMBERnsUri, "wsa");
    prefixMap.put("http://www.w3.org/2007/05/addressing/metadata", "wsam");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\policy\AddressingPrefixMapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */