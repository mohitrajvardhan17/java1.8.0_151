package com.sun.org.apache.xml.internal.security.signature;

public class InvalidSignatureValueException
  extends XMLSignatureException
{
  private static final long serialVersionUID = 1L;
  
  public InvalidSignatureValueException() {}
  
  public InvalidSignatureValueException(String paramString)
  {
    super(paramString);
  }
  
  public InvalidSignatureValueException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString, paramArrayOfObject);
  }
  
  public InvalidSignatureValueException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public InvalidSignatureValueException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(paramString, paramArrayOfObject, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\InvalidSignatureValueException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */