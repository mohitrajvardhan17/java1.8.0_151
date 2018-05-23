package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.ws.wsdl.writer.document.xsd.Schema;

@XmlElement("types")
public abstract interface Types
  extends TypedXmlWriter, Documented
{
  @XmlElement(value="schema", ns="http://www.w3.org/2001/XMLSchema")
  public abstract Schema schema();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\Types.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */