package sun.tracing;

import com.sun.tracing.Probe;
import com.sun.tracing.Provider;
import com.sun.tracing.ProviderName;
import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.HashMap;

public abstract class ProviderSkeleton
  implements InvocationHandler, Provider
{
  protected boolean active = false;
  protected Class<? extends Provider> providerType;
  protected HashMap<Method, ProbeSkeleton> probes;
  
  protected abstract ProbeSkeleton createProbe(Method paramMethod);
  
  protected ProviderSkeleton(Class<? extends Provider> paramClass)
  {
    providerType = paramClass;
    probes = new HashMap();
  }
  
  public void init()
  {
    Method[] arrayOfMethod1 = (Method[])AccessController.doPrivileged(new PrivilegedAction()
    {
      public Method[] run()
      {
        return providerType.getDeclaredMethods();
      }
    });
    for (Method localMethod : arrayOfMethod1)
    {
      if (localMethod.getReturnType() != Void.TYPE) {
        throw new IllegalArgumentException("Return value of method is not void");
      }
      probes.put(localMethod, createProbe(localMethod));
    }
    active = true;
  }
  
  public <T extends Provider> T newProxyInstance()
  {
    final ProviderSkeleton localProviderSkeleton = this;
    (Provider)AccessController.doPrivileged(new PrivilegedAction()
    {
      public T run()
      {
        return (Provider)Proxy.newProxyInstance(providerType.getClassLoader(), new Class[] { providerType }, localProviderSkeleton);
      }
    });
  }
  
  public Object invoke(Object paramObject, Method paramMethod, Object[] paramArrayOfObject)
  {
    Class localClass = paramMethod.getDeclaringClass();
    if (localClass != providerType) {
      try
      {
        if ((localClass == Provider.class) || (localClass == Object.class)) {
          return paramMethod.invoke(this, paramArrayOfObject);
        }
        throw new SecurityException();
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
    triggerProbe(paramMethod, paramArrayOfObject);
    return null;
  }
  
  public Probe getProbe(Method paramMethod)
  {
    return active ? (Probe)probes.get(paramMethod) : null;
  }
  
  public void dispose()
  {
    active = false;
    probes.clear();
  }
  
  protected String getProviderName()
  {
    return getAnnotationString(providerType, ProviderName.class, providerType.getSimpleName());
  }
  
  protected static String getAnnotationString(AnnotatedElement paramAnnotatedElement, Class<? extends Annotation> paramClass, String paramString)
  {
    String str = (String)getAnnotationValue(paramAnnotatedElement, paramClass, "value", paramString);
    return str.isEmpty() ? paramString : str;
  }
  
  protected static Object getAnnotationValue(AnnotatedElement paramAnnotatedElement, Class<? extends Annotation> paramClass, String paramString, Object paramObject)
  {
    Object localObject = paramObject;
    try
    {
      Method localMethod = paramClass.getMethod(paramString, new Class[0]);
      Annotation localAnnotation = paramAnnotatedElement.getAnnotation(paramClass);
      localObject = localMethod.invoke(localAnnotation, new Object[0]);
    }
    catch (NoSuchMethodException localNoSuchMethodException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
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
    catch (NullPointerException localNullPointerException)
    {
      if (!$assertionsDisabled) {
        throw new AssertionError();
      }
    }
    return localObject;
  }
  
  protected void triggerProbe(Method paramMethod, Object[] paramArrayOfObject)
  {
    if (active)
    {
      ProbeSkeleton localProbeSkeleton = (ProbeSkeleton)probes.get(paramMethod);
      if (localProbeSkeleton != null) {
        localProbeSkeleton.uncheckedTrigger(paramArrayOfObject);
      }
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\sun\tracing\ProviderSkeleton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */