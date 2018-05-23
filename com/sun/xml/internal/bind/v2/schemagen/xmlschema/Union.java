package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("union")
public abstract interface Union
  extends Annotated, SimpleTypeHost, TypedXmlWriter
{
  @XmlAttribute
  public abstract Union memberTypes(QName[] paramArrayOfQName);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\Union.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */