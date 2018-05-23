package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public abstract interface FixedOrDefault
  extends TypedXmlWriter
{
  @XmlAttribute("default")
  public abstract FixedOrDefault _default(String paramString);
  
  @XmlAttribute
  public abstract FixedOrDefault fixed(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\FixedOrDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */