package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;

public class MethodLocatable<M>
  implements Locatable
{
  private final Locatable upstream;
  private final M method;
  private final Navigator<?, ?, ?, M> nav;
  
  public MethodLocatable(Locatable paramLocatable, M paramM, Navigator<?, ?, ?, M> paramNavigator)
  {
    upstream = paramLocatable;
    method = paramM;
    nav = paramNavigator;
  }
  
  public Locatable getUpstream()
  {
    return upstream;
  }
  
  public Location getLocation()
  {
    return nav.getMethodLocation(method);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\MethodLocatable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */