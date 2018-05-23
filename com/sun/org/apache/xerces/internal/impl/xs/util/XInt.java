package com.sun.org.apache.xerces.internal.impl.xs.util;

public final class XInt
{
  private int fValue;
  
  XInt(int paramInt)
  {
    fValue = paramInt;
  }
  
  public final int intValue()
  {
    return fValue;
  }
  
  public final short shortValue()
  {
    return (short)fValue;
  }
  
  public final boolean equals(XInt paramXInt)
  {
    return fValue == fValue;
  }
  
  public String toString()
  {
    return Integer.toString(fValue);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\xs\util\XInt.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */