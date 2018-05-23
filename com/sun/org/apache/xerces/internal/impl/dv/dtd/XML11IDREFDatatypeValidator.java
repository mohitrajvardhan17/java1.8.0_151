package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.XML11Char;

public class XML11IDREFDatatypeValidator
  extends IDREFDatatypeValidator
{
  public XML11IDREFDatatypeValidator() {}
  
  public void validate(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    if (paramValidationContext.useNamespaces())
    {
      if (!XML11Char.isXML11ValidNCName(paramString)) {
        throw new InvalidDatatypeValueException("IDREFInvalidWithNamespaces", new Object[] { paramString });
      }
    }
    else if (!XML11Char.isXML11ValidName(paramString)) {
      throw new InvalidDatatypeValueException("IDREFInvalid", new Object[] { paramString });
    }
    paramValidationContext.addIdRef(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\dtd\XML11IDREFDatatypeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */