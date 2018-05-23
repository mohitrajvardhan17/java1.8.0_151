package com.sun.org.apache.xml.internal.security.signature;

public class ReferenceNotInitializedException
  extends XMLSignatureException
{
  private static final long serialVersionUID = 1L;
  
  public ReferenceNotInitializedException() {}
  
  public ReferenceNotInitializedException(String paramString)
  {
    super(paramString);
  }
  
  public ReferenceNotInitializedException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString, paramArrayOfObject);
  }
  
  public ReferenceNotInitializedException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public ReferenceNotInitializedException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(paramString, paramArrayOfObject, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\ReferenceNotInitializedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */