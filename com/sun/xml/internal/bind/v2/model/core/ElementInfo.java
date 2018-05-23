package com.sun.xml.internal.bind.v2.model.core;

import java.util.Collection;

public abstract interface ElementInfo<T, C>
  extends Element<T, C>
{
  public abstract ElementPropertyInfo<T, C> getProperty();
  
  public abstract NonElement<T, C> getContentType();
  
  public abstract T getContentInMemoryType();
  
  public abstract T getType();
  
  public abstract ElementInfo<T, C> getSubstitutionHead();
  
  public abstract Collection<? extends ElementInfo<T, C>> getSubstitutionMembers();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\ElementInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */