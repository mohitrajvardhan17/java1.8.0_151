package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("documentation")
public abstract interface Documentation
  extends TypedXmlWriter
{
  @XmlAttribute
  public abstract Documentation source(String paramString);
  
  @XmlAttribute(ns="http://www.w3.org/XML/1998/namespace")
  public abstract Documentation lang(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\Documentation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */