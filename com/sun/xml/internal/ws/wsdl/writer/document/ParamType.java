package com.sun.xml.internal.ws.wsdl.writer.document;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import javax.xml.namespace.QName;

public abstract interface ParamType
  extends TypedXmlWriter, Documented
{
  @XmlAttribute
  public abstract ParamType message(QName paramQName);
  
  @XmlAttribute
  public abstract ParamType name(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\ParamType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */