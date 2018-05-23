package com.sun.jmx.mbeanserver;

import java.io.InvalidObjectException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import javax.management.Descriptor;
import javax.management.MBeanException;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import sun.reflect.misc.MethodUtil;

final class ConvertingMethod
{
  private static final String[] noStrings = new String[0];
  private final Method method;
  private final MXBeanMapping returnMapping;
  private final MXBeanMapping[] paramMappings;
  private final boolean paramConversionIsIdentity;
  
  static ConvertingMethod from(Method paramMethod)
  {
    try
    {
      return new ConvertingMethod(paramMethod);
    }
    catch (OpenDataException localOpenDataException)
    {
      String str = "Method " + paramMethod.getDeclaringClass().getName() + "." + paramMethod.getName() + " has parameter or return type that cannot be translated into an open type";
      throw new IllegalArgumentException(str, localOpenDataException);
    }
  }
  
  Method getMethod()
  {
    return method;
  }
  
  Descriptor getDescriptor()
  {
    return Introspector.descriptorForElement(method);
  }
  
  Type getGenericReturnType()
  {
    return method.getGenericReturnType();
  }
  
  Type[] getGenericParameterTypes()
  {
    return method.getGenericParameterTypes();
  }
  
  String getName()
  {
    return method.getName();
  }
  
  OpenType<?> getOpenReturnType()
  {
    return returnMapping.getOpenType();
  }
  
  OpenType<?>[] getOpenParameterTypes()
  {
    OpenType[] arrayOfOpenType = new OpenType[paramMappings.length];
    for (int i = 0; i < paramMappings.length; i++) {
      arrayOfOpenType[i] = paramMappings[i].getOpenType();
    }
    return arrayOfOpenType;
  }
  
  void checkCallFromOpen()
  {
    try
    {
      for (MXBeanMapping localMXBeanMapping : paramMappings) {
        localMXBeanMapping.checkReconstructible();
      }
    }
    catch (InvalidObjectException localInvalidObjectException)
    {
      throw new IllegalArgumentException(localInvalidObjectException);
    }
  }
  
  void checkCallToOpen()
  {
    try
    {
      returnMapping.checkReconstructible();
    }
    catch (InvalidObjectException localInvalidObjectException)
    {
      throw new IllegalArgumentException(localInvalidObjectException);
    }
  }
  
  String[] getOpenSignature()
  {
    if (paramMappings.length == 0) {
      return noStrings;
    }
    String[] arrayOfString = new String[paramMappings.length];
    for (int i = 0; i < paramMappings.length; i++) {
      arrayOfString[i] = paramMappings[i].getOpenClass().getName();
    }
    return arrayOfString;
  }
  
  final Object toOpenReturnValue(MXBeanLookup paramMXBeanLookup, Object paramObject)
    throws OpenDataException
  {
    return returnMapping.toOpenValue(paramObject);
  }
  
  final Object fromOpenReturnValue(MXBeanLookup paramMXBeanLookup, Object paramObject)
    throws InvalidObjectException
  {
    return returnMapping.fromOpenValue(paramObject);
  }
  
  final Object[] toOpenParameters(MXBeanLookup paramMXBeanLookup, Object[] paramArrayOfObject)
    throws OpenDataException
  {
    if ((paramConversionIsIdentity) || (paramArrayOfObject == null)) {
      return paramArrayOfObject;
    }
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      arrayOfObject[i] = paramMappings[i].toOpenValue(paramArrayOfObject[i]);
    }
    return arrayOfObject;
  }
  
  final Object[] fromOpenParameters(Object[] paramArrayOfObject)
    throws InvalidObjectException
  {
    if ((paramConversionIsIdentity) || (paramArrayOfObject == null)) {
      return paramArrayOfObject;
    }
    Object[] arrayOfObject = new Object[paramArrayOfObject.length];
    for (int i = 0; i < paramArrayOfObject.length; i++) {
      arrayOfObject[i] = paramMappings[i].fromOpenValue(paramArrayOfObject[i]);
    }
    return arrayOfObject;
  }
  
  final Object toOpenParameter(MXBeanLookup paramMXBeanLookup, Object paramObject, int paramInt)
    throws OpenDataException
  {
    return paramMappings[paramInt].toOpenValue(paramObject);
  }
  
  final Object fromOpenParameter(MXBeanLookup paramMXBeanLookup, Object paramObject, int paramInt)
    throws InvalidObjectException
  {
    return paramMappings[paramInt].fromOpenValue(paramObject);
  }
  
  Object invokeWithOpenReturn(MXBeanLookup paramMXBeanLookup, Object paramObject, Object[] paramArrayOfObject)
    throws MBeanException, IllegalAccessException, InvocationTargetException
  {
    MXBeanLookup localMXBeanLookup = MXBeanLookup.getLookup();
    try
    {
      MXBeanLookup.setLookup(paramMXBeanLookup);
      Object localObject1 = invokeWithOpenReturn(paramObject, paramArrayOfObject);
      return localObject1;
    }
    finally
    {
      MXBeanLookup.setLookup(localMXBeanLookup);
    }
  }
  
  private Object invokeWithOpenReturn(Object paramObject, Object[] paramArrayOfObject)
    throws MBeanException, IllegalAccessException, InvocationTargetException
  {
    Object[] arrayOfObject;
    try
    {
      arrayOfObject = fromOpenParameters(paramArrayOfObject);
    }
    catch (InvalidObjectException localInvalidObjectException)
    {
      String str1 = methodName() + ": cannot convert parameters from open values: " + localInvalidObjectException;
      throw new MBeanException(localInvalidObjectException, str1);
    }
    Object localObject = MethodUtil.invoke(method, paramObject, arrayOfObject);
    try
    {
      return returnMapping.toOpenValue(localObject);
    }
    catch (OpenDataException localOpenDataException)
    {
      String str2 = methodName() + ": cannot convert return value to open value: " + localOpenDataException;
      throw new MBeanException(localOpenDataException, str2);
    }
  }
  
  private String methodName()
  {
    return method.getDeclaringClass() + "." + method.getName();
  }
  
  private ConvertingMethod(Method paramMethod)
    throws OpenDataException
  {
    method = paramMethod;
    MXBeanMappingFactory localMXBeanMappingFactory = MXBeanMappingFactory.DEFAULT;
    returnMapping = localMXBeanMappingFactory.mappingForType(paramMethod.getGenericReturnType(), localMXBeanMappingFactory);
    Type[] arrayOfType = paramMethod.getGenericParameterTypes();
    paramMappings = new MXBeanMapping[arrayOfType.length];
    boolean bool = true;
    for (int i = 0; i < arrayOfType.length; i++)
    {
      paramMappings[i] = localMXBeanMappingFactory.mappingForType(arrayOfType[i], localMXBeanMappingFactory);
      bool &= DefaultMXBeanMappingFactory.isIdentity(paramMappings[i]);
    }
    paramConversionIsIdentity = bool;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\jmx\mbeanserver\ConvertingMethod.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */