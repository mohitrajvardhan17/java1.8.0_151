package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public abstract interface TypeDefParticle
  extends TypedXmlWriter
{
  @XmlElement
  public abstract ExplicitGroup all();
  
  @XmlElement
  public abstract ExplicitGroup sequence();
  
  @XmlElement
  public abstract ExplicitGroup choice();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\TypeDefParticle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */