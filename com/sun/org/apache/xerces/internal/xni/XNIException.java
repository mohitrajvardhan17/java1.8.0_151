package com.sun.org.apache.xerces.internal.xni;

public class XNIException
  extends RuntimeException
{
  static final long serialVersionUID = 9019819772686063775L;
  private Exception fException;
  
  public XNIException(String paramString)
  {
    super(paramString);
  }
  
  public XNIException(Exception paramException)
  {
    super(paramException.getMessage());
    fException = paramException;
  }
  
  public XNIException(String paramString, Exception paramException)
  {
    super(paramString);
    fException = paramException;
  }
  
  public Exception getException()
  {
    return fException;
  }
  
  public Throwable getCause()
  {
    return fException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\xni\XNIException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */