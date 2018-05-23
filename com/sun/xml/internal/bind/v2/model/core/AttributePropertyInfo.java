package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public abstract interface AttributePropertyInfo<T, C>
  extends PropertyInfo<T, C>, NonElementRef<T, C>
{
  public abstract NonElement<T, C> getTarget();
  
  public abstract boolean isRequired();
  
  public abstract QName getXmlName();
  
  public abstract Adapter<T, C> getAdapter();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\AttributePropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */