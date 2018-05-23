package com.sun.org.apache.xml.internal.dtm;

import org.w3c.dom.DOMException;

public class DTMDOMException
  extends DOMException
{
  static final long serialVersionUID = 1895654266613192414L;
  
  public DTMDOMException(short paramShort, String paramString)
  {
    super(paramShort, paramString);
  }
  
  public DTMDOMException(short paramShort)
  {
    super(paramShort, "");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xml\internal\dtm\DTMDOMException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */