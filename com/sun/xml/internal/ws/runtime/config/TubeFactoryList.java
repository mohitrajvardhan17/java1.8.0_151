package com.sun.xml.internal.ws.runtime.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="tubeFactoryListCType", propOrder={"tubeFactoryConfigs", "any"})
public class TubeFactoryList
{
  @XmlElement(name="tube-factory", required=true)
  protected List<TubeFactoryConfig> tubeFactoryConfigs;
  @XmlAnyElement(lax=true)
  protected List<Object> any;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public TubeFactoryList() {}
  
  public List<TubeFactoryConfig> getTubeFactoryConfigs()
  {
    if (tubeFactoryConfigs == null) {
      tubeFactoryConfigs = new ArrayList();
    }
    return tubeFactoryConfigs;
  }
  
  public List<Object> getAny()
  {
    if (any == null) {
      any = new ArrayList();
    }
    return any;
  }
  
  public Map<QName, String> getOtherAttributes()
  {
    return otherAttributes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\runtime\config\TubeFactoryList.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */