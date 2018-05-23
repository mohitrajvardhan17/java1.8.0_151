package com.sun.org.apache.xerces.internal.dom;

import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.w3c.dom.DOMError;
import org.w3c.dom.DOMLocator;

public class DOMErrorImpl
  implements DOMError
{
  public short fSeverity = 1;
  public String fMessage = null;
  public DOMLocatorImpl fLocator = new DOMLocatorImpl();
  public Exception fException = null;
  public String fType;
  public Object fRelatedData;
  
  public DOMErrorImpl() {}
  
  public DOMErrorImpl(short paramShort, XMLParseException paramXMLParseException)
  {
    fSeverity = paramShort;
    fException = paramXMLParseException;
    fLocator = createDOMLocator(paramXMLParseException);
  }
  
  public short getSeverity()
  {
    return fSeverity;
  }
  
  public String getMessage()
  {
    return fMessage;
  }
  
  public DOMLocator getLocation()
  {
    return fLocator;
  }
  
  private DOMLocatorImpl createDOMLocator(XMLParseException paramXMLParseException)
  {
    return new DOMLocatorImpl(paramXMLParseException.getLineNumber(), paramXMLParseException.getColumnNumber(), paramXMLParseException.getCharacterOffset(), paramXMLParseException.getExpandedSystemId());
  }
  
  public Object getRelatedException()
  {
    return fException;
  }
  
  public void reset()
  {
    fSeverity = 1;
    fException = null;
  }
  
  public String getType()
  {
    return fType;
  }
  
  public Object getRelatedData()
  {
    return fRelatedData;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMErrorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */