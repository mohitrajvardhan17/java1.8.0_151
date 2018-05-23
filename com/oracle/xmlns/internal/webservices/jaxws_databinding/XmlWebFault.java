package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebFault;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-fault")
public class XmlWebFault
  implements WebFault
{
  @XmlAttribute(name="name")
  protected String name;
  @XmlAttribute(name="targetNamespace")
  protected String targetNamespace;
  @XmlAttribute(name="faultBean")
  protected String faultBean;
  @XmlAttribute(name="messageName")
  protected String messageName;
  
  public XmlWebFault() {}
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public String getTargetNamespace()
  {
    return targetNamespace;
  }
  
  public void setTargetNamespace(String paramString)
  {
    targetNamespace = paramString;
  }
  
  public String getFaultBean()
  {
    return faultBean;
  }
  
  public void setFaultBean(String paramString)
  {
    faultBean = paramString;
  }
  
  public String name()
  {
    return Util.nullSafe(name);
  }
  
  public String targetNamespace()
  {
    return Util.nullSafe(targetNamespace);
  }
  
  public String faultBean()
  {
    return Util.nullSafe(faultBean);
  }
  
  public String messageName()
  {
    return Util.nullSafe(messageName);
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return WebFault.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebFault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */