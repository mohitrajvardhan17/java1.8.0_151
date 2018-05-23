package sun.reflect;

import java.lang.reflect.InvocationTargetException;

abstract class MethodAccessorImpl
  extends MagicAccessorImpl
  implements MethodAccessor
{
  MethodAccessorImpl() {}
  
  public abstract Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws IllegalArgumentException, InvocationTargetException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\MethodAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */