package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.Service.Mode;
import javax.xml.ws.ServiceMode;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="service-mode")
public class XmlServiceMode
  implements ServiceMode
{
  @XmlAttribute(name="value")
  protected String value;
  
  public XmlServiceMode() {}
  
  public String getValue()
  {
    if (value == null) {
      return "PAYLOAD";
    }
    return value;
  }
  
  public void setValue(String paramString)
  {
    value = paramString;
  }
  
  public Service.Mode value()
  {
    return Service.Mode.valueOf((String)Util.nullSafe(value, "PAYLOAD"));
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return ServiceMode.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlServiceMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */