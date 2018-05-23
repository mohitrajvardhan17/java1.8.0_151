package com.sun.org.apache.xml.internal.security.c14n;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class CanonicalizationException
  extends XMLSecurityException
{
  private static final long serialVersionUID = 1L;
  
  public CanonicalizationException() {}
  
  public CanonicalizationException(String paramString)
  {
    super(paramString);
  }
  
  public CanonicalizationException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString, paramArrayOfObject);
  }
  
  public CanonicalizationException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public CanonicalizationException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(paramString, paramArrayOfObject, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\c14n\CanonicalizationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */