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
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.namespace.QName;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name="tubelineDefinitionCType", propOrder={"clientSide", "endpointSide", "any"})
public class TubelineDefinition
{
  @XmlElement(name="client-side")
  protected TubeFactoryList clientSide;
  @XmlElement(name="endpoint-side")
  protected TubeFactoryList endpointSide;
  @XmlAnyElement(lax=true)
  protected List<Object> any;
  @XmlAttribute
  @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
  @XmlID
  @XmlSchemaType(name="ID")
  protected String name;
  @XmlAnyAttribute
  private Map<QName, String> otherAttributes = new HashMap();
  
  public TubelineDefinition() {}
  
  public TubeFactoryList getClientSide()
  {
    return clientSide;
  }
  
  public void setClientSide(TubeFactoryList paramTubeFactoryList)
  {
    clientSide = paramTubeFactoryList;
  }
  
  public TubeFactoryList getEndpointSide()
  {
    return endpointSide;
  }
  
  public void setEndpointSide(TubeFactoryList paramTubeFactoryList)
  {
    endpointSide = paramTubeFactoryList;
  }
  
  public List<Object> getAny()
  {
    if (any == null) {
      any = new ArrayList();
    }
    return any;
  }
  
  public String getName()
  {
    return name;
  }
  
  public void setName(String paramString)
  {
    name = paramString;
  }
  
  public Map<QName, String> getOtherAttributes()
  {
    return otherAttributes;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\runtime\config\TubelineDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */