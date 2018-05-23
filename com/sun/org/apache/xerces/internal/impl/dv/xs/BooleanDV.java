package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class BooleanDV
  extends TypeValidator
{
  private static final String[] fValueSpace = { "false", "true", "0", "1" };
  
  public BooleanDV() {}
  
  public short getAllowedFacets()
  {
    return 24;
  }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    Boolean localBoolean = null;
    if ((paramString.equals(fValueSpace[0])) || (paramString.equals(fValueSpace[2]))) {
      localBoolean = Boolean.FALSE;
    } else if ((paramString.equals(fValueSpace[1])) || (paramString.equals(fValueSpace[3]))) {
      localBoolean = Boolean.TRUE;
    } else {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "boolean" });
    }
    return localBoolean;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\BooleanDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */