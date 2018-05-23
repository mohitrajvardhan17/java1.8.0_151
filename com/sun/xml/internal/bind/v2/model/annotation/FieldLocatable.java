package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.model.nav.Navigator;
import com.sun.xml.internal.bind.v2.runtime.Location;

public class FieldLocatable<F>
  implements Locatable
{
  private final Locatable upstream;
  private final F field;
  private final Navigator<?, ?, F, ?> nav;
  
  public FieldLocatable(Locatable paramLocatable, F paramF, Navigator<?, ?, F, ?> paramNavigator)
  {
    upstream = paramLocatable;
    field = paramF;
    nav = paramNavigator;
  }
  
  public Locatable getUpstream()
  {
    return upstream;
  }
  
  public Location getLocation()
  {
    return nav.getFieldLocation(field);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\FieldLocatable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */