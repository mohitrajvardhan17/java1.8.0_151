package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.NonElementRef;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;

public abstract interface RuntimeNonElementRef
  extends NonElementRef<Type, Class>
{
  public abstract RuntimeNonElement getTarget();
  
  public abstract RuntimePropertyInfo getSource();
  
  public abstract Transducer getTransducer();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeNonElementRef.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */