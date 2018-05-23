package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;

final class XmlElementRefsQuick
  extends Quick
  implements XmlElementRefs
{
  private final XmlElementRefs core;
  
  public XmlElementRefsQuick(Locatable paramLocatable, XmlElementRefs paramXmlElementRefs)
  {
    super(paramLocatable);
    core = paramXmlElementRefs;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlElementRefsQuick(paramLocatable, (XmlElementRefs)paramAnnotation);
  }
  
  public Class<XmlElementRefs> annotationType()
  {
    return XmlElementRefs.class;
  }
  
  public XmlElementRef[] value()
  {
    return core.value();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlElementRefsQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */