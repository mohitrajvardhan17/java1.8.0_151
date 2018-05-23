package com.sun.xml.internal.ws.wsdl.writer.document.xsd;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import com.sun.xml.internal.ws.wsdl.writer.document.Documented;

@XmlElement("import")
public abstract interface Import
  extends TypedXmlWriter, Documented
{
  @XmlAttribute
  public abstract Import schemaLocation(String paramString);
  
  @XmlAttribute
  public abstract Import namespace(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\xsd\Import.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */