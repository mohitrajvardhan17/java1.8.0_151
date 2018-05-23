package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.ArrayInfo;
import java.lang.reflect.Type;

public abstract interface RuntimeArrayInfo
  extends ArrayInfo<Type, Class>, RuntimeNonElement
{
  public abstract Class getType();
  
  public abstract RuntimeNonElement getItemType();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeArrayInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */