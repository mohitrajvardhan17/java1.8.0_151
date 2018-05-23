package com.sun.org.apache.xerces.internal.impl.xs.traversers;

class SmallContainer
  extends Container
{
  String[] keys;
  
  SmallContainer(int paramInt)
  {
    keys = new String[paramInt];
    values = new OneAttr[paramInt];
  }
  
  void put(String paramString, OneAttr paramOneAttr)
  {
    keys[pos] = paramString;
    values[(pos++)] = paramOneAttr;
  }
  
  OneAttr get(String paramString)
  {
    for (int i = 0; i < pos; i++) {
      if (keys[i].equals(paramString)) {
        return values[i];
      }
    }
    return null;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\SmallContainer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */