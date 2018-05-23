package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-service")
public class XmlWebService
  implements WebService
{
  @XmlAttribute(name="endpoint-interface")
  protected String endpointInterface;
  @XmlAttribute(name="name")
  protected String name;
  @XmlAttribute(name="port-name")
  protected String portName;
  @XmlAttribute(name="service-name")
  protected String serviceName;
  @XmlAttribute(name="target-namespace")
  protected String targetNamespace;
  @XmlAttribute(name="wsdl-location")
  protected String wsdlLocation;
  
  public XmlWebService() {}
  
  public String getEndpointInterface()
  {
    if (endpointInterface == null) {
      return "";
    }
    return endpointInterface;
  }
  
  public void setEndpointInterface(String paramString)
  {
    endpointInterface = paramString;
  }
  
  public String getName()
  {
    if (name == null) {
      return "";
    }
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public String getPortName()
  {
    if (portName == null) {
      return "";
    }
    return portName;
  }
  
  public void setPortName(String paramString)
  {
    portName = paramString;
  }
  
  public String getServiceName()
  {
    if (serviceName == null) {
      return "";
    }
    return serviceName;
  }
  
  public void setServiceName(String paramString)
  {
    serviceName = paramString;
  }
  
  public String getTargetNamespace()
  {
    if (targetNamespace == null) {
      return "";
    }
    return targetNamespace;
  }
  
  public void setTargetNamespace(String paramString)
  {
    targetNamespace = paramString;
  }
  
  public String getWsdlLocation()
  {
    if (wsdlLocation == null) {
      return "";
    }
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
  
  public String serviceName()
  {
    return Util.nullSafe(serviceName);
  }
  
  public String portName()
  {
    return Util.nullSafe(portName);
  }
  
  public String wsdlLocation()
  {
    return Util.nullSafe(wsdlLocation);
  }
  
  public String endpointInterface()
  {
    return Util.nullSafe(endpointInterface);
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return WebService.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebService.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */