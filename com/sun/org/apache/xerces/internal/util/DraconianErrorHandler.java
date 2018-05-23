package com.sun.org.apache.xerces.internal.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DraconianErrorHandler
  implements ErrorHandler
{
  public static final ErrorHandler theInstance = new DraconianErrorHandler();
  
  private DraconianErrorHandler() {}
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    throw paramSAXParseException;
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    throw paramSAXParseException;
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\DraconianErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */