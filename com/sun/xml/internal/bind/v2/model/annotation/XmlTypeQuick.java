package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlType;

final class XmlTypeQuick
  extends Quick
  implements XmlType
{
  private final XmlType core;
  
  public XmlTypeQuick(Locatable paramLocatable, XmlType paramXmlType)
  {
    super(paramLocatable);
    core = paramXmlType;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlTypeQuick(paramLocatable, (XmlType)paramAnnotation);
  }
  
  public Class<XmlType> annotationType()
  {
    return XmlType.class;
  }
  
  public String name()
  {
    return core.name();
  }
  
  public String namespace()
  {
    return core.namespace();
  }
  
  public String[] propOrder()
  {
    return core.propOrder();
  }
  
  public Class factoryClass()
  {
    return core.factoryClass();
  }
  
  public String factoryMethod()
  {
    return core.factoryMethod();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlTypeQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */