package com.sun.java.browser.dom;

public class DOMUnsupportedException
  extends Exception
{
  private Throwable ex;
  private String msg;
  
  public DOMUnsupportedException()
  {
    this(null, null);
  }
  
  public DOMUnsupportedException(String paramString)
  {
    this(null, paramString);
  }
  
  public DOMUnsupportedException(Exception paramException)
  {
    this(paramException, null);
  }
  
  public DOMUnsupportedException(Exception paramException, String paramString)
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\browser\dom\DOMUnsupportedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */