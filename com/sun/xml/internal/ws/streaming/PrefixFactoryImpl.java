package com.sun.xml.internal.ws.streaming;

import java.util.HashMap;
import java.util.Map;

public class PrefixFactoryImpl
  implements PrefixFactory
{
  private String _base;
  private int _next;
  private Map _cachedUriToPrefixMap;
  
  public PrefixFactoryImpl(String paramString)
  {
    _base = paramString;
    _next = 1;
  }
  
  public String getPrefix(String paramString)
  {
    String str = null;
    if (_cachedUriToPrefixMap == null) {
      _cachedUriToPrefixMap = new HashMap();
    } else {
      str = (String)_cachedUriToPrefixMap.get(paramString);
    }
    if (str == null)
    {
      str = _base + Integer.toString(_next++);
      _cachedUriToPrefixMap.put(paramString, str);
    }
    return str;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\streaming\PrefixFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */