package com.sun.org.apache.xml.internal.utils;

public class WrappedRuntimeException
  extends RuntimeException
{
  static final long serialVersionUID = 7140414456714658073L;
  private Exception m_exception;
  
  public WrappedRuntimeException(Exception paramException)
  {
    super(paramException.getMessage());
    m_exception = paramException;
  }
  
  public WrappedRuntimeException(String paramString, Exception paramException)
  {
    super(paramString);
    m_exception = paramException;
  }
  
  public Exception getException()
  {
    return m_exception;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\utils\WrappedRuntimeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */