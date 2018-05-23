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
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="tubelineMappingCType", propOrder={"endpointRef", "tubelineRef", "any"})
public class TubelineMapping
{
  @XmlElement(name="endpoint-ref", required=true)
  @XmlSchemaType(name="anyURI")
  protected String endpointRef;
  @XmlElement(name="tubeline-ref", required=true)
  @XmlSchemaType(name="anyURI")
  protected String tubelineRef;
  @XmlAnyElement(lax=true)
  protected List<Object> any;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public TubelineMapping() {}
  
  public String getEndpointRef()
  {
    return endpointRef;
  }
  
  public void setEndpointRef(String paramString)
  {
    endpointRef = paramString;
  }
  
  public String getTubelineRef()
  {
    return tubelineRef;
  }
  
  public void setTubelineRef(String paramString)
  {
    tubelineRef = paramString;
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


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\runtime\config\TubelineMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */