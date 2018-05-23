package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("definitions")
public abstract interface Definitions
  extends TypedXmlWriter, Documented
{
  @XmlAttribute
  public abstract Definitions name(String paramString);
  
  @XmlAttribute
  public abstract Definitions targetNamespace(String paramString);
  
  @XmlElement
  public abstract Service service();
  
  @XmlElement
  public abstract Binding binding();
  
  @XmlElement
  public abstract PortType portType();
  
  @XmlElement
  public abstract Message message();
  
  @XmlElement
  public abstract Types types();
  
  @XmlElement("import")
  public abstract Import _import();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\Definitions.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */