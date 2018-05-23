package sun.nio.ch;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;

class Reflect
{
  private Reflect() {}
  
  private static void setAccessible(AccessibleObject paramAccessibleObject)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        val$ao.setAccessible(true);
        return null;
      }
    });
  }
  
  static Constructor<?> lookupConstructor(String paramString, Class<?>[] paramArrayOfClass)
  {
    try
    {
      Class localClass = Class.forName(paramString);
      Constructor localConstructor = localClass.getDeclaredConstructor(paramArrayOfClass);
      setAccessible(localConstructor);
      return localConstructor;
    }
    catch (ClassNotFoundException|NoSuchMethodException localClassNotFoundException)
    {
      throw new ReflectionError(localClassNotFoundException);
    }
  }
  
  static Object invoke(Constructor<?> paramConstructor, Object[] paramArrayOfObject)
  {
    try
    {
      return paramConstructor.newInstance(paramArrayOfObject);
    }
    catch (InstantiationException|IllegalAccessException|InvocationTargetException localInstantiationException)
    {
      throw new ReflectionError(localInstantiationException);
    }
  }
  
  static Method lookupMethod(String paramString1, String paramString2, Class... paramVarArgs)
  {
    try
    {
      Class localClass = Class.forName(paramString1);
      Method localMethod = localClass.getDeclaredMethod(paramString2, paramVarArgs);
      setAccessible(localMethod);
      return localMethod;
    }
    catch (ClassNotFoundException|NoSuchMethodException localClassNotFoundException)
    {
      throw new ReflectionError(localClassNotFoundException);
    }
  }
  
  static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
  {
    try
    {
      return paramMethod.invoke(paramObject, paramArrayOfObject);
    }
    catch (IllegalAccessException|InvocationTargetException localIllegalAccessException)
    {
      throw new ReflectionError(localIllegalAccessException);
    }
  }
  
  static Object invokeIO(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
    throws IOException
  {
    try
    {
      return paramMethod.invoke(paramObject, paramArrayOfObject);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ReflectionError(localIllegalAccessException);
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      if (IOException.class.isInstance(localInvocationTargetException.getCause())) {
        throw ((IOException)localInvocationTargetException.getCause());
      }
      throw new ReflectionError(localInvocationTargetException);
    }
  }
  
  static Field lookupField(String paramString1, String paramString2)
  {
    try
    {
      Class localClass = Class.forName(paramString1);
      Field localField = localClass.getDeclaredField(paramString2);
      setAccessible(localField);
      return localField;
    }
    catch (ClassNotFoundException|NoSuchFieldException localClassNotFoundException)
    {
      throw new ReflectionError(localClassNotFoundException);
    }
  }
  
  static Object get(Object paramObject, Field paramField)
  {
    try
    {
      return paramField.get(paramObject);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ReflectionError(localIllegalAccessException);
    }
  }
  
  static Object get(Field paramField)
  {
    return get(null, paramField);
  }
  
  static void set(Object paramObject1, Field paramField, Object paramObject2)
  {
    try
    {
      paramField.set(paramObject1, paramObject2);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ReflectionError(localIllegalAccessException);
    }
  }
  
  static void setInt(Object paramObject, Field paramField, int paramInt)
  {
    try
    {
      paramField.setInt(paramObject, paramInt);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ReflectionError(localIllegalAccessException);
    }
  }
  
  static void setBoolean(Object paramObject, Field paramField, boolean paramBoolean)
  {
    try
    {
      paramField.setBoolean(paramObject, paramBoolean);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      throw new ReflectionError(localIllegalAccessException);
    }
  }
  
  private static class ReflectionError
    extends Error
  {
    private static final long serialVersionUID = -8659519328078164097L;
    
    ReflectionError(Throwable paramThrowable)
    {
      super();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\ch\Reflect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */