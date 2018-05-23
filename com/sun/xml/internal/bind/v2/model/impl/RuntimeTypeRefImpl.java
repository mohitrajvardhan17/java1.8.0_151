package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeNonElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeTypeRef;
import com.sun.xml.internal.bind.v2.runtime.Transducer;
import java.lang.reflect.Type;
import javax.xml.namespace.QName;

final class RuntimeTypeRefImpl
  extends TypeRefImpl<Type, Class>
  implements RuntimeTypeRef
{
  public RuntimeTypeRefImpl(RuntimeElementPropertyInfoImpl paramRuntimeElementPropertyInfoImpl, QName paramQName, Type paramType, boolean paramBoolean, String paramString)
  {
    super(paramRuntimeElementPropertyInfoImpl, paramQName, paramType, paramBoolean, paramString);
  }
  
  public RuntimeNonElement getTarget()
  {
    return (RuntimeNonElement)super.getTarget();
  }
  
  public Transducer getTransducer()
  {
    return RuntimeModelBuilder.createTransducer(this);
  }
  
  public RuntimePropertyInfo getSource()
  {
    return (RuntimePropertyInfo)owner;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeTypeRefImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */