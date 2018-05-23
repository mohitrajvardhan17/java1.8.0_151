package com.sun.xml.internal.bind.v2.runtime.reflect.opt;

import com.sun.xml.internal.bind.Util;
import com.sun.xml.internal.bind.v2.bytecode.ClassTailor;
import com.sun.xml.internal.bind.v2.runtime.RuntimeUtil;
import com.sun.xml.internal.bind.v2.runtime.reflect.Accessor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class OptimizedAccessorFactory
{
  private static final Logger logger = ;
  private static final String fieldTemplateName;
  private static final String methodTemplateName;
  
  private OptimizedAccessorFactory() {}
  
  public static final <B, V> Accessor<B, V> get(Method paramMethod1, Method paramMethod2)
  {
    if (paramMethod1.getParameterTypes().length != 0) {
      return null;
    }
    Class[] arrayOfClass = paramMethod2.getParameterTypes();
    if (arrayOfClass.length != 1) {
      return null;
    }
    if (arrayOfClass[0] != paramMethod1.getReturnType()) {
      return null;
    }
    if (paramMethod2.getReturnType() != Void.TYPE) {
      return null;
    }
    if (paramMethod1.getDeclaringClass() != paramMethod2.getDeclaringClass()) {
      return null;
    }
    if ((Modifier.isPrivate(paramMethod1.getModifiers())) || (Modifier.isPrivate(paramMethod2.getModifiers()))) {
      return null;
    }
    Class localClass1 = arrayOfClass[0];
    String str1 = localClass1.getName().replace('.', '_');
    if (localClass1.isArray())
    {
      str1 = "AOf_";
      str2 = localClass1.getComponentType().getName().replace('.', '_');
      while (str2.startsWith("[L"))
      {
        str2 = str2.substring(2);
        str1 = str1 + "AOf_";
      }
      str1 = str1 + str2;
    }
    String str2 = ClassTailor.toVMClassName(paramMethod1.getDeclaringClass()) + "$JaxbAccessorM_" + paramMethod1.getName() + '_' + paramMethod2.getName() + '_' + str1;
    Class localClass2;
    if (localClass1.isPrimitive()) {
      localClass2 = AccessorInjector.prepare(paramMethod1.getDeclaringClass(), methodTemplateName + ((Class)RuntimeUtil.primitiveToBox.get(localClass1)).getSimpleName(), str2, new String[] { ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(paramMethod1.getDeclaringClass()), "get_" + localClass1.getName(), paramMethod1.getName(), "set_" + localClass1.getName(), paramMethod2.getName() });
    } else {
      localClass2 = AccessorInjector.prepare(paramMethod1.getDeclaringClass(), methodTemplateName + "Ref", str2, new String[] { ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(paramMethod1.getDeclaringClass()), ClassTailor.toVMClassName(Ref.class), ClassTailor.toVMClassName(localClass1), "()" + ClassTailor.toVMTypeName(Ref.class), "()" + ClassTailor.toVMTypeName(localClass1), '(' + ClassTailor.toVMTypeName(Ref.class) + ")V", '(' + ClassTailor.toVMTypeName(localClass1) + ")V", "get_ref", paramMethod1.getName(), "set_ref", paramMethod2.getName() });
    }
    if (localClass2 == null) {
      return null;
    }
    Accessor localAccessor = instanciate(localClass2);
    if ((localAccessor != null) && (logger.isLoggable(Level.FINE))) {
      logger.log(Level.FINE, "Using optimized Accessor for {0} and {1}", new Object[] { paramMethod1, paramMethod2 });
    }
    return localAccessor;
  }
  
  public static final <B, V> Accessor<B, V> get(Field paramField)
  {
    int i = paramField.getModifiers();
    if ((Modifier.isPrivate(i)) || (Modifier.isFinal(i))) {
      return null;
    }
    String str = ClassTailor.toVMClassName(paramField.getDeclaringClass()) + "$JaxbAccessorF_" + paramField.getName();
    Class localClass;
    if (paramField.getType().isPrimitive()) {
      localClass = AccessorInjector.prepare(paramField.getDeclaringClass(), fieldTemplateName + ((Class)RuntimeUtil.primitiveToBox.get(paramField.getType())).getSimpleName(), str, new String[] { ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(paramField.getDeclaringClass()), "f_" + paramField.getType().getName(), paramField.getName() });
    } else {
      localClass = AccessorInjector.prepare(paramField.getDeclaringClass(), fieldTemplateName + "Ref", str, new String[] { ClassTailor.toVMClassName(Bean.class), ClassTailor.toVMClassName(paramField.getDeclaringClass()), ClassTailor.toVMClassName(Ref.class), ClassTailor.toVMClassName(paramField.getType()), ClassTailor.toVMTypeName(Ref.class), ClassTailor.toVMTypeName(paramField.getType()), "f_ref", paramField.getName() });
    }
    if (localClass == null) {
      return null;
    }
    Accessor localAccessor = instanciate(localClass);
    if ((localAccessor != null) && (logger.isLoggable(Level.FINE))) {
      logger.log(Level.FINE, "Using optimized Accessor for {0}", paramField);
    }
    return localAccessor;
  }
  
  private static <B, V> Accessor<B, V> instanciate(Class paramClass)
  {
    try
    {
      return (Accessor)paramClass.newInstance();
    }
    catch (InstantiationException localInstantiationException)
    {
      logger.log(Level.INFO, "failed to load an optimized Accessor", localInstantiationException);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      logger.log(Level.INFO, "failed to load an optimized Accessor", localIllegalAccessException);
    }
    catch (SecurityException localSecurityException)
    {
      logger.log(Level.INFO, "failed to load an optimized Accessor", localSecurityException);
    }
    return null;
  }
  
  static
  {
    String str = FieldAccessor_Byte.class.getName();
    fieldTemplateName = str.substring(0, str.length() - "Byte".length()).replace('.', '/');
    str = MethodAccessor_Byte.class.getName();
    methodTemplateName = str.substring(0, str.length() - "Byte".length()).replace('.', '/');
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\runtime\reflect\opt\OptimizedAccessorFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */