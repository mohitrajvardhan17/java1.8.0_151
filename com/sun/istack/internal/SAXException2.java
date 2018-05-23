package com.sun.istack.internal;

import org.xml.sax.SAXException;

public class SAXException2
  extends SAXException
{
  public SAXException2(String paramString)
  {
    super(paramString);
  }
  
  public SAXException2(Exception paramException)
  {
    super(paramException);
  }
  
  public SAXException2(String paramString, Exception paramException)
  {
    super(paramString, paramException);
  }
  
  public Throwable getCause()
  {
    return getException();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\istack\internal\SAXException2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */