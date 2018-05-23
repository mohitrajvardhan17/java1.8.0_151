package com.sun.org.apache.xerces.internal.dom;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class LCount
{
  static final Map<String, LCount> lCounts = new ConcurrentHashMap();
  public int captures = 0;
  public int bubbles = 0;
  public int defaults;
  public int total = 0;
  
  LCount() {}
  
  static LCount lookup(String paramString)
  {
    LCount localLCount = (LCount)lCounts.get(paramString);
    if (localLCount == null) {
      lCounts.put(paramString, localLCount = new LCount());
    }
    return localLCount;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\LCount.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */