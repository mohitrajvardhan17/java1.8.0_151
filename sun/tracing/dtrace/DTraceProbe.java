package sun.tracing.dtrace;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import sun.tracing.ProbeSkeleton;

class DTraceProbe
  extends ProbeSkeleton
{
  private Object proxy;
  private Method declared_method;
  private Method implementing_method;
  
  DTraceProbe(Object paramObject, Method paramMethod)
  {
    super(paramMethod.getParameterTypes());
    proxy = paramObject;
    declared_method = paramMethod;
    try
    {
      implementing_method = paramObject.getClass().getMethod(paramMethod.getName(), paramMethod.getParameterTypes());
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      throw new RuntimeException("Internal error, wrong proxy class");
    }
  }
  
  public boolean isEnabled()
  {
    return JVM.isEnabled(implementing_method);
  }
  
  public void uncheckedTrigger(Object[] paramArrayOfObject)
  {
    try
    {
      implementing_method.invoke(proxy, paramArrayOfObject);
    }
    catch (IllegalAccessException localIllegalAccessException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    catch (InvocationTargetException localInvocationTargetException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
  }
  
  String getProbeName()
  {
    return DTraceProvider.getProbeName(declared_method);
  }
  
  String getFunctionName()
  {
    return DTraceProvider.getFunctionName(declared_method);
  }
  
  Method getMethod()
  {
    return implementing_method;
  }
  
  Class<?>[] getParameterTypes()
  {
    return parameters;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\dtrace\DTraceProbe.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */