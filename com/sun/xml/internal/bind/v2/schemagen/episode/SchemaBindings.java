package com.sun.xml.internal.bind.v2.schemagen.episode;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

public abstract interface SchemaBindings
  extends TypedXmlWriter
{
  @XmlAttribute
  public abstract void map(boolean paramBoolean);
  
  @XmlElement("package")
  public abstract Package _package();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\episode\SchemaBindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */