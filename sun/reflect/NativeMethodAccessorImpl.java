package sun.reflect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import sun.reflect.misc.ReflectUtil;

class NativeMethodAccessorImpl
  extends MethodAccessorImpl
{
  private final Method method;
  private DelegatingMethodAccessorImpl parent;
  private int numInvocations;
  
  NativeMethodAccessorImpl(Method paramMethod)
  {
    method = paramMethod;
  }
  
  public Object invoke(Object paramObject, Object[] paramArrayOfObject)
    throws IllegalArgumentException, InvocationTargetException
  {
    if ((++numInvocations > ReflectionFactory.inflationThreshold()) && (!ReflectUtil.isVMAnonymousClass(method.getDeclaringClass())))
    {
      MethodAccessorImpl localMethodAccessorImpl = (MethodAccessorImpl)new MethodAccessorGenerator().generateMethod(method.getDeclaringClass(), method.getName(), method.getParameterTypes(), method.getReturnType(), method.getExceptionTypes(), method.getModifiers());
      parent.setDelegate(localMethodAccessorImpl);
    }
    return invoke0(method, paramObject, paramArrayOfObject);
  }
  
  void setParent(DelegatingMethodAccessorImpl paramDelegatingMethodAccessorImpl)
  {
    parent = paramDelegatingMethodAccessorImpl;
  }
  
  private static native Object invoke0(Method paramMethod, Object paramObject, Object[] paramArrayOfObject);
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\reflect\NativeMethodAccessorImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */