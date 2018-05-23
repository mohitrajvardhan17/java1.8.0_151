package com.sun.xml.internal.ws.api;

import javax.xml.ws.WebServiceFeature;

public class ComponentFeature
  extends WebServiceFeature
  implements ServiceSharedFeatureMarker
{
  private final Component component;
  private final Target target;
  
  public ComponentFeature(Component paramComponent)
  {
    this(paramComponent, Target.CONTAINER);
  }
  
  public ComponentFeature(Component paramComponent, Target paramTarget)
  {
    enabled = true;
    component = paramComponent;
    target = paramTarget;
  }
  
  public String getID()
  {
    return ComponentFeature.class.getName();
  }
  
  public Component getComponent()
  {
    return component;
  }
  
  public Target getTarget()
  {
    return target;
  }
  
  public static enum Target
  {
    CONTAINER,  ENDPOINT,  SERVICE,  STUB;
    
    private Target() {}
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\ComponentFeature.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */