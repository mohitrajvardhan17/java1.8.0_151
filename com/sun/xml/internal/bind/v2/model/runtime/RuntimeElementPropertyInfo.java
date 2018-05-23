package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ElementPropertyInfo;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;

public abstract interface RuntimeElementPropertyInfo
  extends ElementPropertyInfo<Type, Class>, RuntimePropertyInfo
{
  public abstract Collection<? extends RuntimeTypeInfo> ref();
  
  public abstract List<? extends RuntimeTypeRef> getTypes();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeElementPropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */