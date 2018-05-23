package com.sun.java.browser.dom;

public class DOMAccessException
  extends Exception
{
  private Throwable ex;
  private String msg;
  
  public DOMAccessException()
  {
    this(null, null);
  }
  
  public DOMAccessException(String paramString)
  {
    this(null, paramString);
  }
  
  public DOMAccessException(Exception paramException)
  {
    this(paramException, null);
  }
  
  public DOMAccessException(Exception paramException, String paramString)
  {
    ex = paramException;
    msg = paramString;
  }
  
  public String getMessage()
  {
    return msg;
  }
  
  public Throwable getCause()
  {
    return ex;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\browser\dom\DOMAccessException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */