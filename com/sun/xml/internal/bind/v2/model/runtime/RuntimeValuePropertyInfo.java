package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ValuePropertyInfo;
import java.lang.reflect.Type;

public abstract interface RuntimeValuePropertyInfo
  extends ValuePropertyInfo<Type, Class>, RuntimePropertyInfo, RuntimeNonElementRef
{
  public abstract RuntimeNonElement getTarget();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeValuePropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */