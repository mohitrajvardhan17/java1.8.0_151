package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("import")
public abstract interface Import
  extends Annotated, TypedXmlWriter
{
  @XmlAttribute
  public abstract Import namespace(String paramString);
  
  @XmlAttribute
  public abstract Import schemaLocation(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\Import.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */