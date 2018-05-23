package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElement;

final class XmlElementQuick
  extends Quick
  implements XmlElement
{
  private final XmlElement core;
  
  public XmlElementQuick(Locatable paramLocatable, XmlElement paramXmlElement)
  {
    super(paramLocatable);
    core = paramXmlElement;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlElementQuick(paramLocatable, (XmlElement)paramAnnotation);
  }
  
  public Class<XmlElement> annotationType()
  {
    return XmlElement.class;
  }
  
  public String name()
  {
    return core.name();
  }
  
  public Class type()
  {
    return core.type();
  }
  
  public String namespace()
  {
    return core.namespace();
  }
  
  public String defaultValue()
  {
    return core.defaultValue();
  }
  
  public boolean required()
  {
    return core.required();
  }
  
  public boolean nillable()
  {
    return core.nillable();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlElementQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */