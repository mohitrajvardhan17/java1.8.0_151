package com.sun.org.apache.xerces.internal.impl.xs.traversers;

abstract class Container
{
  static final int THRESHOLD = 5;
  OneAttr[] values;
  int pos = 0;
  
  Container() {}
  
  static Container getContainer(int paramInt)
  {
    if (paramInt > 5) {
      return new LargeContainer(paramInt);
    }
    return new SmallContainer(paramInt);
  }
  
  abstract void put(String paramString, OneAttr paramOneAttr);
  
  abstract OneAttr get(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\traversers\Container.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */