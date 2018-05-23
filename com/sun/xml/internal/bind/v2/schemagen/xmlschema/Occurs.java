package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public abstract interface Occurs
  extends TypedXmlWriter
{
  @XmlAttribute
  public abstract Occurs minOccurs(int paramInt);
  
  @XmlAttribute
  public abstract Occurs maxOccurs(String paramString);
  
  @XmlAttribute
  public abstract Occurs maxOccurs(int paramInt);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\Occurs.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */