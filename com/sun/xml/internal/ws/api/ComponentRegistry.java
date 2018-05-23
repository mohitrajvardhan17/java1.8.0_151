package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import java.util.Set;

public abstract interface ComponentRegistry
  extends Component
{
  @NotNull
  public abstract Set<Component> getComponents();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\api\ComponentRegistry.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */