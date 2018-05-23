package com.sun.org.apache.xerces.internal.jaxp.validation;

import org.xml.sax.SAXException;

public class WrappedSAXException
  extends RuntimeException
{
  public final SAXException exception;
  
  WrappedSAXException(SAXException paramSAXException)
  {
    exception = paramSAXException;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\WrappedSAXException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */