package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("simpleContent")
public abstract interface SimpleContent
  extends Annotated, TypedXmlWriter
{
  @XmlElement
  public abstract SimpleExtension extension();
  
  @XmlElement
  public abstract SimpleRestriction restriction();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\SimpleContent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */