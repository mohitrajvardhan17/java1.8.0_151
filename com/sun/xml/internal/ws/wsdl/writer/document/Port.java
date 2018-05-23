package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("port")
public abstract interface Port
  extends TypedXmlWriter, Documented
{
  @XmlAttribute
  public abstract Port name(String paramString);
  
  @XmlAttribute
  public abstract Port arrayType(String paramString);
  
  @XmlAttribute
  public abstract Port binding(QName paramQName);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\Port.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */