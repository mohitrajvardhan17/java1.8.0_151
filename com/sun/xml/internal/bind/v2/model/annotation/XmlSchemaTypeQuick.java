package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlSchemaType;

final class XmlSchemaTypeQuick
  extends Quick
  implements XmlSchemaType
{
  private final XmlSchemaType core;
  
  public XmlSchemaTypeQuick(Locatable paramLocatable, XmlSchemaType paramXmlSchemaType)
  {
    super(paramLocatable);
    core = paramXmlSchemaType;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlSchemaTypeQuick(paramLocatable, (XmlSchemaType)paramAnnotation);
  }
  
  public Class<XmlSchemaType> annotationType()
  {
    return XmlSchemaType.class;
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
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlSchemaTypeQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */