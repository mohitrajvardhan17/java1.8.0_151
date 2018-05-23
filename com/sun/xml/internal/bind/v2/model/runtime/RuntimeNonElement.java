package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.NonElement;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;

public abstract interface RuntimeNonElement
  extends NonElement<Type, Class>, RuntimeTypeInfo
{
  public abstract <V> Transducer<V> getTransducer();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeNonElement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */