package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public abstract interface MapPropertyInfo<T, C>
  extends PropertyInfo<T, C>
{
  public abstract QName getXmlName();
  
  public abstract boolean isCollectionNillable();
  
  public abstract NonElement<T, C> getKeyType();
  
  public abstract NonElement<T, C> getValueType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\MapPropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */