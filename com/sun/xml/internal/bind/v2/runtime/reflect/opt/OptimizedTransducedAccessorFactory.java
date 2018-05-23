package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import com.sun.xml.internal.bind.v2.model.core.TypeInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimeClassInfo;
import com.sun.xml.internal.bind.v2.model.runtime.RuntimePropertyInfo;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.FieldReflection;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor.GetterSetterReflection;
import com.sun.xml.internal.bind.v2.runtime.reflect.TransducedAccessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class OptimizedTransducedAccessorFactory
{
  private static final Logger logger = ;
  private static final String fieldTemplateName;
  private static final String methodTemplateName;
  private static final Map<Class, String> suffixMap;
  
  private OptimizedTransducedAccessorFactory() {}
  
  public static final TransducedAccessor get(RuntimePropertyInfo paramRuntimePropertyInfo)
  {
    Accessor localAccessor = paramRuntimePropertyInfo.getAccessor();
    Class localClass1 = null;
    TypeInfo localTypeInfo = paramRuntimePropertyInfo.parent();
    if (!(localTypeInfo instanceof RuntimeClassInfo)) {
      return null;
    }
    Class localClass2 = (Class)((RuntimeClassInfo)localTypeInfo).getClazz();
    String str = ClassTailor.toVMClassName(localClass2) + "_JaxbXducedAccessor_" + paramRuntimePropertyInfo.getName();
    Object localObject1;
    Object localObject2;
    if ((localAccessor instanceof Accessor.FieldReflection))
    {
      localObject1 = (Accessor.FieldReflection)localAccessor;
      localObject2 = f;
      int i = ((Field)localObject2).getModifiers();
      if ((Modifier.isPrivate(i)) || (Modifier.isFinal(i))) {
        return null;
      }
      Class localClass3 = ((Field)localObject2).getType();
      if (localClass3.isPrimitive()) {
        localClass1 = AccessorInjector.prepare(localClass2, fieldTemplateName + (String)suffixMap.get(localClass3), str, new String[] { ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(localClass2), "f_" + localClass3.getName(), ((Field)localObject2).getName() });
      }
    }
    if (localAccessor.getClass() == Accessor.GetterSetterReflection.class)
    {
      localObject1 = (Accessor.GetterSetterReflection)localAccessor;
      if ((getter == null) || (setter == null)) {
        return null;
      }
      localObject2 = getter.getReturnType();
      if ((Modifier.isPrivate(getter.getModifiers())) || (Modifier.isPrivate(setter.getModifiers()))) {
        return null;
      }
      if (((Class)localObject2).isPrimitive()) {
        localClass1 = AccessorInjector.prepare(localClass2, methodTemplateName + (String)suffixMap.get(localObject2), str, new String[] { ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(localClass2), "get_" + ((Class)localObject2).getName(), getter.getName(), "set_" + ((Class)localObject2).getName(), setter.getName() });
      }
    }
    if (localClass1 == null) {
      return null;
    }
    logger.log(Level.FINE, "Using optimized TransducedAccessor for " + paramRuntimePropertyInfo.displayName());
    try
    {
      return (TransducedAccessor)localClass1.newInstance();
    }
    catch (InstantiationException localInstantiationException)
    {
      logger.log(Level.INFO, "failed to load an optimized TransducedAccessor", localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      logger.log(Level.INFO, "failed to load an optimized TransducedAccessor", localIllegalAccessException);
    }
    catch (SecurityException localSecurityException)
    {
      logger.log(Level.INFO, "failed to load an optimized TransducedAccessor", localSecurityException);
    }
    return null;
  }
  
  static
  {
    String str = TransducedAccessor_field_Byte.class.getName();
    fieldTemplateName = str.substring(0, str.length() - "Byte".length()).replace('.', '/');
    str = TransducedAccessor_method_Byte.class.getName();
    methodTemplateName = str.substring(0, str.length() - "Byte".length()).replace('.', '/');
    suffixMap = new HashMap();
    suffixMap.put(Byte.TYPE, "Byte");
    suffixMap.put(Short.TYPE, "Short");
    suffixMap.put(Integer.TYPE, "Integer");
    suffixMap.put(Long.TYPE, "Long");
    suffixMap.put(Boolean.TYPE, "Boolean");
    suffixMap.put(Float.TYPE, "Float");
    suffixMap.put(Double.TYPE, "Double");
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\OptimizedTransducedAccessorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */