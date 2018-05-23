package com.sun.xml.internal.bind.v2.model.core;

public abstract interface NonElementRef<T, C>
{
  public abstract NonElement<T, C> getTarget();
  
  public abstract PropertyInfo<T, C> getSource();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\NonElementRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */