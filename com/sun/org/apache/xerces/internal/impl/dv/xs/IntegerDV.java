package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

public class IntegerDV
  extends DecimalDV
{
  public IntegerDV() {}
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    try
    {
      return new DecimalDV.XDecimal(paramString, true);
    }
    catch (NumberFormatException localNumberFormatException)
    {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "integer" });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\IntegerDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */