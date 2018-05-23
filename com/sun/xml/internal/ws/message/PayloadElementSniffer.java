package com.sun.xml.internal.ws.message;

import com.sun.xml.internal.ws.encoding.soap.SOAP12Constants;
import com.sun.xml.internal.ws.encoding.soap.SOAPConstants;
import javax.xml.namespace.QName;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class PayloadElementSniffer
  extends DefaultHandler
{
  private boolean bodyStarted;
  private QName payloadQName;
  
  public PayloadElementSniffer() {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes)
    throws SAXException
  {
    if (bodyStarted)
    {
      payloadQName = new QName(paramString1, paramString2);
      throw new SAXException("Payload element found, interrupting the parsing process.");
    }
    if ((equalsQName(paramString1, paramString2, SOAPConstants.QNAME_SOAP_BODY)) || (equalsQName(paramString1, paramString2, SOAP12Constants.QNAME_SOAP_BODY))) {
      bodyStarted = true;
    }
  }
  
  private boolean equalsQName(String paramString1, String paramString2, QName paramQName)
  {
    return (paramQName.getLocalPart().equals(paramString2)) && (paramQName.getNamespaceURI().equals(paramString1));
  }
  
  public QName getPayloadQName()
  {
    return payloadQName;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\message\PayloadElementSniffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */