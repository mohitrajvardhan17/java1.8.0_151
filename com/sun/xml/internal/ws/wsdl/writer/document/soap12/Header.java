package com.sun.xml.internal.ws.wsdl.writer.document.soap12;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("header")
public abstract interface Header
  extends TypedXmlWriter, BodyType
{
  @XmlAttribute
  public abstract Header message(QName paramQName);
  
  @XmlElement
  public abstract HeaderFault headerFault();
  
  @XmlAttribute
  public abstract BodyType part(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\soap12\Header.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */