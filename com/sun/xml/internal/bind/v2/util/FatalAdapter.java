package com.sun.xml.internal.bind.v2.util;

import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class FatalAdapter
  implements ErrorHandler
{
  private final ErrorHandler core;
  
  public FatalAdapter(ErrorHandler paramErrorHandler)
  {
    core = paramErrorHandler;
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    core.warning(paramSAXParseException);
  }
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    core.fatalError(paramSAXParseException);
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    core.fatalError(paramSAXParseException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\util\FatalAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */