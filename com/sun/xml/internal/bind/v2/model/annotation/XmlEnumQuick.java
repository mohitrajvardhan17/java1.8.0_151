package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlEnum;

final class XmlEnumQuick
  extends Quick
  implements XmlEnum
{
  private final XmlEnum core;
  
  public XmlEnumQuick(Locatable paramLocatable, XmlEnum paramXmlEnum)
  {
    super(paramLocatable);
    core = paramXmlEnum;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlEnumQuick(paramLocatable, (XmlEnum)paramAnnotation);
  }
  
  public Class<XmlEnum> annotationType()
  {
    return XmlEnum.class;
  }
  
  public Class value()
  {
    return core.value();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlEnumQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */