package com.sun.xml.internal.ws.wsdl.writer.document.soap12;

import com.sun.xml.internal.txw2.TypedXmlWriter;
import com.sun.xml.internal.txw2.annotation.XmlAttribute;

public abstract interface BodyType
  extends TypedXmlWriter
{
  @XmlAttribute
  public abstract BodyType encodingStyle(String paramString);
  
  @XmlAttribute
  public abstract BodyType namespace(String paramString);
  
  @XmlAttribute
  public abstract BodyType use(String paramString);
  
  @XmlAttribute
  public abstract BodyType parts(String paramString);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\wsdl\writer\document\soap12\BodyType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */