package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.MapPropertyInfo;
import java.lang.reflect.Type;

public abstract interface RuntimeMapPropertyInfo
  extends RuntimePropertyInfo, MapPropertyInfo<Type, Class>
{
  public abstract RuntimeNonElement getKeyType();
  
  public abstract RuntimeNonElement getValueType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeMapPropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */