package com.sun.xml.internal.ws.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.Resource;
import javax.xml.ws.WebServiceException;

public abstract class InjectionPlan<T, R>
{
  public InjectionPlan() {}
  
  public abstract void inject(T paramT, R paramR);
  
  public void inject(T paramT, Callable<R> paramCallable)
  {
    try
    {
      inject(paramT, paramCallable.call());
    }
    catch (Exception localException)
    {
      throw new WebServiceException(localException);
    }
  }
  
  private static void invokeMethod(Method paramMethod, final Object paramObject, final Object... paramVarArgs)
  {
    if (paramMethod == null) {
      return;
    }
    AccessController.doPrivileged(new PrivilegedAction()
    {
      public Void run()
      {
        try
        {
          if (!val$method.isAccessible()) {
            val$method.setAccessible(true);
          }
          val$method.invoke(paramObject, paramVarArgs);
        }
        catch (IllegalAccessException localIllegalAccessException)
        {
          throw new WebServiceException(localIllegalAccessException);
        }
        catch (InvocationTargetException localInvocationTargetException)
        {
          throw new WebServiceException(localInvocationTargetException);
        }
        return null;
      }
    });
  }
  
  public static <T, R> InjectionPlan<T, R> buildInjectionPlan(Class<? extends T> paramClass, Class<R> paramClass1, boolean paramBoolean)
  {
    ArrayList localArrayList = new ArrayList();
    Object localObject3;
    Resource localResource;
    for (Object localObject1 = paramClass; localObject1 != Object.class; localObject1 = ((Class)localObject1).getSuperclass()) {
      for (localObject3 : ((Class)localObject1).getDeclaredFields())
      {
        localResource = (Resource)((Field)localObject3).getAnnotation(Resource.class);
        if ((localResource != null) && (isInjectionPoint(localResource, ((Field)localObject3).getType(), "Incorrect type for field" + ((Field)localObject3).getName(), paramClass1)))
        {
          if ((paramBoolean) && (!Modifier.isStatic(((Field)localObject3).getModifiers()))) {
            throw new WebServiceException("Static resource " + paramClass1 + " cannot be injected to non-static " + localObject3);
          }
          localArrayList.add(new FieldInjectionPlan((Field)localObject3));
        }
      }
    }
    for (localObject1 = paramClass; localObject1 != Object.class; localObject1 = ((Class)localObject1).getSuperclass()) {
      for (localObject3 : ((Class)localObject1).getDeclaredMethods())
      {
        localResource = (Resource)((Method)localObject3).getAnnotation(Resource.class);
        if (localResource != null)
        {
          Class[] arrayOfClass = ((Method)localObject3).getParameterTypes();
          if (arrayOfClass.length != 1) {
            throw new WebServiceException("Incorrect no of arguments for method " + localObject3);
          }
          if (isInjectionPoint(localResource, arrayOfClass[0], "Incorrect argument types for method" + ((Method)localObject3).getName(), paramClass1))
          {
            if ((paramBoolean) && (!Modifier.isStatic(((Method)localObject3).getModifiers()))) {
              throw new WebServiceException("Static resource " + paramClass1 + " cannot be injected to non-static " + localObject3);
            }
            localArrayList.add(new MethodInjectionPlan((Method)localObject3));
          }
        }
      }
    }
    return new Compositor(localArrayList);
  }
  
  private static boolean isInjectionPoint(Resource paramResource, Class paramClass1, String paramString, Class paramClass2)
  {
    Class localClass = paramResource.type();
    if (localClass.equals(Object.class)) {
      return paramClass1.equals(paramClass2);
    }
    if (localClass.equals(paramClass2))
    {
      if (paramClass1.isAssignableFrom(paramClass2)) {
        return true;
      }
      throw new WebServiceException(paramString);
    }
    return false;
  }
  
  private static class Compositor<T, R>
    extends InjectionPlan<T, R>
  {
    private final Collection<InjectionPlan<T, R>> children;
    
    public Compositor(Collection<InjectionPlan<T, R>> paramCollection)
    {
      children = paramCollection;
    }
    
    public void inject(T paramT, R paramR)
    {
      Iterator localIterator = children.iterator();
      while (localIterator.hasNext())
      {
        InjectionPlan localInjectionPlan = (InjectionPlan)localIterator.next();
        localInjectionPlan.inject(paramT, paramR);
      }
    }
    
    public void inject(T paramT, Callable<R> paramCallable)
    {
      if (!children.isEmpty()) {
        super.inject(paramT, paramCallable);
      }
    }
  }
  
  public static class FieldInjectionPlan<T, R>
    extends InjectionPlan<T, R>
  {
    private final Field field;
    
    public FieldInjectionPlan(Field paramField)
    {
      field = paramField;
    }
    
    public void inject(final T paramT, final R paramR)
    {
      AccessController.doPrivileged(new PrivilegedAction()
      {
        public Object run()
        {
          try
          {
            if (!field.isAccessible()) {
              field.setAccessible(true);
            }
            field.set(paramT, paramR);
            return null;
          }
          catch (IllegalAccessException localIllegalAccessException)
          {
            throw new WebServiceException(localIllegalAccessException);
          }
        }
      });
    }
  }
  
  public static class MethodInjectionPlan<T, R>
    extends InjectionPlan<T, R>
  {
    private final Method method;
    
    public MethodInjectionPlan(Method paramMethod)
    {
      method = paramMethod;
    }
    
    public void inject(T paramT, R paramR)
    {
      InjectionPlan.invokeMethod(method, paramT, new Object[] { paramR });
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\xml\internal\ws\util\InjectionPlan.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */