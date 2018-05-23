package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public abstract interface AttrDecls
  extends TypedXmlWriter
{
  @XmlElement
  public abstract LocalAttribute attribute();
  
  @XmlElement
  public abstract Wildcard anyAttribute();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\AttrDecls.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */