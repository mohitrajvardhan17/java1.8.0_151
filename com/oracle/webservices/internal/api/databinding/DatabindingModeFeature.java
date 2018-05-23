package com.oracle.webservices.internal.api.databinding;

import com.sun.xml.internal.ws.api.ServiceSharedFeatureMarker;
import java.util.HashMap;
import java.util.Map;
import javax.xml.ws.WebServiceFeature;

public class DatabindingModeFeature
  extends WebServiceFeature
  implements ServiceSharedFeatureMarker
{
  public static final String ID = "http://jax-ws.java.net/features/databinding";
  public static final String GLASSFISH_JAXB = "glassfish.jaxb";
  private String mode;
  private Map<String, Object> properties;
  
  public DatabindingModeFeature(String paramString)
  {
    mode = paramString;
    properties = new HashMap();
  }
  
  public String getMode()
  {
    return mode;
  }
  
  public String getID()
  {
    return "http://jax-ws.java.net/features/databinding";
  }
  
  public Map<String, Object> getProperties()
  {
    return properties;
  }
  
  public static Builder builder()
  {
    return new Builder(new DatabindingModeFeature(null));
  }
  
  public static final class Builder
  {
    private final DatabindingModeFeature o;
    
    Builder(DatabindingModeFeature paramDatabindingModeFeature)
    {
      o = paramDatabindingModeFeature;
    }
    
    public DatabindingModeFeature build()
    {
      return o;
    }
    
    public Builder value(String paramString)
    {
      o.mode = paramString;
      return this;
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\oracle\webservices\internal\api\databinding\DatabindingModeFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */