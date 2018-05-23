package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebServiceClient;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-service-client")
public class XmlWebServiceClient
  implements WebServiceClient
{
  @XmlAttribute(name="name")
  protected String name;
  @XmlAttribute(name="targetNamespace")
  protected String targetNamespace;
  @XmlAttribute(name="wsdlLocation")
  protected String wsdlLocation;
  
  public XmlWebServiceClient() {}
  
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
  
  public String getWsdlLocation()
  {
    return wsdlLocation;
  }
  
  public void setWsdlLocation(String paramString)
  {
    wsdlLocation = paramString;
  }
  
  public String name()
  {
    return Util.nullSafe(name);
  }
  
  public String targetNamespace()
  {
    return Util.nullSafe(targetNamespace);
  }
  
  public String wsdlLocation()
  {
    return Util.nullSafe(wsdlLocation);
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return WebServiceClient.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebServiceClient.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */