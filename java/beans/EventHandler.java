package java.beans;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import sun.reflect.misc.MethodUtil;
import sun.reflect.misc.ReflectUtil;

public class EventHandler
  implements InvocationHandler
{
  private Object target;
  private String action;
  private final String eventPropertyName;
  private final String listenerMethodName;
  private final AccessControlContext acc = AccessController.getContext();
  
  @ConstructorProperties({"target", "action", "eventPropertyName", "listenerMethodName"})
  public EventHandler(Object paramObject, String paramString1, String paramString2, String paramString3)
  {
    target = paramObject;
    action = paramString1;
    if (paramObject == null) {
      throw new NullPointerException("target must be non-null");
    }
    if (paramString1 == null) {
      throw new NullPointerException("action must be non-null");
    }
    eventPropertyName = paramString2;
    listenerMethodName = paramString3;
  }
  
  public Object getTarget()
  {
    return target;
  }
  
  public String getAction()
  {
    return action;
  }
  
  public String getEventPropertyName()
  {
    return eventPropertyName;
  }
  
  public String getListenerMethodName()
  {
    return listenerMethodName;
  }
  
  private Object applyGetters(Object paramObject, String paramString)
  {
    if ((paramString == null) || (paramString.equals(""))) {
      return paramObject;
    }
    int i = paramString.indexOf('.');
    if (i == -1) {
      i = paramString.length();
    }
    String str1 = paramString.substring(0, i);
    String str2 = paramString.substring(Math.min(i + 1, paramString.length()));
    try
    {
      Method localMethod = null;
      if (paramObject != null)
      {
        localMethod = Statement.getMethod(paramObject.getClass(), "get" + NameGenerator.capitalize(str1), new Class[0]);
        if (localMethod == null) {
          localMethod = Statement.getMethod(paramObject.getClass(), "is" + NameGenerator.capitalize(str1), new Class[0]);
        }
        if (localMethod == null) {
          localMethod = Statement.getMethod(paramObject.getClass(), str1, new Class[0]);
        }
      }
      if (localMethod == null) {
        throw new RuntimeException("No method called: " + str1 + " defined on " + paramObject);
      }
      Object localObject = MethodUtil.invoke(localMethod, paramObject, new Object[0]);
      return applyGetters(localObject, str2);
    }
    catch (Exception localException)
    {
      throw new RuntimeException("Failed to call method: " + str1 + " on " + paramObject, localException);
    }
  }
  
  public Object invoke(final Object paramObject, final Method paramMethod, final Object[] paramArrayOfObject)
  {
    AccessControlContext localAccessControlContext = acc;
    if ((localAccessControlContext == null) && (System.getSecurityManager() != null)) {
      throw new SecurityException("AccessControlContext is not set");
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Object run()
      {
        return EventHandler.this.invokeInternal(paramObject, paramMethod, paramArrayOfObject);
      }
    }, localAccessControlContext);
  }
  
  private Object invokeInternal(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
  {
    String str1 = paramMethod.getName();
    if (paramMethod.getDeclaringClass() == Object.class)
    {
      if (str1.equals("hashCode")) {
        return new Integer(System.identityHashCode(paramObject));
      }
      if (str1.equals("equals")) {
        return paramObject == paramArrayOfObject[0] ? Boolean.TRUE : Boolean.FALSE;
      }
      if (str1.equals("toString")) {
        return paramObject.getClass().getName() + '@' + Integer.toHexString(paramObject.hashCode());
      }
    }
    if ((listenerMethodName == null) || (listenerMethodName.equals(str1)))
    {
      Class[] arrayOfClass = null;
      Object[] arrayOfObject = null;
      if (eventPropertyName == null)
      {
        arrayOfObject = new Object[0];
        arrayOfClass = new Class[0];
      }
      else
      {
        Object localObject1 = applyGetters(paramArrayOfObject[0], getEventPropertyName());
        arrayOfObject = new Object[] { localObject1 };
        arrayOfClass = new Class[] { localObject1 == null ? null : localObject1.getClass() };
      }
      try
      {
        int i = action.lastIndexOf('.');
        if (i != -1)
        {
          target = applyGetters(target, action.substring(0, i));
          action = action.substring(i + 1);
        }
        localObject2 = Statement.getMethod(target.getClass(), action, arrayOfClass);
        if (localObject2 == null) {
          localObject2 = Statement.getMethod(target.getClass(), "set" + NameGenerator.capitalize(action), arrayOfClass);
        }
        if (localObject2 == null)
        {
          String str2 = " with argument " + arrayOfClass[0];
          throw new RuntimeException("No method called " + action + " on " + target.getClass() + str2);
        }
        return MethodUtil.invoke((Method)localObject2, target, arrayOfObject);
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        throw new RuntimeException(localIllegalAccessException);
      }
      catch (InvocationTargetException localInvocationTargetException)
      {
        Object localObject2 = localInvocationTargetException.getTargetException();
        throw ((localObject2 instanceof RuntimeException) ? (RuntimeException)localObject2 : new RuntimeException((Throwable)localObject2));
      }
    }
    return null;
  }
  
  public static <T> T create(Class<T> paramClass, Object paramObject, String paramString)
  {
    return (T)create(paramClass, paramObject, paramString, null, null);
  }
  
  public static <T> T create(Class<T> paramClass, Object paramObject, String paramString1, String paramString2)
  {
    return (T)create(paramClass, paramObject, paramString1, paramString2, null);
  }
  
  public static <T> T create(Class<T> paramClass, Object paramObject, String paramString1, String paramString2, String paramString3)
  {
    final EventHandler localEventHandler = new EventHandler(paramObject, paramString1, paramString2, paramString3);
    if (paramClass == null) {
      throw new NullPointerException("listenerInterface must be non-null");
    }
    ClassLoader localClassLoader = getClassLoader(paramClass);
    final Class[] arrayOfClass = { paramClass };
    (T)AccessController.doPrivileged(new PrivilegedAction()
    {
      public T run()
      {
        return (T)Proxy.newProxyInstance(val$loader, arrayOfClass, localEventHandler);
      }
    });
  }
  
  private static ClassLoader getClassLoader(Class<?> paramClass)
  {
    ReflectUtil.checkPackageAccess(paramClass);
    ClassLoader localClassLoader = paramClass.getClassLoader();
    if (localClassLoader == null)
    {
      localClassLoader = Thread.currentThread().getContextClassLoader();
      if (localClassLoader == null) {
        localClassLoader = ClassLoader.getSystemClassLoader();
      }
    }
    return localClassLoader;
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\java\beans\EventHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */