package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public abstract class ErrorHandlerProxy
  implements ErrorHandler
{
  public ErrorHandlerProxy() {}
  
  public void error(SAXParseException paramSAXParseException)
    throws SAXException
  {
    XMLErrorHandler localXMLErrorHandler = getErrorHandler();
    if ((localXMLErrorHandler instanceof ErrorHandlerWrapper)) {
      fErrorHandler.error(paramSAXParseException);
    } else {
      localXMLErrorHandler.error("", "", ErrorHandlerWrapper.createXMLParseException(paramSAXParseException));
    }
  }
  
  public void fatalError(SAXParseException paramSAXParseException)
    throws SAXException
  {
    XMLErrorHandler localXMLErrorHandler = getErrorHandler();
    if ((localXMLErrorHandler instanceof ErrorHandlerWrapper)) {
      fErrorHandler.fatalError(paramSAXParseException);
    } else {
      localXMLErrorHandler.fatalError("", "", ErrorHandlerWrapper.createXMLParseException(paramSAXParseException));
    }
  }
  
  public void warning(SAXParseException paramSAXParseException)
    throws SAXException
  {
    XMLErrorHandler localXMLErrorHandler = getErrorHandler();
    if ((localXMLErrorHandler instanceof ErrorHandlerWrapper)) {
      fErrorHandler.warning(paramSAXParseException);
    } else {
      localXMLErrorHandler.warning("", "", ErrorHandlerWrapper.createXMLParseException(paramSAXParseException));
    }
  }
  
  protected abstract XMLErrorHandler getErrorHandler();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\ErrorHandlerProxy.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */