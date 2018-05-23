package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public abstract interface Wildcard
  extends Annotated, TypedXmlWriter
{
  @XmlAttribute
  public abstract Wildcard processContents(String paramString);
  
  @XmlAttribute
  public abstract Wildcard namespace(String[] paramArrayOfString);
  
  @XmlAttribute
  public abstract Wildcard namespace(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\Wildcard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */