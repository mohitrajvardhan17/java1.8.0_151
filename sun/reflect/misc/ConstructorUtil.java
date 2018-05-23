package sun.reflect.misc;

import java.lang.reflect.Constructor;

public final class ConstructorUtil
{
  private ConstructorUtil() {}
  
  public static Constructor<?> getConstructor(Class<?> paramClass, Class<?>[] paramArrayOfClass)
    throws NoSuchMethodException
  {
    ReflectUtil.checkPackageAccess(paramClass);
    return paramClass.getConstructor(paramArrayOfClass);
  }
  
  public static Constructor<?>[] getConstructors(Class<?> paramClass)
  {
    ReflectUtil.checkPackageAccess(paramClass);
    return paramClass.getConstructors();
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\misc\ConstructorUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */