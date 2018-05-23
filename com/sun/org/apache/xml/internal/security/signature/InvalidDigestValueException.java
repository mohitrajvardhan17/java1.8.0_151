package com.sun.org.apache.xml.internal.security.signature;

public class InvalidDigestValueException
  extends XMLSignatureException
{
  private static final long serialVersionUID = 1L;
  
  public InvalidDigestValueException() {}
  
  public InvalidDigestValueException(String paramString)
  {
    super(paramString);
  }
  
  public InvalidDigestValueException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString, paramArrayOfObject);
  }
  
  public InvalidDigestValueException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public InvalidDigestValueException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(paramString, paramArrayOfObject, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\InvalidDigestValueException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */