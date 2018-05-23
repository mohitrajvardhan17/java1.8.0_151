package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlRootElement;

final class XmlRootElementQuick
  extends Quick
  implements XmlRootElement
{
  private final XmlRootElement core;
  
  public XmlRootElementQuick(Locatable paramLocatable, XmlRootElement paramXmlRootElement)
  {
    super(paramLocatable);
    core = paramXmlRootElement;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlRootElementQuick(paramLocatable, (XmlRootElement)paramAnnotation);
  }
  
  public Class<XmlRootElement> annotationType()
  {
    return XmlRootElement.class;
  }
  
  public String name()
  {
    return core.name();
  }
  
  public String namespace()
  {
    return core.namespace();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlRootElementQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */