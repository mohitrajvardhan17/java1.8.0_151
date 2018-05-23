package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.XMLLocator;
import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLErrorHandler;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ErrorHandlerWrapper
  implements XMLErrorHandler
{
  protected ErrorHandler fErrorHandler;
  
  public ErrorHandlerWrapper() {}
  
  public ErrorHandlerWrapper(ErrorHandler paramErrorHandler)
  {
    setErrorHandler(paramErrorHandler);
  }
  
  public void setErrorHandler(ErrorHandler paramErrorHandler)
  {
    fErrorHandler = paramErrorHandler;
  }
  
  public ErrorHandler getErrorHandler()
  {
    return fErrorHandler;
  }
  
  public void warning(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException
  {
    if (fErrorHandler != null)
    {
      SAXParseException localSAXParseException1 = createSAXParseException(paramXMLParseException);
      try
      {
        fErrorHandler.warning(localSAXParseException1);
      }
      catch (SAXParseException localSAXParseException2)
      {
        throw createXMLParseException(localSAXParseException2);
      }
      catch (SAXException localSAXException)
      {
        throw createXNIException(localSAXException);
      }
    }
  }
  
  public void error(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException
  {
    if (fErrorHandler != null)
    {
      SAXParseException localSAXParseException1 = createSAXParseException(paramXMLParseException);
      try
      {
        fErrorHandler.error(localSAXParseException1);
      }
      catch (SAXParseException localSAXParseException2)
      {
        throw createXMLParseException(localSAXParseException2);
      }
      catch (SAXException localSAXException)
      {
        throw createXNIException(localSAXException);
      }
    }
  }
  
  public void fatalError(String paramString1, String paramString2, XMLParseException paramXMLParseException)
    throws XNIException
  {
    if (fErrorHandler != null)
    {
      SAXParseException localSAXParseException1 = createSAXParseException(paramXMLParseException);
      try
      {
        fErrorHandler.fatalError(localSAXParseException1);
      }
      catch (SAXParseException localSAXParseException2)
      {
        throw createXMLParseException(localSAXParseException2);
      }
      catch (SAXException localSAXException)
      {
        throw createXNIException(localSAXException);
      }
    }
  }
  
  protected static SAXParseException createSAXParseException(XMLParseException paramXMLParseException)
  {
    return new SAXParseException(paramXMLParseException.getMessage(), paramXMLParseException.getPublicId(), paramXMLParseException.getExpandedSystemId(), paramXMLParseException.getLineNumber(), paramXMLParseException.getColumnNumber(), paramXMLParseException.getException());
  }
  
  protected static XMLParseException createXMLParseException(SAXParseException paramSAXParseException)
  {
    String str1 = paramSAXParseException.getPublicId();
    final String str2 = paramSAXParseException.getSystemId();
    final int i = paramSAXParseException.getLineNumber();
    final int j = paramSAXParseException.getColumnNumber();
    XMLLocator local1 = new XMLLocator()
    {
      public String getPublicId()
      {
        return val$fPublicId;
      }
      
      public String getExpandedSystemId()
      {
        return str2;
      }
      
      public String getBaseSystemId()
      {
        return null;
      }
      
      public String getLiteralSystemId()
      {
        return null;
      }
      
      public int getColumnNumber()
      {
        return j;
      }
      
      public int getLineNumber()
      {
        return i;
      }
      
      public int getCharacterOffset()
      {
        return -1;
      }
      
      public String getEncoding()
      {
        return null;
      }
      
      public String getXMLVersion()
      {
        return null;
      }
    };
    return new XMLParseException(local1, paramSAXParseException.getMessage(), paramSAXParseException);
  }
  
  protected static XNIException createXNIException(SAXException paramSAXException)
  {
    return new XNIException(paramSAXException.getMessage(), paramSAXException);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\util\ErrorHandlerWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */