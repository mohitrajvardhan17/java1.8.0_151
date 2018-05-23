package sun.reflect.misc;

import java.lang.reflect.Field;

public final class FieldUtil
{
  private FieldUtil() {}
  
  public static Field getField(Class<?> paramClass, String paramString)
    throws NoSuchFieldException
  {
    ReflectUtil.checkPackageAccess(paramClass);
    return paramClass.getField(paramString);
  }
  
  public static Field[] getFields(Class<?> paramClass)
  {
    ReflectUtil.checkPackageAccess(paramClass);
    return paramClass.getFields();
  }
  
  public static Field[] getDeclaredFields(Class<?> paramClass)
  {
    ReflectUtil.checkPackageAccess(paramClass);
    return paramClass.getDeclaredFields();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\misc\FieldUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */