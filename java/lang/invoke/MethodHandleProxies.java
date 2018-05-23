package java.lang.invoke;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import sun.invoke.WrapperInstance;
import sun.reflect.CallerSensitive;
import sun.reflect.Reflection;
import sun.reflect.misc.ReflectUtil;

public class MethodHandleProxies
{
  private MethodHandleProxies() {}
  
  @CallerSensitive
  public static <T> T asInterfaceInstance(final Class<T> paramClass, MethodHandle paramMethodHandle)
  {
    if ((!paramClass.isInterface()) || (!Modifier.isPublic(paramClass.getModifiers()))) {
      throw MethodHandleStatics.newIllegalArgumentException("not a public interface", paramClass.getName());
    }
    MethodHandle localMethodHandle1;
    if (System.getSecurityManager() != null)
    {
      localObject1 = Reflection.getCallerClass();
      localObject2 = localObject1 != null ? ((Class)localObject1).getClassLoader() : null;
      ReflectUtil.checkProxyPackageAccess((ClassLoader)localObject2, new Class[] { paramClass });
      localMethodHandle1 = localObject2 != null ? bindCaller(paramMethodHandle, (Class)localObject1) : paramMethodHandle;
    }
    else
    {
      localMethodHandle1 = paramMethodHandle;
    }
    Object localObject1 = paramClass.getClassLoader();
    if (localObject1 == null)
    {
      localObject2 = Thread.currentThread().getContextClassLoader();
      localObject1 = localObject2 != null ? localObject2 : ClassLoader.getSystemClassLoader();
    }
    final Object localObject2 = getSingleNameMethods(paramClass);
    if (localObject2 == null) {
      throw MethodHandleStatics.newIllegalArgumentException("not a single-method interface", paramClass.getName());
    }
    final MethodHandle[] arrayOfMethodHandle = new MethodHandle[localObject2.length];
    Object localObject3;
    Object localObject4;
    for (int i = 0; i < localObject2.length; i++)
    {
      localObject3 = localObject2[i];
      localObject4 = MethodType.methodType(((Method)localObject3).getReturnType(), ((Method)localObject3).getParameterTypes());
      MethodHandle localMethodHandle2 = localMethodHandle1.asType((MethodType)localObject4);
      localMethodHandle2 = localMethodHandle2.asType(localMethodHandle2.type().changeReturnType(Object.class));
      arrayOfMethodHandle[i] = localMethodHandle2.asSpreader(Object[].class, ((MethodType)localObject4).parameterCount());
    }
    final InvocationHandler local1 = new InvocationHandler()
    {
      private Object getArg(String paramAnonymousString)
      {
        if (paramAnonymousString == "getWrapperInstanceTarget") {
          return val$target;
        }
        if (paramAnonymousString == "getWrapperInstanceType") {
          return paramClass;
        }
        throw new AssertionError();
      }
      
      public Object invoke(Object paramAnonymousObject, Method paramAnonymousMethod, Object[] paramAnonymousArrayOfObject)
        throws Throwable
      {
        for (int i = 0; i < localObject2.length; i++) {
          if (paramAnonymousMethod.equals(localObject2[i])) {
            return arrayOfMethodHandle[i].invokeExact(paramAnonymousArrayOfObject);
          }
        }
        if (paramAnonymousMethod.getDeclaringClass() == WrapperInstance.class) {
          return getArg(paramAnonymousMethod.getName());
        }
        if (MethodHandleProxies.isObjectMethod(paramAnonymousMethod)) {
          return MethodHandleProxies.callObjectMethod(paramAnonymousObject, paramAnonymousMethod, paramAnonymousArrayOfObject);
        }
        throw MethodHandleStatics.newInternalError("bad proxy method: " + paramAnonymousMethod);
      }
    };
    if (System.getSecurityManager() != null)
    {
      localObject4 = localObject1;
      localObject3 = AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          return Proxy.newProxyInstance(val$loader, new Class[] { paramClass, WrapperInstance.class }, local1);
        }
      });
    }
    else
    {
      localObject3 = Proxy.newProxyInstance((ClassLoader)localObject1, new Class[] { paramClass, WrapperInstance.class }, local1);
    }
    return (T)paramClass.cast(localObject3);
  }
  
  private static MethodHandle bindCaller(MethodHandle paramMethodHandle, Class<?> paramClass)
  {
    MethodHandle localMethodHandle = MethodHandleImpl.bindCaller(paramMethodHandle, paramClass);
    if (paramMethodHandle.isVarargsCollector())
    {
      MethodType localMethodType = localMethodHandle.type();
      int i = localMethodType.parameterCount();
      return localMethodHandle.asVarargsCollector(localMethodType.parameterType(i - 1));
    }
    return localMethodHandle;
  }
  
  public static boolean isWrapperInstance(Object paramObject)
  {
    return paramObject instanceof WrapperInstance;
  }
  
  private static WrapperInstance asWrapperInstance(Object paramObject)
  {
    try
    {
      if (paramObject != null) {
        return (WrapperInstance)paramObject;
      }
    }
    catch (ClassCastException localClassCastException) {}
    throw MethodHandleStatics.newIllegalArgumentException("not a wrapper instance");
  }
  
  public static MethodHandle wrapperInstanceTarget(Object paramObject)
  {
    return asWrapperInstance(paramObject).getWrapperInstanceTarget();
  }
  
  public static Class<?> wrapperInstanceType(Object paramObject)
  {
    return asWrapperInstance(paramObject).getWrapperInstanceType();
  }
  
  private static boolean isObjectMethod(Method paramMethod)
  {
    switch (paramMethod.getName())
    {
    case "toString": 
      return (paramMethod.getReturnType() == String.class) && (paramMethod.getParameterTypes().length == 0);
    case "hashCode": 
      return (paramMethod.getReturnType() == Integer.TYPE) && (paramMethod.getParameterTypes().length == 0);
    case "equals": 
      return (paramMethod.getReturnType() == Boolean.TYPE) && (paramMethod.getParameterTypes().length == 1) && (paramMethod.getParameterTypes()[0] == Object.class);
    }
    return false;
  }
  
  private static Object callObjectMethod(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
  {
    assert (isObjectMethod(paramMethod)) : paramMethod;
    switch (paramMethod.getName())
    {
    case "toString": 
      return paramObject.getClass().getName() + "@" + Integer.toHexString(paramObject.hashCode());
    case "hashCode": 
      return Integer.valueOf(System.identityHashCode(paramObject));
    case "equals": 
      return Boolean.valueOf(paramObject == paramArrayOfObject[0]);
    }
    return null;
  }
  
  private static Method[] getSingleNameMethods(Class<?> paramClass)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject = null;
    for (Method localMethod : paramClass.getMethods()) {
      if ((!isObjectMethod(localMethod)) && (Modifier.isAbstract(localMethod.getModifiers())))
      {
        String str = localMethod.getName();
        if (localObject == null) {
          localObject = str;
        } else if (!((String)localObject).equals(str)) {
          return null;
        }
        localArrayList.add(localMethod);
      }
    }
    if (localObject == null) {
      return null;
    }
    return (Method[])localArrayList.toArray(new Method[localArrayList.size()]);
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\lang\invoke\MethodHandleProxies.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */