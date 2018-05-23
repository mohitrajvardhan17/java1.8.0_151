package com.sun.java.browser.dom;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

public abstract interface DOMAccessor
{
  public abstract Document getDocument(Object paramObject)
    throws DOMException;
  
  public abstract DOMImplementation getDOMImplementation();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\java\browser\dom\DOMAccessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */