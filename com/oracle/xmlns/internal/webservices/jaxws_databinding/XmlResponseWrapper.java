package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.ws.ResponseWrapper;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="response-wrapper")
public class XmlResponseWrapper
  implements ResponseWrapper
{
  @XmlAttribute(name="local-name")
  protected String localName;
  @XmlAttribute(name="target-namespace")
  protected String targetNamespace;
  @XmlAttribute(name="class-name")
  protected String className;
  @XmlAttribute(name="part-name")
  protected String partName;
  
  public XmlResponseWrapper() {}
  
  public String getLocalName()
  {
    if (localName == null) {
      return "";
    }
    return localName;
  }
  
  public void setLocalName(String paramString)
  {
    localName = paramString;
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
  
  public String getClassName()
  {
    if (className == null) {
      return "";
    }
    return className;
  }
  
  public void setClassName(String paramString)
  {
    className = paramString;
  }
  
  public String getPartName()
  {
    return partName;
  }
  
  public void setPartName(String paramString)
  {
    partName = paramString;
  }
  
  public String localName()
  {
    return Util.nullSafe(localName);
  }
  
  public String targetNamespace()
  {
    return Util.nullSafe(targetNamespace);
  }
  
  public String className()
  {
    return Util.nullSafe(className);
  }
  
  public String partName()
  {
    return Util.nullSafe(partName);
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return ResponseWrapper.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlResponseWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */