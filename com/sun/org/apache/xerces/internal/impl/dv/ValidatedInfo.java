package com.sun.org.apache.xerces.internal.impl.dv;

import com.sun.org.apache.xerces.internal.xs.ShortList;

public class ValidatedInfo
{
  public String normalizedValue;
  public Object actualValue;
  public short actualValueType;
  public XSSimpleType memberType;
  public XSSimpleType[] memberTypes;
  public ShortList itemValueTypes;
  
  public ValidatedInfo() {}
  
  public void reset()
  {
    normalizedValue = null;
    actualValue = null;
    memberType = null;
    memberTypes = null;
  }
  
  public String stringValue()
  {
    if (actualValue == null) {
      return normalizedValue;
    }
    return actualValue.toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\ValidatedInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */