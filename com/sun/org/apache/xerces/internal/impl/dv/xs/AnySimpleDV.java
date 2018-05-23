package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class AnySimpleDV
  extends TypeValidator
{
  public AnySimpleDV() {}
  
  public short getAllowedFacets()
  {
    return 0;
  }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    return paramString;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\AnySimpleDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */