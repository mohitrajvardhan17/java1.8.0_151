package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("portType")
public abstract interface PortType
  extends TypedXmlWriter, Documented
{
  @XmlAttribute
  public abstract PortType name(String paramString);
  
  @XmlElement
  public abstract Operation operation();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\PortType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */