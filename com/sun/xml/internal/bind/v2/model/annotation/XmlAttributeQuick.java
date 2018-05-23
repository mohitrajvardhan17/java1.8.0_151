package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAttribute;

final class XmlAttributeQuick
  extends Quick
  implements XmlAttribute
{
  private final XmlAttribute core;
  
  public XmlAttributeQuick(Locatable paramLocatable, XmlAttribute paramXmlAttribute)
  {
    super(paramLocatable);
    core = paramXmlAttribute;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlAttributeQuick(paramLocatable, (XmlAttribute)paramAnnotation);
  }
  
  public Class<XmlAttribute> annotationType()
  {
    return XmlAttribute.class;
  }
  
  public String name()
  {
    return core.name();
  }
  
  public String namespace()
  {
    return core.namespace();
  }
  
  public boolean required()
  {
    return core.required();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlAttributeQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */