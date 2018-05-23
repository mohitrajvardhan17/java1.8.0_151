package com.sun.xml.internal.bind.v2.model.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public final class RuntimeInlineAnnotationReader
  extends AbstractInlineAnnotationReaderImpl<Type, Class, Field, Method>
  implements RuntimeAnnotationReader
{
  private final Map<Class<? extends Annotation>, Map<Package, Annotation>> packageCache = new HashMap();
  
  public RuntimeInlineAnnotationReader() {}
  
  public <A extends Annotation> A getFieldAnnotation(Class<A> paramClass, Field paramField, Locatable paramLocatable)
  {
    return LocatableAnnotation.create(paramField.getAnnotation(paramClass), paramLocatable);
  }
  
  public boolean hasFieldAnnotation(Class<? extends Annotation> paramClass, Field paramField)
  {
    return paramField.isAnnotationPresent(paramClass);
  }
  
  public boolean hasClassAnnotation(Class paramClass, Class<? extends Annotation> paramClass1)
  {
    return paramClass.isAnnotationPresent(paramClass1);
  }
  
  public Annotation[] getAllFieldAnnotations(Field paramField, Locatable paramLocatable)
  {
    Annotation[] arrayOfAnnotation = paramField.getAnnotations();
    for (int i = 0; i < arrayOfAnnotation.length; i++) {
      arrayOfAnnotation[i] = LocatableAnnotation.create(arrayOfAnnotation[i], paramLocatable);
    }
    return arrayOfAnnotation;
  }
  
  public <A extends Annotation> A getMethodAnnotation(Class<A> paramClass, Method paramMethod, Locatable paramLocatable)
  {
    return LocatableAnnotation.create(paramMethod.getAnnotation(paramClass), paramLocatable);
  }
  
  public boolean hasMethodAnnotation(Class<? extends Annotation> paramClass, Method paramMethod)
  {
    return paramMethod.isAnnotationPresent(paramClass);
  }
  
  public Annotation[] getAllMethodAnnotations(Method paramMethod, Locatable paramLocatable)
  {
    Annotation[] arrayOfAnnotation = paramMethod.getAnnotations();
    for (int i = 0; i < arrayOfAnnotation.length; i++) {
      arrayOfAnnotation[i] = LocatableAnnotation.create(arrayOfAnnotation[i], paramLocatable);
    }
    return arrayOfAnnotation;
  }
  
  public <A extends Annotation> A getMethodParameterAnnotation(Class<A> paramClass, Method paramMethod, int paramInt, Locatable paramLocatable)
  {
    Annotation[] arrayOfAnnotation1 = paramMethod.getParameterAnnotations()[paramInt];
    for (Annotation localAnnotation : arrayOfAnnotation1) {
      if (localAnnotation.annotationType() == paramClass) {
        return LocatableAnnotation.create(localAnnotation, paramLocatable);
      }
    }
    return null;
  }
  
  public <A extends Annotation> A getClassAnnotation(Class<A> paramClass, Class paramClass1, Locatable paramLocatable)
  {
    return LocatableAnnotation.create(paramClass1.getAnnotation(paramClass), paramLocatable);
  }
  
  public <A extends Annotation> A getPackageAnnotation(Class<A> paramClass, Class paramClass1, Locatable paramLocatable)
  {
    Package localPackage = paramClass1.getPackage();
    if (localPackage == null) {
      return null;
    }
    Object localObject = (Map)packageCache.get(paramClass);
    if (localObject == null)
    {
      localObject = new HashMap();
      packageCache.put(paramClass, localObject);
    }
    if (((Map)localObject).containsKey(localPackage)) {
      return (Annotation)((Map)localObject).get(localPackage);
    }
    Annotation localAnnotation = LocatableAnnotation.create(localPackage.getAnnotation(paramClass), paramLocatable);
    ((Map)localObject).put(localPackage, localAnnotation);
    return localAnnotation;
  }
  
  public Class getClassValue(Annotation paramAnnotation, String paramString)
  {
    try
    {
      return (Class)paramAnnotation.annotationType().getMethod(paramString, new Class[0]).invoke(paramAnnotation, new Object[0]);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new IllegalAccessError(localIllegalAccessException.getMessage());
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new InternalError(Messages.CLASS_NOT_FOUND.format(new Object[] { paramAnnotation.annotationType(), localInvocationTargetException.getMessage() }));
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new NoSuchMethodError(localNoSuchMethodException.getMessage());
    }
  }
  
  public Class[] getClassArrayValue(Annotation paramAnnotation, String paramString)
  {
    try
    {
      return (Class[])paramAnnotation.annotationType().getMethod(paramString, new Class[0]).invoke(paramAnnotation, new Object[0]);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new IllegalAccessError(localIllegalAccessException.getMessage());
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      throw new InternalError(localInvocationTargetException.getMessage());
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new NoSuchMethodError(localNoSuchMethodException.getMessage());
    }
  }
  
  protected String fullName(Method paramMethod)
  {
    return paramMethod.getDeclaringClass().getName() + '#' + paramMethod.getName();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\bind\v2\model\annotation\RuntimeInlineAnnotationReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */