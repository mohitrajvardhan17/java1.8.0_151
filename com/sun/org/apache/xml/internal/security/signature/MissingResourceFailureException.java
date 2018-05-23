package com.sun.org.apache.xml.internal.security.signature;

public class MissingResourceFailureException
  extends XMLSignatureException
{
  private static final long serialVersionUID = 1L;
  private Reference uninitializedReference = null;
  
  public MissingResourceFailureException(String paramString, Reference paramReference)
  {
    super(paramString);
    uninitializedReference = paramReference;
  }
  
  public MissingResourceFailureException(String paramString, Object[] paramArrayOfObject, Reference paramReference)
  {
    super(paramString, paramArrayOfObject);
    uninitializedReference = paramReference;
  }
  
  public MissingResourceFailureException(String paramString, Exception paramException, Reference paramReference)
  {
    super(paramString, paramException);
    uninitializedReference = paramReference;
  }
  
  public MissingResourceFailureException(String paramString, Object[] paramArrayOfObject, Exception paramException, Reference paramReference)
  {
    super(paramString, paramArrayOfObject, paramException);
    uninitializedReference = paramReference;
  }
  
  public void setReference(Reference paramReference)
  {
    uninitializedReference = paramReference;
  }
  
  public Reference getReference()
  {
    return uninitializedReference;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\security\signature\MissingResourceFailureException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */