package com.sun.naming.internal;

import java.util.List;
import javax.naming.NamingException;

public final class FactoryEnumeration
{
  private List<NamedWeakReference<Object>> factories;
  private int posn = 0;
  private ClassLoader loader;
  
  FactoryEnumeration(List<NamedWeakReference<Object>> paramList, ClassLoader paramClassLoader)
  {
    factories = paramList;
    loader = paramClassLoader;
  }
  
  public Object next()
    throws NamingException
  {
    synchronized (factories)
    {
      NamedWeakReference localNamedWeakReference = (NamedWeakReference)factories.get(posn++);
      Object localObject1 = localNamedWeakReference.get();
      if ((localObject1 != null) && (!(localObject1 instanceof Class))) {
        return localObject1;
      }
      String str = localNamedWeakReference.getName();
      try
      {
        if (localObject1 == null)
        {
          Class localClass = Class.forName(str, true, loader);
          localObject1 = localClass;
        }
        localObject1 = ((Class)localObject1).newInstance();
        localNamedWeakReference = new NamedWeakReference(localObject1, str);
        factories.set(posn - 1, localNamedWeakReference);
        return localObject1;
      }
      catch (ClassNotFoundException localClassNotFoundException)
      {
        localNamingException = new NamingException("No longer able to load " + str);
        localNamingException.setRootCause(localClassNotFoundException);
        throw localNamingException;
      }
      catch (InstantiationException localInstantiationException)
      {
        localNamingException = new NamingException("Cannot instantiate " + localObject1);
        localNamingException.setRootCause(localInstantiationException);
        throw localNamingException;
      }
      catch (IllegalAccessException localIllegalAccessException)
      {
        NamingException localNamingException = new NamingException("Cannot access " + localObject1);
        localNamingException.setRootCause(localIllegalAccessException);
        throw localNamingException;
      }
    }
  }
  
  public boolean hasMore()
  {
    synchronized (factories)
    {
      return posn < factories.size();
    }
  }
}


/* Location:              C:\Program Files (x86)\Java\jre1.8.0_151\lib\rt.jar!\com\sun\naming\internal\FactoryEnumeration.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       0.7.1
 */