package com.sun.org.apache.xerces.internal.impl.xs.traversers;

import java.util.HashMap;
import java.util.Map;

class LargeContainer
  extends Container
{
  Map items;
  
  LargeContainer(int paramInt)
  {
    items = new HashMap(paramInt * 2 + 1);
    values = new OneAttr[paramInt];
  }
  
  void put(String paramString, OneAttr paramOneAttr)
  {
    items.put(paramString, paramOneAttr);
    values[(pos++)] = paramOneAttr;
  }
  
  OneAttr get(String paramString)
  {
    OneAttr localOneAttr = (OneAttr)items.get(paramString);
    return localOneAttr;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\LargeContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */