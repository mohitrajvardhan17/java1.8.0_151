package com.sun.xml.internal.bind.v2.model.impl;

import com.sun.xml.internal.bind.v2.model.runtime.RuntimeElement;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeReferencePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Set;

class RuntimeReferencePropertyInfoImpl
  extends ReferencePropertyInfoImpl<Type, Class, Field, Method>
  implements RuntimeReferencePropertyInfo
{
  private final Accessor acc;
  
  public RuntimeReferencePropertyInfoImpl(RuntimeClassInfoImpl paramRuntimeClassInfoImpl, PropertySeed<Type, Class, Field, Method> paramPropertySeed)
  {
    super(paramRuntimeClassInfoImpl, paramPropertySeed);
    Accessor localAccessor = ((RuntimeClassInfoImpl.RuntimePropertySeed)paramPropertySeed).getAccessor();
    if ((getAdapter() != null) && (!isCollection())) {
      localAccessor = localAccessor.adapt(getAdapter());
    }
    acc = localAccessor;
  }
  
  public Set<? extends RuntimeElement> getElements()
  {
    return super.getElements();
  }
  
  public Set<? extends RuntimeElement> ref()
  {
    return super.ref();
  }
  
  public Accessor getAccessor()
  {
    return acc;
  }
  
  public boolean elementOnlyContent()
  {
    return !isMixed();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\impl\RuntimeReferencePropertyInfoImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */