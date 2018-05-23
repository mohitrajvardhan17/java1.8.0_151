package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.soap.Addressing;
import javax.xml.ws.soap.AddressingFeature.Responses;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="addressing")
public class XmlAddressing
  implements Addressing
{
  @XmlAttribute(name="enabled")
  protected Boolean enabled;
  @XmlAttribute(name="required")
  protected Boolean required;
  
  public XmlAddressing() {}
  
  public Boolean getEnabled()
  {
    return Boolean.valueOf(enabled());
  }
  
  public void setEnabled(Boolean paramBoolean)
  {
    enabled = paramBoolean;
  }
  
  public Boolean getRequired()
  {
    return Boolean.valueOf(required());
  }
  
  public void setRequired(Boolean paramBoolean)
  {
    required = paramBoolean;
  }
  
  public boolean enabled()
  {
    return ((Boolean)Util.nullSafe(enabled, Boolean.valueOf(true))).booleanValue();
  }
  
  public boolean required()
  {
    return ((Boolean)Util.nullSafe(required, Boolean.valueOf(false))).booleanValue();
  }
  
  public AddressingFeature.Responses responses()
  {
    return AddressingFeature.Responses.ALL;
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return Addressing.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlAddressing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */