package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("complexType")
public abstract interface ComplexType
  extends Annotated, ComplexTypeModel, TypedXmlWriter
{
  @XmlAttribute("final")
  public abstract ComplexType _final(String[] paramArrayOfString);
  
  @XmlAttribute("final")
  public abstract ComplexType _final(String paramString);
  
  @XmlAttribute
  public abstract ComplexType block(String[] paramArrayOfString);
  
  @XmlAttribute
  public abstract ComplexType block(String paramString);
  
  @XmlAttribute("abstract")
  public abstract ComplexType _abstract(boolean paramBoolean);
  
  @XmlAttribute
  public abstract ComplexType name(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\ComplexType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */