package com.sun.xml.internal.bind.v2.model.annotation;

import com.sun.xml.internal.bind.v2.runtime.Location;
import java.lang.annotation.Annotation;

public abstract class Quick
  implements Annotation, Locatable, Location
{
  private final Locatable upstream;
  
  protected Quick(Locatable paramLocatable)
  {
    upstream = paramLocatable;
  }
  
  protected abstract Annotation getAnnotation();
  
  protected abstract Quick newInstance(Locatable paramLocatable, Annotation paramAnnotation);
  
  public final Location getLocation()
  {
    return this;
  }
  
  public final Locatable getUpstream()
  {
    return upstream;
  }
  
  public final String toString()
  {
    return getAnnotation().toString();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\Quick.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */