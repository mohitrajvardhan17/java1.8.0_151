package com.sun.org.apache.xerces.internal.impl.xs.util;

public final class XIntPool
{
  private static final short POOL_SIZE = 10;
  private static final XInt[] fXIntPool = new XInt[10];
  
  public XIntPool() {}
  
  public final XInt getXInt(int paramInt)
  {
    if ((paramInt >= 0) && (paramInt < fXIntPool.length)) {
      return fXIntPool[paramInt];
    }
    return new XInt(paramInt);
  }
  
  static
  {
    for (int i = 0; i < 10; i++) {
      fXIntPool[i] = new XInt(i);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\util\XIntPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */