package java.rmi.server;

import java.io.InvalidObjectException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.rmi.UnexpectedException;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Map;
import java.util.WeakHashMap;
import sun.rmi.server.Util;
import sun.rmi.server.WeakClassHashMap;

public class RemoteObjectInvocationHandler
  extends RemoteObject
  implements InvocationHandler
{
  private static final long serialVersionUID = 2L;
  private static final boolean allowFinalizeInvocation;
  private static final MethodToHash_Maps methodToHash_Maps = new MethodToHash_Maps();
  
  public RemoteObjectInvocationHandler(RemoteRef paramRemoteRef)
  {
    super(paramRemoteRef);
    if (paramRemoteRef == null) {
      throw new NullPointerException();
    }
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Throwable
  {
    if (!Proxy.isProxyClass(paramObject.getClass())) {
      throw new IllegalArgumentException("not a proxy");
    }
    if (Proxy.getInvocationHandler(paramObject) != this) {
      throw new IllegalArgumentException("handler mismatch");
    }
    if (paramMethod.getDeclaringClass() == Object.class) {
      return invokeObjectMethod(paramObject, paramMethod, paramArrayOfObject);
    }
    if (("finalize".equals(paramMethod.getName())) && (paramMethod.getParameterCount() == 0) && (!allowFinalizeInvocation)) {
      return null;
    }
    return invokeRemoteMethod(paramObject, paramMethod, paramArrayOfObject);
  }
  
  private Object invokeObjectMethod(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
  {
    String str = paramMethod.getName();
    if (str.equals("hashCode")) {
      return Integer.valueOf(hashCode());
    }
    if (str.equals("equals"))
    {
      Object localObject = paramArrayOfObject[0];
      InvocationHandler localInvocationHandler;
      return Boolean.valueOf((paramObject == localObject) || ((localObject != null) && (Proxy.isProxyClass(localObject.getClass())) && (((localInvocationHandler = Proxy.getInvocationHandler(localObject)) instanceof RemoteObjectInvocationHandler)) && (equals(localInvocationHandler))));
    }
    if (str.equals("toString")) {
      return proxyToString(paramObject);
    }
    throw new IllegalArgumentException("unexpected Object method: " + paramMethod);
  }
  
  private Object invokeRemoteMethod(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
    throws Exception
  {
    try
    {
      if (!(paramObject instanceof Remote)) {
        throw new IllegalArgumentException("proxy not Remote instance");
      }
      return ref.invoke((Remote)paramObject, paramMethod, paramArrayOfObject, getMethodHash(paramMethod));
    }
    catch (Exception localException)
    {
      UnexpectedException localUnexpectedException;
      if (!(localException instanceof RuntimeException))
      {
        Class localClass1 = paramObject.getClass();
        try
        {
          paramMethod = localClass1.getMethod(paramMethod.getName(), paramMethod.getParameterTypes());
        }
        catch (NoSuchMethodException localNoSuchMethodException)
        {
          throw ((IllegalArgumentException)new IllegalArgumentException().initCause(localNoSuchMethodException));
        }
        Class localClass2 = localException.getClass();
        for (Class localClass3 : paramMethod.getExceptionTypes()) {
          if (localClass3.isAssignableFrom(localClass2)) {
            throw localException;
          }
        }
        localUnexpectedException = new UnexpectedException("unexpected exception", localException);
      }
      throw localUnexpectedException;
    }
  }
  
  private String proxyToString(Object paramObject)
  {
    Class[] arrayOfClass = paramObject.getClass().getInterfaces();
    if (arrayOfClass.length == 0) {
      return "Proxy[" + this + "]";
    }
    String str = arrayOfClass[0].getName();
    if ((str.equals("java.rmi.Remote")) && (arrayOfClass.length > 1)) {
      str = arrayOfClass[1].getName();
    }
    int i = str.lastIndexOf('.');
    if (i >= 0) {
      str = str.substring(i + 1);
    }
    return "Proxy[" + str + "," + this + "]";
  }
  
  private void readObjectNoData()
    throws InvalidObjectException
  {
    throw new InvalidObjectException("no data in stream; class: " + getClass().getName());
  }
  
  private static long getMethodHash(Method paramMethod)
  {
    return ((Long)((Map)methodToHash_Maps.get(paramMethod.getDeclaringClass())).get(paramMethod)).longValue();
  }
  
  static
  {
    String str1 = "sun.rmi.server.invocationhandler.allowFinalizeInvocation";
    String str2 = (String)AccessController.doPrivileged(new PrivilegedAction()
    {
      public String run()
      {
        return System.getProperty(val$propName);
      }
    });
    if ("".equals(str2)) {
      allowFinalizeInvocation = true;
    } else {
      allowFinalizeInvocation = Boolean.parseBoolean(str2);
    }
  }
  
  private static class MethodToHash_Maps
    extends WeakClassHashMap<Map<Method, Long>>
  {
    MethodToHash_Maps() {}
    
    protected Map<Method, Long> computeValue(Class<?> paramClass)
    {
      new WeakHashMap()
      {
        public synchronized Long get(Object paramAnonymousObject)
        {
          Long localLong = (Long)super.get(paramAnonymousObject);
          if (localLong == null)
          {
            Method localMethod = (Method)paramAnonymousObject;
            localLong = Long.valueOf(Util.computeMethodHash(localMethod));
            put(localMethod, localLong);
          }
          return localLong;
        }
      };
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\rmi\server\RemoteObjectInvocationHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */