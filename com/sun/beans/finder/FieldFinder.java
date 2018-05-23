package com.sun.beans.finder;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import sun.reflect.misc.ReflectUtil;

public final class FieldFinder
{
  public static Field findField(Class<?> paramClass, String paramString)
    throws NoSuchFieldException
  {
    if (paramString == null) {
      throw new IllegalArgumentException("Field name is not set");
    }
    Field localField = paramClass.getField(paramString);
    if (!Modifier.isPublic(localField.getModifiers())) {
      throw new NoSuchFieldException("Field '" + paramString + "' is not public");
    }
    paramClass = localField.getDeclaringClass();
    if ((!Modifier.isPublic(paramClass.getModifiers())) || (!ReflectUtil.isPackageAccessible(paramClass))) {
      throw new NoSuchFieldException("Field '" + paramString + "' is not accessible");
    }
    return localField;
  }
  
  public static Field findInstanceField(Class<?> paramClass, String paramString)
    throws NoSuchFieldException
  {
    Field localField = findField(paramClass, paramString);
    if (Modifier.isStatic(localField.getModifiers())) {
      throw new NoSuchFieldException("Field '" + paramString + "' is static");
    }
    return localField;
  }
  
  public static Field findStaticField(Class<?> paramClass, String paramString)
    throws NoSuchFieldException
  {
    Field localField = findField(paramClass, paramString);
    if (!Modifier.isStatic(localField.getModifiers())) {
      throw new NoSuchFieldException("Field '" + paramString + "' is not static");
    }
    return localField;
  }
  
  private FieldFinder() {}
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\beans\finder\FieldFinder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */