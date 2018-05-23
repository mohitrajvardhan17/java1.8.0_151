package com.sun.org.apache.xml.internal.security.transforms;

import com.sun.org.apache.xml.internal.security.exceptions.XMLSecurityException;

public class TransformationException
  extends XMLSecurityException
{
  private static final long serialVersionUID = 1L;
  
  public TransformationException() {}
  
  public TransformationException(String paramString)
  {
    super(paramString);
  }
  
  public TransformationException(String paramString, Object[] paramArrayOfObject)
  {
    super(paramString, paramArrayOfObject);
  }
  
  public TransformationException(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public TransformationException(String paramString, Object[] paramArrayOfObject, Exception paramException)
  {
    super(paramString, paramArrayOfObject, paramException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\transforms\TransformationException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */