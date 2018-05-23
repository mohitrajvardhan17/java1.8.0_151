package com.sun.xml.internal.messaging.saaj.soap.impl;

import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Body1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Detail1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Envelope1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Fault1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.FaultElement1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.Header1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_1.SOAPPart1_1Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Body1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Detail1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Envelope1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Fault1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.Header1_2Impl;
import com.sun.xml.internal.messaging.saaj.soap.ver1_2.SOAPPart1_2Impl;
import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPElement;

public class ElementFactory
{
  public ElementFactory() {}
  
  public static SOAPElement createElement(SOAPDocumentImpl paramSOAPDocumentImpl, Name paramName)
  {
    return createElement(paramSOAPDocumentImpl, paramName.getLocalName(), paramName.getPrefix(), paramName.getURI());
  }
  
  public static SOAPElement createElement(SOAPDocumentImpl paramSOAPDocumentImpl, QName paramQName)
  {
    return createElement(paramSOAPDocumentImpl, paramQName.getLocalPart(), paramQName.getPrefix(), paramQName.getNamespaceURI());
  }
  
  public static SOAPElement createElement(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString1, String paramString2, String paramString3)
  {
    if (paramSOAPDocumentImpl == null) {
      if ("http://schemas.xmlsoap.org/soap/envelope/".equals(paramString3)) {
        paramSOAPDocumentImpl = new SOAPPart1_1Impl().getDocument();
      } else if ("http://www.w3.org/2003/05/soap-envelope".equals(paramString3)) {
        paramSOAPDocumentImpl = new SOAPPart1_2Impl().getDocument();
      } else {
        paramSOAPDocumentImpl = new SOAPDocumentImpl(null);
      }
    }
    SOAPElement localSOAPElement = createNamedElement(paramSOAPDocumentImpl, paramString1, paramString2, paramString3);
    return localSOAPElement != null ? localSOAPElement : new ElementImpl(paramSOAPDocumentImpl, paramString3, NameImpl.createQName(paramString2, paramString1));
  }
  
  public static SOAPElement createNamedElement(SOAPDocumentImpl paramSOAPDocumentImpl, String paramString1, String paramString2, String paramString3)
  {
    if (paramString2 == null) {
      paramString2 = "SOAP-ENV";
    }
    if (paramString1.equalsIgnoreCase("Envelope"))
    {
      if ("http://schemas.xmlsoap.org/soap/envelope/".equals(paramString3)) {
        return new Envelope1_1Impl(paramSOAPDocumentImpl, paramString2);
      }
      if ("http://www.w3.org/2003/05/soap-envelope".equals(paramString3)) {
        return new Envelope1_2Impl(paramSOAPDocumentImpl, paramString2);
      }
    }
    if (paramString1.equalsIgnoreCase("Body"))
    {
      if ("http://schemas.xmlsoap.org/soap/envelope/".equals(paramString3)) {
        return new Body1_1Impl(paramSOAPDocumentImpl, paramString2);
      }
      if ("http://www.w3.org/2003/05/soap-envelope".equals(paramString3)) {
        return new Body1_2Impl(paramSOAPDocumentImpl, paramString2);
      }
    }
    if (paramString1.equalsIgnoreCase("Header"))
    {
      if ("http://schemas.xmlsoap.org/soap/envelope/".equals(paramString3)) {
        return new Header1_1Impl(paramSOAPDocumentImpl, paramString2);
      }
      if ("http://www.w3.org/2003/05/soap-envelope".equals(paramString3)) {
        return new Header1_2Impl(paramSOAPDocumentImpl, paramString2);
      }
    }
    if (paramString1.equalsIgnoreCase("Fault"))
    {
      Object localObject = null;
      if ("http://schemas.xmlsoap.org/soap/envelope/".equals(paramString3)) {
        localObject = new Fault1_1Impl(paramSOAPDocumentImpl, paramString2);
      } else if ("http://www.w3.org/2003/05/soap-envelope".equals(paramString3)) {
        localObject = new Fault1_2Impl(paramSOAPDocumentImpl, paramString2);
      }
      if (localObject != null) {
        return (SOAPElement)localObject;
      }
    }
    if (paramString1.equalsIgnoreCase("Detail"))
    {
      if ("http://schemas.xmlsoap.org/soap/envelope/".equals(paramString3)) {
        return new Detail1_1Impl(paramSOAPDocumentImpl, paramString2);
      }
      if ("http://www.w3.org/2003/05/soap-envelope".equals(paramString3)) {
        return new Detail1_2Impl(paramSOAPDocumentImpl, paramString2);
      }
    }
    if (((paramString1.equalsIgnoreCase("faultcode")) || (paramString1.equalsIgnoreCase("faultstring")) || (paramString1.equalsIgnoreCase("faultactor"))) && ("http://schemas.xmlsoap.org/soap/envelope/".equals(paramString3))) {
      return new FaultElement1_1Impl(paramSOAPDocumentImpl, paramString1, paramString2);
    }
    return null;
  }
  
  protected static void invalidCreate(String paramString)
  {
    throw new TreeException(paramString);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\messaging\saaj\soap\impl\ElementFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */