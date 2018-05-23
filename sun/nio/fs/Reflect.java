package sun.nio.fs;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;

class Reflect
{
  private Reflect() {}
  
  private static void setAccessible(AccessibleObject paramAccessibleObject)
  {
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        val$ao.setAccessible(true);
        return null;
      }
    });
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
    catch (ClassNotFoundException localClassNotFoundException)
    {
      throw new AssertionError(localClassNotFoundException);
    }
    catch (NoSuchFieldException localNoSuchFieldException)
    {
      throw new AssertionError(localNoSuchFieldException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\nio\fs\Reflect.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */