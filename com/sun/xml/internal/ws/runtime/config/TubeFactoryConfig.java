package com.sun.xml.internal.ws.runtime.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="tubeFactoryCType", propOrder={"any"})
public class TubeFactoryConfig
{
  @XmlAnyElement(lax=true)
  protected List<Object> any;
  @XmlAttribute(required=true)
  protected String className;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public TubeFactoryConfig() {}
  
  public List<Object> getAny()
  {
    if (any == null) {
      any = new ArrayList();
    }
    return any;
  }
  
  public String getClassName()
  {
    return className;
  }
  
  public void setClassName(String paramString)
  {
    className = paramString;
  }
  
  public Map<QName, String> getOtherAttributes()
  {
    return otherAttributes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\runtime\config\TubeFactoryConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */