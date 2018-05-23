package com.sun.org.apache.xerces.internal.jaxp.validation;

import com.sun.org.apache.xerces.internal.xni.XNIException;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import com.sun.org.apache.xerces.internal.xni.parser.XMLParseException;
import javax.xml.transform.stream.StreamSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

final class Util
{
  Util() {}
  
  public static final XMLInputSource toXMLInputSource(StreamSource paramStreamSource)
  {
    if (paramStreamSource.getReader() != null) {
      return new XMLInputSource(paramStreamSource.getPublicId(), paramStreamSource.getSystemId(), paramStreamSource.getSystemId(), paramStreamSource.getReader(), null);
    }
    if (paramStreamSource.getInputStream() != null) {
      return new XMLInputSource(paramStreamSource.getPublicId(), paramStreamSource.getSystemId(), paramStreamSource.getSystemId(), paramStreamSource.getInputStream(), null);
    }
    return new XMLInputSource(paramStreamSource.getPublicId(), paramStreamSource.getSystemId(), paramStreamSource.getSystemId());
  }
  
  public static SAXException toSAXException(XNIException paramXNIException)
  {
    if ((paramXNIException instanceof XMLParseException)) {
      return toSAXParseException((XMLParseException)paramXNIException);
    }
    if ((paramXNIException.getException() instanceof SAXException)) {
      return (SAXException)paramXNIException.getException();
    }
    return new SAXException(paramXNIException.getMessage(), paramXNIException.getException());
  }
  
  public static SAXParseException toSAXParseException(XMLParseException paramXMLParseException)
  {
    if ((paramXMLParseException.getException() instanceof SAXParseException)) {
      return (SAXParseException)paramXMLParseException.getException();
    }
    return new SAXParseException(paramXMLParseException.getMessage(), paramXMLParseException.getPublicId(), paramXMLParseException.getExpandedSystemId(), paramXMLParseException.getLineNumber(), paramXMLParseException.getColumnNumber(), paramXMLParseException.getException());
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\validation\Util.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */