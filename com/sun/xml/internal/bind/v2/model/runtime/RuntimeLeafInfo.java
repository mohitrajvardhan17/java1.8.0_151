package com.sun.xml.internal.bind.v2.model.runtime;

import com.sun.xml.internal.bind.v2.model.core.LeafInfo;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;
import javax.xml.namespace.QName;

public abstract interface RuntimeLeafInfo
  extends LeafInfo<Type, Class>, RuntimeNonElement
{
  public abstract <V> Transducer<V> getTransducer();
  
  public abstract Class getClazz();
  
  public abstract QName[] getTypeNames();
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\runtime\RuntimeLeafInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */