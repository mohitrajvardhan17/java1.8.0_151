package com.sun.org.apache.xerces.internal.jaxp.validation;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

final class DraconianErrorHandler
  implements ErrorHandler
{
  private static final DraconianErrorHandler ERROR_HANDLER_INSTANCE = new DraconianErrorHandler();
  
  private DraconianErrorHandler() {}
  
  public static DraconianErrorHandler getInstance()
  {
    return ERROR_HANDLER_INSTANCE;
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {}
  
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\DraconianErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */