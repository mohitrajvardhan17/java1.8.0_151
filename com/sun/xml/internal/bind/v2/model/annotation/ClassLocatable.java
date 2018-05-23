package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;

public class ClassLocatable<C>
  implements Locatable
{
  private final Locatable upstream;
  private final C clazz;
  private final Navigator<?, C, ?, ?> nav;
  
  public ClassLocatable(Locatable paramLocatable, C paramC, Navigator<?, C, ?, ?> paramNavigator)
  {
    upstream = paramLocatable;
    clazz = paramC;
    nav = paramNavigator;
  }
  
  public Locatable getUpstream()
  {
    return upstream;
  }
  
  public Location getLocation()
  {
    return nav.getClassLocation(clazz);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\ClassLocatable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */