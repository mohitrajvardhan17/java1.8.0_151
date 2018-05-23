package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlValue;

final class XmlValueQuick
  extends Quick
  implements XmlValue
{
  private final XmlValue core;
  
  public XmlValueQuick(Locatable paramLocatable, XmlValue paramXmlValue)
  {
    super(paramLocatable);
    core = paramXmlValue;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlValueQuick(paramLocatable, (XmlValue)paramAnnotation);
  }
  
  public Class<XmlValue> annotationType()
  {
    return XmlValue.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlValueQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */