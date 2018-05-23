package com.sun.xml.internal.bind.api;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public abstract interface ErrorListener
  extends ErrorHandler
{
  public abstract void error(SAXParseException paramSAXParseException);
  
  public abstract void fatalError(SAXParseException paramSAXParseException);
  
  public abstract void warning(SAXParseException paramSAXParseException);
  
  public abstract void info(SAXParseException paramSAXParseException);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\api\ErrorListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */