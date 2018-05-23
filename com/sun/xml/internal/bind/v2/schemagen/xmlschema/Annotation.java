package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("annotation")
public abstract interface Annotation
  extends TypedXmlWriter
{
  @XmlElement
  public abstract Appinfo appinfo();
  
  @XmlElement
  public abstract Documentation documentation();
  
  @XmlAttribute
  public abstract Annotation id(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\Annotation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */