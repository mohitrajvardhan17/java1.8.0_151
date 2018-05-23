package com.sun.xml.internal.ws.addressing;

import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import com.sun.xml.internal.ws.api.addressing.WSEndpointReference.EPRExtension;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class WSEPRExtension
  extends WSEndpointReference.EPRExtension
{
  XMLStreamBuffer xsb;
  final QName qname;
  
  public WSEPRExtension(XMLStreamBuffer paramXMLStreamBuffer, QName paramQName)
  {
    xsb = paramXMLStreamBuffer;
    qname = paramQName;
  }
  
  public XMLStreamReader readAsXMLStreamReader()
    throws XMLStreamException
  {
    return xsb.readAsXMLStreamReader();
  }
  
  public QName getQName()
  {
    return qname;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\addressing\WSEPRExtension.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */