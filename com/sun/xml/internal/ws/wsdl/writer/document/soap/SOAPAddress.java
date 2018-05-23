package com.sun.xml.internal.ws.wsdl.writer.document.soap;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("address")
public abstract interface SOAPAddress
  extends TypedXmlWriter
{
  @XmlAttribute
  public abstract SOAPAddress location(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\soap\SOAPAddress.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */