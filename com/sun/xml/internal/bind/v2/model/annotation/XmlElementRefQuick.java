package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementRef;

final class XmlElementRefQuick
  extends Quick
  implements XmlElementRef
{
  private final XmlElementRef core;
  
  public XmlElementRefQuick(Locatable paramLocatable, XmlElementRef paramXmlElementRef)
  {
    super(paramLocatable);
    core = paramXmlElementRef;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlElementRefQuick(paramLocatable, (XmlElementRef)paramAnnotation);
  }
  
  public Class<XmlElementRef> annotationType()
  {
    return XmlElementRef.class;
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
  
  public boolean required()
  {
    return core.required();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlElementRefQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */