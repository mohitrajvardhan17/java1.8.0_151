package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.WebServiceProvider;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-service-provider")
public class XmlWebServiceProvider
  implements WebServiceProvider
{
  @XmlAttribute(name="targetNamespace")
  protected String targetNamespace;
  @XmlAttribute(name="serviceName")
  protected String serviceName;
  @XmlAttribute(name="portName")
  protected String portName;
  @XmlAttribute(name="wsdlLocation")
  protected String wsdlLocation;
  
  public XmlWebServiceProvider() {}
  
  public String getTargetNamespace()
  {
    return targetNamespace;
  }
  
  public void setTargetNamespace(String paramString)
  {
    targetNamespace = paramString;
  }
  
  public String getServiceName()
  {
    return serviceName;
  }
  
  public void setServiceName(String paramString)
  {
    serviceName = paramString;
  }
  
  public String getPortName()
  {
    return portName;
  }
  
  public void setPortName(String paramString)
  {
    portName = paramString;
  }
  
  public String getWsdlLocation()
  {
    return wsdlLocation;
  }
  
  public void setWsdlLocation(String paramString)
  {
    wsdlLocation = paramString;
  }
  
  public String wsdlLocation()
  {
    return Util.nullSafe(wsdlLocation);
  }
  
  public String serviceName()
  {
    return Util.nullSafe(serviceName);
  }
  
  public String targetNamespace()
  {
    return Util.nullSafe(targetNamespace);
  }
  
  public String portName()
  {
    return Util.nullSafe(portName);
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return WebServiceProvider.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebServiceProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */