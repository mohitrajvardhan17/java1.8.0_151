package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("simpleType")
public abstract interface SimpleType
  extends Annotated, SimpleDerivation, TypedXmlWriter
{
  @XmlAttribute("final")
  public abstract SimpleType _final(String paramString);
  
  @XmlAttribute("final")
  public abstract SimpleType _final(String[] paramArrayOfString);
  
  @XmlAttribute
  public abstract SimpleType name(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\SimpleType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */