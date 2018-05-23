package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlNsForm;
import javax.xml.bind.annotation.XmlSchema;

final class XmlSchemaQuick
  extends Quick
  implements XmlSchema
{
  private final XmlSchema core;
  
  public XmlSchemaQuick(Locatable paramLocatable, XmlSchema paramXmlSchema)
  {
    super(paramLocatable);
    core = paramXmlSchema;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlSchemaQuick(paramLocatable, (XmlSchema)paramAnnotation);
  }
  
  public Class<XmlSchema> annotationType()
  {
    return XmlSchema.class;
  }
  
  public String location()
  {
    return core.location();
  }
  
  public String namespace()
  {
    return core.namespace();
  }
  
  public XmlNs[] xmlns()
  {
    return core.xmlns();
  }
  
  public XmlNsForm elementFormDefault()
  {
    return core.elementFormDefault();
  }
  
  public XmlNsForm attributeFormDefault()
  {
    return core.attributeFormDefault();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlSchemaQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */