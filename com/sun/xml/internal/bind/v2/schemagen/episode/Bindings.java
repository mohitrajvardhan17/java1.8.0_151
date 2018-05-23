package com.sun.xml.internal.bind.v2.schemagen.episode;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;
import com.sun.xml.internal.txw2.annotation.XmlElement;

@XmlElement("bindings")
public abstract interface Bindings
  extends TypedXmlWriter
{
  @XmlElement
  public abstract Bindings bindings();
  
  @XmlElement("class")
  public abstract Klass klass();
  
  public abstract Klass typesafeEnumClass();
  
  @XmlElement
  public abstract SchemaBindings schemaBindings();
  
  @XmlAttribute
  public abstract void scd(String paramString);
  
  @XmlAttribute
  public abstract void version(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\schemagen\episode\Bindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */