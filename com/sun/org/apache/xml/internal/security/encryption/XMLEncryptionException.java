package com.sun.org.apache.xml.internal.security.encryption;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class XMLEncryptionException
  extends XMLSecurityException
{
  private static final long serialVersionUID = 1L;
  
  public XMLEncryptionException() {}
  
  public XMLEncryptionException(String paramString)
  {
    super(paramString);
  }
  
  public XMLEncryptionException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString, paramArrayOfObject);
  }
  
  public XMLEncryptionException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public XMLEncryptionException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(paramString, paramArrayOfObject, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\encryption\XMLEncryptionException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */