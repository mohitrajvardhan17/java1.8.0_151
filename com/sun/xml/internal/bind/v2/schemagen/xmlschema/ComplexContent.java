package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("complexContent")
public abstract interface ComplexContent
  extends Annotated, TypedXmlWriter
{
  @XmlElement
  public abstract ComplexExtension extension();
  
  @XmlElement
  public abstract ComplexRestriction restriction();
  
  @XmlAttribute
  public abstract ComplexContent mixed(boolean paramBoolean);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\ComplexContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */