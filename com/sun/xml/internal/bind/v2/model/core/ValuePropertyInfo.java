package com.sun.xml.internal.bind.v2.model.core;

public abstract interface ValuePropertyInfo<T, C>
  extends PropertyInfo<T, C>, NonElementRef<T, C>
{
  public abstract Adapter<T, C> getAdapter();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\ValuePropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */