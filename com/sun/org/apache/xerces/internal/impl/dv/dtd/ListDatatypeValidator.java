package com.sun.org.apache.xerces.internal.impl.dv.dtd;

import com.sun.org.apache.xerces.internal.impl.dv.DatatypeValidator;
import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import java.util.StringTokenizer;

public class ListDatatypeValidator
  implements DatatypeValidator
{
  DatatypeValidator fItemValidator;
  
  public ListDatatypeValidator(DatatypeValidator paramDatatypeValidator)
  {
    fItemValidator = paramDatatypeValidator;
  }
  
  public void validate(String paramString, ValidationContext paramValidationContext)
    throws InvalidDatatypeValueException
  {
    StringTokenizer localStringTokenizer = new StringTokenizer(paramString, " ");
    int i = localStringTokenizer.countTokens();
    if (i == 0) {
      throw new InvalidDatatypeValueException("EmptyList", null);
    }
    while (localStringTokenizer.hasMoreTokens()) {
      fItemValidator.validate(localStringTokenizer.nextToken(), paramValidationContext);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\dtd\ListDatatypeValidator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */