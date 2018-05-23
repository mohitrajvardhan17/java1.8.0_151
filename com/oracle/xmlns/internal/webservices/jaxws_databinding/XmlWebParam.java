package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import java.lang.annotation.Annotation;
import javax.jws.WebParam;
import javax.jws.WebParam.Mode;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="")
@XmlRootElement(name="web-param")
public class XmlWebParam
  implements WebParam
{
  @XmlAttribute(name="header")
  protected Boolean header;
  @XmlAttribute(name="mode")
  protected WebParamMode mode;
  @XmlAttribute(name="name")
  protected String name;
  @XmlAttribute(name="part-name")
  protected String partName;
  @XmlAttribute(name="target-namespace")
  protected String targetNamespace;
  
  public XmlWebParam() {}
  
  public boolean isHeader()
  {
    if (header == null) {
      return false;
    }
    return header.booleanValue();
  }
  
  public void setHeader(Boolean paramBoolean)
  {
    header = paramBoolean;
  }
  
  public WebParamMode getMode()
  {
    if (mode == null) {
      return WebParamMode.IN;
    }
    return mode;
  }
  
  public void setMode(WebParamMode paramWebParamMode)
  {
    mode = paramWebParamMode;
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
  
  public String getPartName()
  {
    if (partName == null) {
      return "";
    }
    return partName;
  }
  
  public void setPartName(String paramString)
  {
    partName = paramString;
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
  
  public String name()
  {
    return Util.nullSafe(name);
  }
  
  public String partName()
  {
    return Util.nullSafe(partName);
  }
  
  public String targetNamespace()
  {
    return Util.nullSafe(targetNamespace);
  }
  
  public WebParam.Mode mode()
  {
    return (WebParam.Mode)Util.nullSafe(mode, WebParam.Mode.IN);
  }
  
  public boolean header()
  {
    return ((Boolean)Util.nullSafe(header, Boolean.valueOf(false))).booleanValue();
  }
  
  public Class<? extends Annotation> annotationType()
  {
    return WebParam.class;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\XmlWebParam.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */