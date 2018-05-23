package com.sun.rowset.internal;

import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlErrorHandler
  extends DefaultHandler
{
  public int errorCounter = 0;
  
  public XmlErrorHandler() {}
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    errorCounter += 1;
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    errorCounter += 1;
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\rowset\internal\XmlErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */