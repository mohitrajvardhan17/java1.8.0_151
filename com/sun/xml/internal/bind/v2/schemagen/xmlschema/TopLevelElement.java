package com.sun.xml.internal.bind.v2.schemagen.xmlschema;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;
import javax.xml.namespace.QName;

@XmlElement("element")
public abstract interface TopLevelElement
  extends Element, TypedXmlWriter
{
  @XmlAttribute("final")
  public abstract TopLevelElement _final(String[] paramArrayOfString);
  
  @XmlAttribute("final")
  public abstract TopLevelElement _final(String paramString);
  
  @XmlAttribute("abstract")
  public abstract TopLevelElement _abstract(boolean paramBoolean);
  
  @XmlAttribute
  public abstract TopLevelElement substitutionGroup(QName paramQName);
  
  @XmlAttribute
  public abstract TopLevelElement name(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\xmlschema\TopLevelElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */