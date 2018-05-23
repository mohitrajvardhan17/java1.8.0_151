package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceRef;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-service-ref")
public class XmlWebServiceRef
  implements WebServiceRef
{
  @XmlAttribute(name="name")
  protected String name;
  @XmlAttribute(name="type")
  protected String type;
  @XmlAttribute(name="mappedName")
  protected String mappedName;
  @XmlAttribute(name="value")
  protected String value;
  @XmlAttribute(name="wsdlLocation")
  protected String wsdlLocation;
  @XmlAttribute(name="lookup")
  protected String lookup;
  
  public XmlWebServiceRef() {}
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public String getType()
  {
    return type;
  }
  
  public void setType(String paramString)
  {
    type = paramString;
  }
  
  public String getMappedName()
  {
    return mappedName;
  }
  
  public void setMappedName(String paramString)
  {
    mappedName = paramString;
  }
  
  public String getValue()
  {
    return value;
  }
  
  public void setValue(String paramString)
  {
    value = paramString;
  }
  
  public String getWsdlLocation()
  {
    return wsdlLocation;
  }
  
  public void setWsdlLocation(String paramString)
  {
    wsdlLocation = paramString;
  }
  
  public String getLookup()
  {
    return lookup;
  }
  
  public void setLookup(String paramString)
  {
    lookup = paramString;
  }
  
  public String name()
  {
    return Util.nullSafe(name);
  }
  
  public Class<?> type()
  {
    if (type == null) {
      return Object.class;
    }
    return Util.findClass(type);
  }
  
  public String mappedName()
  {
    return Util.nullSafe(mappedName);
  }
  
  public Class<? extends Service> value()
  {
    if (value == null) {
      return Service.class;
    }
    return Util.findClass(value);
  }
  
  public String wsdlLocation()
  {
    return Util.nullSafe(wsdlLocation);
  }
  
  public String lookup()
  {
    return Util.nullSafe(lookup);
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return WebServiceRef.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebServiceRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */