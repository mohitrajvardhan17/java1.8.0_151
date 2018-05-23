package com.sun.xml.internal.bind.v2.model.core;

import javax.xml.namespace.QName;

public abstract interface TypeRef<T, C>
  extends NonElementRef<T, C>
{
  public abstract QName getTagName();
  
  public abstract boolean isNillable();
  
  public abstract String getDefaultValue();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\core\TypeRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */