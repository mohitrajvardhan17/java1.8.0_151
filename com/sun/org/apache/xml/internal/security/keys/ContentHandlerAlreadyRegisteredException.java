package com.sun.org.apache.xml.internal.security.keys;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class ContentHandlerAlreadyRegisteredException
  extends XMLSecurityException
{
  private static final long serialVersionUID = 1L;
  
  public ContentHandlerAlreadyRegisteredException() {}
  
  public ContentHandlerAlreadyRegisteredException(String paramString)
  {
    super(paramString);
  }
  
  public ContentHandlerAlreadyRegisteredException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString, paramArrayOfObject);
  }
  
  public ContentHandlerAlreadyRegisteredException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public ContentHandlerAlreadyRegisteredException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(paramString, paramArrayOfObject, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\keys\ContentHandlerAlreadyRegisteredException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */