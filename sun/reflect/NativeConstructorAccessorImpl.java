package sun.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import sun.reflect.misc.ReflectUtil;

class NativeConstructorAccessorImpl
  extends ConstructorAccessorImpl
{
  private final Constructor<?> c;
  private DelegatingConstructorAccessorImpl parent;
  private int numInvocations;
  
  NativeConstructorAccessorImpl(Constructor<?> paramConstructor)
  {
    c = paramConstructor;
  }
  
  public Object newInstance(Object[] paramArrayOfObject)
    throws InstantiationException, IllegalArgumentException, InvocationTargetException
  {
    if ((++numInvocations > ReflectionFactory.inflationThreshold()) && (!ReflectUtil.isVMAnonymousClass(c.getDeclaringClass())))
    {
      ConstructorAccessorImpl localConstructorAccessorImpl = (ConstructorAccessorImpl)new MethodAccessorGenerator().generateConstructor(c.getDeclaringClass(), c.getParameterTypes(), c.getExceptionTypes(), c.getModifiers());
      parent.setDelegate(localConstructorAccessorImpl);
    }
    return newInstance0(c, paramArrayOfObject);
  }
  
  void setParent(DelegatingConstructorAccessorImpl paramDelegatingConstructorAccessorImpl)
  {
    parent = paramDelegatingConstructorAccessorImpl;
  }
  
  private static native Object newInstance0(Constructor<?> paramConstructor, Object[] paramArrayOfObject)
    throws InstantiationException, IllegalArgumentException, InvocationTargetException;
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\NativeConstructorAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */