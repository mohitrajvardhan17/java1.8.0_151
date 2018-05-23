package sun.reflect.misc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;

class Trampoline
{
  Trampoline() {}
  
  private static void ensureInvocableMethod(Method paramMethod)
    throws InvocationTargetException
  {
    Class localClass = paramMethod.getDeclaringClass();
    if ((localClass.equals(AccessController.class)) || (localClass.equals(Method.class)) || (localClass.getName().startsWith("java.lang.invoke."))) {
      throw new InvocationTargetException(new UnsupportedOperationException("invocation not supported"));
    }
  }
  
  private static Object invoke(Method paramMethod, Object paramObject, Object[] paramArrayOfObject)
    throws InvocationTargetException, IllegalAccessException
  {
    ensureInvocableMethod(paramMethod);
    return paramMethod.invoke(paramObject, paramArrayOfObject);
  }
  
  static
  {
    if (Trampoline.class.getClassLoader() == null) {
      throw new Error("Trampoline must not be defined by the bootstrap classloader");
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\misc\Trampoline.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */