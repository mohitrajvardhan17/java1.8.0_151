package com.sun.xml.internal.bind;

import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.FieldReflection;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.GetterOnlyReflection;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.GetterSetterReflection;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.ReadOnlyFieldReflection;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.SetterOnlyReflection;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AccessorFactoryImpl
  implements InternalAccessorFactory
{
  private static AccessorFactoryImpl instance = new AccessorFactoryImpl();
  
  private AccessorFactoryImpl() {}
  
  public static AccessorFactoryImpl getInstance()
  {
    return instance;
  }
  
  public Accessor createFieldAccessor(Class paramClass, Field paramField, boolean paramBoolean)
  {
    return paramBoolean ? new Accessor.ReadOnlyFieldReflection(paramField) : new Accessor.FieldReflection(paramField);
  }
  
  public Accessor createFieldAccessor(Class paramClass, Field paramField, boolean paramBoolean1, boolean paramBoolean2)
  {
    return paramBoolean1 ? new Accessor.ReadOnlyFieldReflection(paramField, paramBoolean2) : new Accessor.FieldReflection(paramField, paramBoolean2);
  }
  
  public Accessor createPropertyAccessor(Class paramClass, Method paramMethod1, Method paramMethod2)
  {
    if (paramMethod1 == null) {
      return new Accessor.SetterOnlyReflection(paramMethod2);
    }
    if (paramMethod2 == null) {
      return new Accessor.GetterOnlyReflection(paramMethod1);
    }
    return new Accessor.GetterSetterReflection(paramMethod1, paramMethod2);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\AccessorFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */