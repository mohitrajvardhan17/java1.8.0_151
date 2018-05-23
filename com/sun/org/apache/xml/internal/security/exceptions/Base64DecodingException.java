package com.sun.org.apache.xml.internal.security.exceptions;

public class Base64DecodingException
  extends XMLSecurityException
{
  private static final long serialVersionUID = 1L;
  
  public Base64DecodingException() {}
  
  public Base64DecodingException(String paramString)
  {
    super(paramString);
  }
  
  public Base64DecodingException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString, paramArrayOfObject);
  }
  
  public Base64DecodingException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public Base64DecodingException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(paramString, paramArrayOfObject, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\exceptions\Base64DecodingException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */