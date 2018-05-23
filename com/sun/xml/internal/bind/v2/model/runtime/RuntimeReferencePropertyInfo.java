package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ReferencePropertyInfo;
import java.lang.reflect.Type;
import java.util.Set;

public abstract interface RuntimeReferencePropertyInfo
  extends ReferencePropertyInfo<Type, Class>, RuntimePropertyInfo
{
  public abstract Set<? extends RuntimeElement> getElements();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeReferencePropertyInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */