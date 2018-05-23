package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

public abstract class ErrorHandlerAdaptor
  implements XMLErrorHandler
{
  private boolean hadError = false;
  
  public ErrorHandlerAdaptor() {}
  
  public boolean hadError()
  {
    return hadError;
  }
  
  public void reset()
  {
    hadError = false;
  }
  
  protected abstract ErrorHandler getErrorHandler();
  
  public void fatalError(String paramString1, String paramString2, XMLParseException paramXMLParseException)
  {
    try
    {
      hadError = true;
      getErrorHandler().fatalError(Util.toSAXParseException(paramXMLParseException));
    }
    catch (SAXException localSAXException)
    {
      throw new WrappedSAXException(localSAXException);
    }
  }
  
  public void error(String paramString1, String paramString2, XMLParseException paramXMLParseException)
  {
    try
    {
      hadError = true;
      getErrorHandler().error(Util.toSAXParseException(paramXMLParseException));
    }
    catch (SAXException localSAXException)
    {
      throw new WrappedSAXException(localSAXException);
    }
  }
  
  public void warning(String paramString1, String paramString2, XMLParseException paramXMLParseException)
  {
    try
    {
      getErrorHandler().warning(Util.toSAXParseException(paramXMLParseException));
    }
    catch (SAXException localSAXException)
    {
      throw new WrappedSAXException(localSAXException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\ErrorHandlerAdaptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */