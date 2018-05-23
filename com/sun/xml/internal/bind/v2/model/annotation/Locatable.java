package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.runtime.Location;

public abstract interface Locatable
{
  public abstract Locatable getUpstream();
  
  public abstract Location getLocation();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\Locatable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */