package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("service")
public abstract interface Service
  extends TypedXmlWriter, Documented
{
  @XmlAttribute
  public abstract Service name(String paramString);
  
  @XmlElement
  public abstract Port port();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\Service.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */