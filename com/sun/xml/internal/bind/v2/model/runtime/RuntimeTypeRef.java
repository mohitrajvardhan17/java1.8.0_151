package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.TypeRef;
import java.lang.reflect.Type;

public abstract interface RuntimeTypeRef
  extends TypeRef<Type, Class>, RuntimeNonElementRef
{
  public abstract RuntimeNonElement getTarget();
  
  public abstract RuntimePropertyInfo getSource();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeTypeRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */