package com.sun.xml.internal.ws.api;

import java.util.List;
import javax.xml.ws.WebServiceFeature;

public class ComponentsFeature
  extends WebServiceFeature
  implements ServiceSharedFeatureMarker
{
  private final List<ComponentFeature> componentFeatures;
  
  public ComponentsFeature(List<ComponentFeature> paramList)
  {
    enabled = true;
    componentFeatures = paramList;
  }
  
  public String getID()
  {
    return ComponentsFeature.class.getName();
  }
  
  public List<ComponentFeature> getComponentFeatures()
  {
    return componentFeatures;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\ComponentsFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */