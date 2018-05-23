package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlTransient;

final class XmlTransientQuick
  extends Quick
  implements XmlTransient
{
  private final XmlTransient core;
  
  public XmlTransientQuick(Locatable paramLocatable, XmlTransient paramXmlTransient)
  {
    super(paramLocatable);
    core = paramXmlTransient;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlTransientQuick(paramLocatable, (XmlTransient)paramAnnotation);
  }
  
  public Class<XmlTransient> annotationType()
  {
    return XmlTransient.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlTransientQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */