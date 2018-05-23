package com.sun.org.apache.xml.internal.security.c14n;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class InvalidCanonicalizerException
  extends XMLSecurityException
{
  private static final long serialVersionUID = 1L;
  
  public InvalidCanonicalizerException() {}
  
  public InvalidCanonicalizerException(String paramString)
  {
    super(paramString);
  }
  
  public InvalidCanonicalizerException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString, paramArrayOfObject);
  }
  
  public InvalidCanonicalizerException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public InvalidCanonicalizerException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(paramString, paramArrayOfObject, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\InvalidCanonicalizerException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */