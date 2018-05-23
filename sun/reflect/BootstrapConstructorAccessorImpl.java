package sun.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import sun.misc.Unsafe;

class BootstrapConstructorAccessorImpl
  extends ConstructorAccessorImpl
{
  private final Constructor<?> constructor;
  
  BootstrapConstructorAccessorImpl(Constructor<?> paramConstructor)
  {
    constructor = paramConstructor;
  }
  
  public Object newInstance(Object[] paramArrayOfObject)
    throws IllegalArgumentException, InvocationTargetException
  {
    try
    {
      return UnsafeFieldAccessorImpl.unsafe.allocateInstance(constructor.getDeclaringClass());
    }
    catch (InstantiationException localInstantiationException)
    {
      throw new InvocationTargetException(localInstantiationException);
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\BootstrapConstructorAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */