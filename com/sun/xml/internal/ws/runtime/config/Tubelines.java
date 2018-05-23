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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="tubelinesConfigCType", propOrder={"tubelineMappings", "tubelineDefinitions", "any"})
public class Tubelines
{
  @XmlElement(name="tubeline-mapping")
  protected List<TubelineMapping> tubelineMappings;
  @XmlElement(name="tubeline")
  protected List<TubelineDefinition> tubelineDefinitions;
  @XmlAnyElement(lax=true)
  protected List<Object> any;
  @XmlAttribute(name="default")
  @XmlSchemaType(name="anyURI")
  protected String _default;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public Tubelines() {}
  
  public List<TubelineMapping> getTubelineMappings()
  {
    if (tubelineMappings == null) {
      tubelineMappings = new ArrayList();
    }
    return tubelineMappings;
  }
  
  public List<TubelineDefinition> getTubelineDefinitions()
  {
    if (tubelineDefinitions == null) {
      tubelineDefinitions = new ArrayList();
    }
    return tubelineDefinitions;
  }
  
  public List<Object> getAny()
  {
    if (any == null) {
      any = new ArrayList();
    }
    return any;
  }
  
  public String getDefault()
  {
    return _default;
  }
  
  public void setDefault(String paramString)
  {
    _default = paramString;
  }
  
  public Map<QName, String> getOtherAttributes()
  {
    return otherAttributes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\runtime\config\Tubelines.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */