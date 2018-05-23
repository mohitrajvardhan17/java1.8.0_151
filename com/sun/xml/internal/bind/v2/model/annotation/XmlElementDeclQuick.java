package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlElementDecl;

final class XmlElementDeclQuick
  extends Quick
  implements XmlElementDecl
{
  private final XmlElementDecl core;
  
  public XmlElementDeclQuick(Locatable paramLocatable, XmlElementDecl paramXmlElementDecl)
  {
    super(paramLocatable);
    core = paramXmlElementDecl;
  }
  
  protected Annotation getAnnotation()
  {
    return core;
  }
  
  protected Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation)
  {
    return new XmlElementDeclQuick(paramLocatable, (XmlElementDecl)paramAnnotation);
  }
  
  public Class<XmlElementDecl> annotationType()
  {
    return XmlElementDecl.class;
  }
  
  public String name()
  {
    return core.name();
  }
  
  public Class scope()
  {
    return core.scope();
  }
  
  public String namespace()
  {
    return core.namespace();
  }
  
  public String defaultValue()
  {
    return core.defaultValue();
  }
  
  public String substitutionHeadNamespace()
  {
    return core.substitutionHeadNamespace();
  }
  
  public String substitutionHeadName()
  {
    return core.substitutionHeadName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\XmlElementDeclQuick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */