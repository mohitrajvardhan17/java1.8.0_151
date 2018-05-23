package com.sun.xml.internal.ws.server;

import com.sun.xml.internal.ws.developer.ValidationErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DraconianValidationErrorHandler
  extends ValidationErrorHandler
{
  public DraconianValidationErrorHandler() {}
  
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\server\DraconianValidationErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */