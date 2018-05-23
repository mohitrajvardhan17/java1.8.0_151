package org.xml.sax;

public abstract interface ErrorHandler
{
  public abstract void warning(SAXParseException paramSAXParseException)
    throws SAXException;
  
  public abstract void error(SAXParseException paramSAXParseException)
    throws SAXException;
  
  public abstract void fatalError(SAXParseException paramSAXParseException)
    throws SAXException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\org\xml\sax\ErrorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */